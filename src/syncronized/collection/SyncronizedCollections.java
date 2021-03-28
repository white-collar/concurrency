package syncronized.collection;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Особенностью синхронизированных коллекций является то, что все их методы
 * API уже синхронизированы, как заявлено платформой. Таким образом, их можно
 * использовать безопасно с несколькими потоками - коллекция всегда будет согласованной.
 *
 * Но!
 *
 * Если используются составные операции, требующие двух последовательных вызовов с прочтением
 * состояния коллекции - получить размер и добавить новый элемент, то такие операции разработчик должен
 * поддерживать самостоятельно.
 */
public class SyncronizedCollections {

    /**
     * Безопасный метод, несмотря на то, что в теле метода используются два вызова.
     * @param list
     * @param value
     */
    public static void insertIfAbsent(Vector<Long> list, Long value) {
        synchronized (list) {
            var contains = list.contains(value);
            if (!contains) {
                list.add(value);
                System.out.println("Value added: " + value);
            }
        }
    }


    /**
     * Небезопасный метод, потому что последовательные операции над коллекцией не синхронизированы.
     * @param list
     * @param value
     */
    public static void insertIfAbsentUnsafe(Vector<Long> list, Long value) {
        var contains = list.contains(value);
        if (!contains) {
            list.add(value);
            System.out.println("Value added: " + value);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        Vector vector = new Vector<Long>();

        Runnable insertIfAbsent = () -> {
            long millis = System.currentTimeMillis() / 1000;
            insertIfAbsent(vector, millis);
        };
        for (int i = 0; i < 10001; i++) {
            executor.execute(insertIfAbsent);
        }
        executor.shutdown();
        executor.awaitTermination(4000, TimeUnit.SECONDS);

    }

}
