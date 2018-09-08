package threadingTest;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class NoLockConcurrentQueue<T> implements ConcurrentQueue<T> {

    ConcurrentLinkedQueue queue;

    public NoLockConcurrentQueue(){
        queue = new ConcurrentLinkedQueue<T>();
    }

    @Override
    public void offer(T message) {
        queue.offer(message);
    }

    @Override
    public T poll() {
        return (T)queue.poll();
    }


}