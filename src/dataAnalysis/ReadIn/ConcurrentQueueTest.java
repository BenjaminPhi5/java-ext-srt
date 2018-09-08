package dataAnalysis.ReadIn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentQueueTest {

    private ConcurrentLinkedQueue<byte[]> q;
    private String filename;

    private class Producer extends Thread {
        private int sent = 0;
        FileInputStream fis;
        Producer() {
            try {
                fis = new FileInputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                byte buf[] = new byte[1<<16];
                int n;
                while ((n = fis.read(buf)) != -1) {
                    q.offer(buf);
                    sent++;
                    buf = new byte[1<<16];
                }
                fis.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        public int numberProduced() {
            return sent;
        }
    }

    private class Consumer extends Thread {
        private int recv = 0;
        int local_max = 0, local_min = Integer.MAX_VALUE, local_total = 0;

        public void run() {
            byte[] r;
            int x;
            while ((r = q.poll()) == null || ! (new String(r).equals("EOF"))) {
                if (r != null) {
                    // this bit gets the int and checks the values

                    byte[] buf = r;
                    local_total += buf.length/4;
                    for(int i = 0; i <  buf.length/ 4; i++){
                        x  = (((int) buf[4*i+3]) & 255)
                                |   ((((int) buf[4*i+2]) & 255) << 8)
                                |   ((((int) buf[4*i+1]) & 255) << 16)
                                |   ((((int) buf[4*i]) & 255) << 24);
                        if(x < local_min)
                            local_min = x;
                        if(x > local_max)
                            local_max = x;
                    }

                    /*
                    try {
                        IntBuffer wrapped = ByteBuffer.wrap(r).asIntBuffer();
                        while (wrapped.hasRemaining()){
                            local_total++;
                            x = wrapped.get();
                            if(x < local_min)
                                local_min = x;
                            if(x > local_max)
                                local_max = x;
                        }
                    } catch (UnsupportedOperationException e) {// do nothing
                }
                */
                    recv++;
                } else {
                    Thread.yield();
                }
            }
            q.offer(("EOF").getBytes());
        }

        public int numberConsumed() {
            return recv;
        }
    }

    private Consumer[] consumers;
    private Producer[] producers;
    public int smallest;
    public int biggest;
    public int total;

    public ConcurrentQueueTest(int c, int p, String filename) {
        smallest = Integer.MAX_VALUE;
        biggest = 0;
        total = 0;
        this.filename = filename;
        this.q = new ConcurrentLinkedQueue<>();
        consumers = new Consumer[c];
        for (int i = 0; i < c; ++i) {
            consumers[i] = new Consumer();
        }
        producers = new Producer[p];
        for (int i = 0; i < p; ++i) {
            producers[i] = new Producer();
        }
    }

    public boolean run() {

        for (Consumer c : consumers) {
            c.start();
        }
        for (Producer p : producers) {
            p.start();
        }
        for (Producer p : producers) {
            try {
                p.join();
            } catch (InterruptedException e) {
            }
        }
        q.offer("EOF".getBytes());
        for (Consumer c : consumers) {
            try {
                total += c.local_total;
                if(c.local_max > biggest)
                    biggest = c.local_max;
                if(c.local_min < smallest)
                    smallest = c.local_min;

                c.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int recv = Arrays.stream(consumers).mapToInt(Consumer::numberConsumed).sum();
        int sent = Arrays.stream(producers).mapToInt(Producer::numberProduced).sum();

        System.out.println("Recv: " + recv + "\nSent: " + sent+"\n-----");
        System.out.printf("%s (%d,%d):\t%s%n",
                q.getClass().getSimpleName(),
                consumers.length,
                producers.length,
                recv == sent ? "PASS" : "FAIL");
        return recv == sent;
    }
}