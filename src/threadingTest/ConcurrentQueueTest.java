package threadingTest;

import java.util.Arrays;

public class ConcurrentQueueTest {

    private ConcurrentQueue<String> q;

    private class Producer extends Thread {
        private int sent = 0;

        public void run() {
            for (int i = 0; i < 70000; ++i) {
                //for (int i = 0; i < 2; ++i) {
                q.offer("" + i);
                sent++;
            }
        }

        public int numberProduced() {
            return sent;
        }
    }

    private class Consumer extends Thread {
        private int recv = 0;

        public void run() {
            String r;
            while ((r = q.poll()) == null || !r.equals("EOF")) {
                //System.out.println("r is : " + r);
                if (r != null) {
                    recv++;
                    //if((recv/100) % 10 == 1)
                    //  System.out.println("recv: " + recv);
                } else {
                    Thread.yield();
                }
            }
            q.offer("EOF");
        }

        public int numberConsumed() {
            return recv;
        }
    }

    private Consumer[] consumers;
    private Producer[] producers;

    ConcurrentQueueTest(ConcurrentQueue<String> q, int c, int p) {
        this.q = q;
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
        q.offer("EOF");
        for (Consumer c : consumers) {
            try {
                c.join(10000);
            } catch (InterruptedException e) {
                // IGNORED exception
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

    public static void main(String[] args) {

        long time;
        for (int i = 0; i < 10; ++i) {
            time = System.currentTimeMillis();
            if (!new ConcurrentQueueTest(new NoLockConcurrentQueue<String>(),
                    5, 5).run())
                return;
            System.out.println("time one lock: " +(System.currentTimeMillis() - time));

        }
    }
}
