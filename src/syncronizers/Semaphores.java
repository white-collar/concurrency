package syncronizers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Семафоры - специальный механизм, позволяющий более гибко контролировать доступ
 * к разделяемому ресурсу.
 *
 * При создании семафора необходимо указать сколько потоков могут работать с ним - permits.
 * Поток, запросив доступ к семафору, увеличивает счетчик permits.
 * Если приходит новый поток и обнаруживает, что число permits достигло максимума, поток блокируется,
 * пока счетчик не уменьшится.
 *
 *
 * Идеально для ограничения доступа к ресурсам.
 */
public class Semaphores {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Semaphore semaphore = new Semaphore(3);

        Runnable r = () -> {
            try {
                System.out.println("Trying to acquire - " + Thread.currentThread().getName());
                if (semaphore.tryAcquire(2, TimeUnit.SECONDS)) {
                    System.out.println("Acquired - " + Thread.currentThread().getName());
                    Thread.sleep(2000);
                    System.out.println("Done - " + Thread.currentThread().getName());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        };
        for (int i = 0; i < 4; i++) {
            executor.execute(r);
        }

        executor.shutdown();
    }
}
