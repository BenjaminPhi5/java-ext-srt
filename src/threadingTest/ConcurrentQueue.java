package threadingTest;

public interface ConcurrentQueue<T> {
    /**
     * Add message to queue.
     */
    public void offer(T message);

    /**
     * Return the first item from the queue or null if the queue is empty.
     */
    public T poll();
}
