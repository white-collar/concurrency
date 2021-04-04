package concurrent.collections;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentCollectionHashMap {

    /**
     * В concurrent коллекциях есть некоторые ослабления в механизме синхронизации,
     * чтобы увеличить производительность.
     *
     * Используется специальный механизм Lock Striping.
     *
     * Он заключается в том, что коллекция делится на некие полосы (сегменты), размер которых по умолчанию
     * равен 16. Если вызываются методы коллекции относительно некоторого элемента, то синхронизируется всегда
     * только тот сегмент, в котором этот конкретный элемент находится. Другие сегменты при этом могут быть
     * прочитаны и записаны другим потоком без ограничений.
     *
     * Также методы size() или isEmpty() могут быть ненадежными. Полагаться на их значения нельзя.
     *
     * Синхронизировать последовательные вызовы также нет необходимости.
     *
     */
    public static void usingConcurrentMap(){

        ExecutorService executor = Executors.newCachedThreadPool();
        Random random = new Random();
        ConcurrentHashMap valuesPerUuid = new ConcurrentHashMap<UUID, Integer>();
        // atomic operations
        valuesPerUuid.putIfAbsent(UUID.randomUUID(), random.nextInt(100));

        for (int i = 0; i < 100; i++) {
            if (i % 6 == 0) {
                // write
                executor.execute(() -> {
                    UUID uuid = UUID.randomUUID();
                    Integer value = random.nextInt(10);
                    System.out.println("Added " + uuid + " - " + value);
                    valuesPerUuid.putIfAbsent(uuid, value);
                });
            } else {
                // read
                executor.execute(() -> System.out.println("Printed " + valuesPerUuid.values()));
            }
        }

        executor.shutdown();

    }

    public static void main(String[] args) {
        usingConcurrentMap();
    }
}
