package locks;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExplicitReadWriteLock {

    /**
     * Специальный вид лока, предоставляющий более гибкую стратегию для чтения
     * данных - все потоки могут читать синхронизированные данные, но только один
     * поток может выполнять запись.
     *
     * Чтение разрешено, если нет ни одного потока, осуществляющего запись.
     *
     * Запись разрешена, если нет потоков, выполняющих чтение.
     */
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private String myContent = "A long default content......";

    public String showContent() {
        ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
        readLock.lock();
        try {
            System.out.println("Reading state while holding a lock.");
            return myContent;
        } finally {
            readLock.unlock();
        }
    }

    public void writeContent(String newContentToAppend) {
        ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            System.err.println("Writing " + newContentToAppend);
            myContent = new StringBuilder().append(myContent).append(newContentToAppend).toString();
        } finally {
            writeLock.unlock();
        }
    }

    public static void main(String[] args) {
        var executor = Executors.newCachedThreadPool();
        ExplicitReadWriteLock self = new ExplicitReadWriteLock();

        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(new Random().nextInt(10) * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(self.showContent());
            });
        }


        for (int i = 0; i < 5; i++) {
            executor.execute(() -> self.writeContent(UUID.randomUUID().toString()));
        }
        executor.shutdown();
    }
}
