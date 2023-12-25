package queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CustomNonBlockingQueue<E> {

    public final ConcurrentLinkedQueue<E> customQueue;

    public CustomNonBlockingQueue() {
        this.customQueue = new ConcurrentLinkedQueue<E>();
    }
}
