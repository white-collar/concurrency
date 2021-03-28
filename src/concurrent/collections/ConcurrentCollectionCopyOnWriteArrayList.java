package concurrent.collections;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Замена синхронизированному списку.
 *
 * Каждый раз, когда производится запись, создается новая копия списка.
 *
 * Таким образом, эту коллекцию используют,когда чтение превалирует над операциями записи.
 */
public class ConcurrentCollectionCopyOnWriteArrayList {

    public static void usingCopyOnWriteArrayList() {
        System.out.println("=== CopyOnWriteArrayList ===");
        ExecutorService executor = Executors.newCachedThreadPool();
        Random random = new Random();

        var copyOnWriteArrayList = new CopyOnWriteArrayList<Integer>();

        for (int i = 0; i < 100; i++) {
            if (i % 8 == 0) {
                // write
                executor.execute(() -> {
                    var value = random.nextInt(10);
                    System.err.println("Added " + value);
                    copyOnWriteArrayList.add(value);
                });
            } else {
                // read
                executor.execute(() -> {
                    var builder = new StringBuilder();
                    for (var value : copyOnWriteArrayList) {
                        builder.append(value + " ");
                    }
                    System.out.println("Reading " + builder.toString());
                });
            }
        }

        executor.shutdown();

    }

    public static void main(String[] args) {
        usingCopyOnWriteArrayList();
    }

}
