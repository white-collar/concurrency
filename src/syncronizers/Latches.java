package syncronizers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Специальное логика, реализующая поведение ожидания потоков, которые уже
 * созданы, но еще не запущены и ждут наступление определенного события - пока число
 * letch не станет равно нулю.
 *
 * Мы создаем latch - в примере оно равно трём.
 * Код потока, заинтересованный в том, чтобы начать выполняться, вызывает метод countDown(),
 * который уменьшает число latch на 1.
 *
 * Далее каждый из созданных потоков запускается на выполнение, но ... в реальности ни один не стартует,
 * потому что объект CountDownLatch при помощи метода await приостанавливает их выполнение до тех пор,
 * пока счетчик latch не будет равен нулю. Метод await ждет 2 секунды, фактически давая каждому потоку успеть
 * выполниться, уменьшить счетчик и, таким образом, когда счетчик полностью обнулится, весь пул потоков
 * будет действительно запущен на выполнение.
 */

public class Latches {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(3);
        Runnable r = () -> {
            try {
                Thread.sleep(1000);
                System.out.println("Service in " + Thread.currentThread().getName() + " initialized.");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        executor.execute(r);
        executor.execute(r);
        executor.execute(r);
        try {
            latch.await(2, TimeUnit.SECONDS);
            System.out.println("All services up and running!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
