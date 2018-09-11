package dataAnalysis.ReadIn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentQueueTest {

    private ConcurrentLinkedQueue<Data> q;
    private String filename;

    private class Data{
        public byte[] buffer;
        public int total;

        Data(byte[] buffer, int total){
            this.buffer = buffer;
            this.total = total;
        }
    }

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
                    // adding ints read to the front
                    q.offer(new Data(buf, n/4));
                    //System.out.println("total at read:" + n/4);
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
        int local_max;
        int local_min;
        int local_total;

        public void run() {
            Data r;
            int x;
            while ((r = q.poll()) == null || ! ((new String(r.buffer)).equals("EOF"))) {
                if (r != null) {
                    local_max = 0; local_min = Integer.MAX_VALUE; local_total = 0;
                    // this bit gets the int and checks the values

                    byte[] buf = r.buffer;
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
                        IntBuffer wrapped = ByteBuffer.wrap(r.buffer).asIntBuffer();
                        while (r.total > 0){
                            r.total--;
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
                    readWriteLock.writeLock().lock();
                    {
                        total += local_total;
                        if(local_max > biggest)
                            biggest = local_max;
                        if(local_min < smallest)
                            smallest = local_min;
                        //System.out.println("local total: " + local_total);
                    }
                    readWriteLock.writeLock().unlock();

                    recv++;


                } else {
                    Thread.yield();
                }
            }
            q.offer(new Data(("EOF").getBytes(), 0));
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
    ReadWriteLock readWriteLock;

    public ConcurrentQueueTest(int c, int p, String filename) {
        readWriteLock = new ReentrantReadWriteLock();
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
        q.offer(new Data("EOF".getBytes(), 0));
        for (Consumer c : consumers) {
            try {
                c.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //int recv = Arrays.stream(consumers).mapToInt(Consumer::numberConsumed).sum();
        //int sent = Arrays.stream(producers).mapToInt(Producer::numberProduced).sum();
        /*
        System.out.println("Recv: " + recv + "\nSent: " + sent+"\n-----");
        System.out.printf("%s (%d,%d):\t%s%n",
                q.getClass().getSimpleName(),
                consumers.length,
                producers.length,
                recv == sent ? "PASS" : "FAIL");
        return recv == sent;
        */

        System.out.println("smallest: " + smallest +"\t");
        System.out.print("biggest: " + biggest+"\t");
        System.out.print("total: " + total + "\n");
        return true;
    }
}