package concurrent.collections;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Безопасная очередь.
 *
 * Используется для реализации паттерна produce-consume.
 *
 * Методы put() и take() -  блокирующие.
 *
 * Так что если некоторый поток намерен получить значение очереди, которая еще пуста,
 * то он будет ожидать, пока в очереди не появится какое-либо значение.
 *
 * Классически первым элементом очереди является элемент, который был помещен в нее ранее всего,
 * и последним будет такой, который поместили позднее всего.
 *
 * Идеально когда надо передать безопасно данные из одного потока в другой.
 */
public class ConcurrentCollectionBlockingQueque {

    public static void blockingQueque() {
        System.out.println("=== BlockingQueue ===");

        // Bounded UUID queue
        LinkedBlockingQueue uuidQueue = new LinkedBlockingQueue<UUID>(10);

        System.out.println("Queue will execute for 10s");

        // Multiple consumers
        Runnable runConsumer = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    var uuid = uuidQueue.take();
                    System.out.println("Consumed: " + uuid + " by " + Thread.currentThread().getName());

                } catch (InterruptedException e) {
                    // interrupted pattern
                    // InterruptedException makes isInterrupted returns false
                    Thread.currentThread().interrupt();
                    System.err.println("Consumer Finished");
                }
            }
        };
        var consumer1 = new Thread(runConsumer);
        consumer1.start();
        var consumer2 = new Thread(runConsumer);
        consumer2.start();

        // Producer Thread
        Runnable runProducer = () -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Random r = new Random();
                    // Delay producer
                    Thread.sleep(r.nextInt(1000));
                    UUID randomUUID = UUID.randomUUID();
                    System.out.println("Produced: " + randomUUID + " by " + Thread.currentThread().getName());
                    uuidQueue.put(randomUUID);
                }
            } catch (InterruptedException e) {
                // interrupted pattern
                System.err.println("Producer Finished");
            }
        };

        // Multiple producers - Examples using simple threads this time.
        var producer1 = new Thread(runProducer);
        producer1.start();
        var producer2 = new Thread(runProducer);
        producer2.start();
        var producer3 = new Thread(runProducer);
        producer3.start();

        try {
            // Queue will run for 10secs
            Thread.sleep(10000);
            producer1.interrupt();
            producer2.interrupt();
            producer3.interrupt();
            consumer1.interrupt();
            consumer2.interrupt();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        blockingQueque();
    }
}
