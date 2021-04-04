package patterns.producer.consumer;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ProducerConsumer {

    /**
     * Общий поведенческий паттерн, позволяющий отделить друг от друга две логики.
     * Первую логику условно называют продьюсером, вторую - консьюмером (потребителем).
     *
     * При этом и продьюсер и консьюмер должны находиться в разных потокках для их наиболее
     * эффективной работы.
     */

    private BlockingQueue<String> data = new LinkedBlockingQueue<>();

    private Callable<Void> consumer = () -> {
        while (true) {
            var dataUnit = data.poll(5, TimeUnit.SECONDS);
            if (dataUnit == null)
                break;
            System.out.println("Consumed " + dataUnit + " from " + Thread.currentThread().getName());
        }
        return null;
    };

    private Callable<Void> producer = () -> {
        for (int i = 0; i < 90_000; i++) {
            var dataUnit = UUID.randomUUID().toString();
            data.put(dataUnit);
        }
        return null;
    };

    public void run(long forHowLong, TimeUnit unit) throws InterruptedException {
        var pool = Executors.newCachedThreadPool();
        pool.submit(producer);
        pool.submit(consumer);
        pool.submit(consumer);
        pool.shutdown();
        pool.awaitTermination(forHowLong, unit);
    }

    public static void main(String[] args) {
        var producerConsumer = new ProducerConsumer();
        try {
            producerConsumer.run(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
