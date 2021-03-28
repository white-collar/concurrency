package syncronizers;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Барьеры реализуют логику - логика выполнения потока будет отложена до тех пор,
 * пока не соберется достаточное число потоков. Это "достаточное" число определяется в
 * конструкторе Barrier.
 */
public class Barrier {
    public static void main(String[] args) {
        Runnable barrierAction = () -> System.out.println("Well done, guys!");

        ExecutorService executor = Executors.newCachedThreadPool();
        CyclicBarrier barrier = new CyclicBarrier(10, barrierAction);

        Runnable task = () -> {
            try {
                System.out.println("Doing task for " + Thread.currentThread().getName());
                Thread.sleep(new Random().nextInt(10) * 100);
                System.out.println("Done for " + Thread.currentThread().getName());
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 10; i++) {
            executor.execute(task);
        }
        executor.shutdown();

    }
}
