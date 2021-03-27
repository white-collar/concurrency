package locks;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ExplicitReentrantLock {

    /**
     * Это точно такой же механизм, как и intrinsic locks, но с гарантиями, описывающими
     *  сериализацию и видимость объекта для других потоков.
     */
    private ReentrantLock reentrantLock = new ReentrantLock();
    private boolean state;

    /**
     * Простейший способ установки reentrant lock
     */
    public void lockMyHearth() {
        reentrantLock.lock();
        try {
            System.out.println("Changing stated in a serialized way");
            state = !state;
            System.out.println("Changed: " + state);
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * Можно также запросить лок с ограничением по времени.
     * @throws InterruptedException
     */
    public void lockMyHearthWithTiming() throws InterruptedException {

        if (!reentrantLock.tryLock(1l, TimeUnit.SECONDS)) {
            System.err.println("Failed to acquire the lock - it's already held.");
        } else {
            try {
                System.out.println("Simulating a blocking computation - forcing tryLock() to fail");
                Thread.sleep(3000);
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        var executor = Executors.newCachedThreadPool();
        ExplicitReentrantLock self = new ExplicitReentrantLock();
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> self.lockMyHearth());
        }

        for (int i = 0; i < 40; i++) {
            executor.execute(() -> {
                try {
                    self.lockMyHearthWithTiming();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        executor.shutdown();
    }

}
