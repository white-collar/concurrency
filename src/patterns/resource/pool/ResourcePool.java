package patterns.resource.pool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ResourcePool<T> {
    /**
     * Паттерн многопоточного программирования, позволяющий организовать
     * ограниченный доступ потоков к некоторому ограниченному ресурсу.
     *
     * Это может быть, к примеру, пул соединений к базе данных.
     *
     * Таким образом, мы гарантируем, что ни один поток не будет занимать ресурс неограниченно долго.
     */

    private final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private Semaphore semaphore;
    private BlockingQueue<T> resources;

    public ResourcePool(int poolSize, List<T> initializedResources) {
        this.semaphore = new Semaphore(poolSize, true);
        this.resources = new LinkedBlockingQueue<>(poolSize);
        this.resources.addAll(initializedResources);
    }

    public T get() throws InterruptedException {
        return get(Integer.MAX_VALUE);
    }

    public T get(long secondsToTimeout) throws InterruptedException {
        semaphore.acquire();
        try {
            T resource = resources.poll(secondsToTimeout, TIME_UNIT);
            return resource;
        } finally {
            semaphore.release();
        }
    }

    public void release(T resource) throws InterruptedException {
        if (resource != null) {
            resources.put(resource);
            semaphore.release();
        }
    }
}
