package locks;

import java.util.concurrent.Executors;

public class IntrinsicLock {
    private boolean state;


    /**
     * Этот метод сихронизирован, на что указывает наличие ключевого слова synchronized.
     * Это означает, что вызов этого метода возможен только из кода потока.
     * При этом, как только поток начинает выполнение этого метода, никакой другой поток
     * не может получить управление, поскольку первый поток "ставит лок" на объект, сигнализируя
     * тем самым, что метод пока что занят. Этот механизм называют intrinsic lock (monitor lock).
     *
     *
     * Если бы синхронизация не была применена и этот метод вызывался бы разными потоками, то
     * не существовало бы никакой гарантии относительно согласованного состояния поля state.
     *
     * Поскольку мы имеем дело с примитивом, то при инициализации класса его начальное значение было
     * бы установлено в значение false, как это определяется стандартом языка.
     *
     * Каждый новый поток в свой момент времени обнаруживал бы состояние поля state в случайном порядке -
     * к примеру, только что созданное, только что измененное на true потоком №34, только что измененное на false
     *  потоком № 76 и т.д.
     *
     * Но поскольку применяется синхронизация, то значения поля state гарантированно будут представлять из себя
     * последовательность false, true, false, true, false, true ....
     *
     * Лок снимается, когда выполнение оформленного им кода заканчивается (даже если произошло исключение).
     * Перед снятием лока задействуется
     * специальный механизм, который гарантирует, что все потоки, ожидающие своей очереди, увидят изменения, созданные
     * предыдущим потоком и, таким образом, значение поля всегда будет согласовано.
     *
     * Конструктор объекта нельзя синхронизировать. Платформа гарантирует, что только тот поток, который создал объект,
     * сможет получить доступ к этому объекту. Таким образом синхронизация конструктора является избыточной операцией и
     * является синтаксической ошибкой.
     */
    public synchronized void mySynchronizedMethod() {
        state = !state;

        System.out.println("My state is:" + state);
    }

    public void mySynchronizedBlock() {
        /**
         * Можно синхронизовать также отдельный блок кода. Его выполнение также возможно
         * только другим потоком. Платформа гарантирует, что строка Who owns my lock:
         * будет выполнена до начала выполнения синхронизированного блока.
         */
        System.out.println("Who owns my lock: " + Thread.currentThread().getName());
        synchronized (this) {
            state = !state;
            System.out.println("Who owns my lock after state changes: " + Thread.currentThread().getName());
            System.out.println("State is: " + state);
            System.out.println("====");
        }
    }

    /**
     * Лок обладает свойством reentrancy. Другими словами, если потом синхронизировал метод,
     * то потом этот же поток получит управление вложенного синхронизированного кода, при этом лок
     * на метод всё еще будет активным и не будет убран до конца выполнения внутреннего синхронизированного блока.
     */
    public synchronized void reentrancy() {
        System.out.println("Before acquiring again");
        synchronized (this) {
            System.out.println("I'm own it! " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var executor = Executors.newCachedThreadPool();
        var self = new IntrinsicLock();
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> self.mySynchronizedMethod());
        }
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> self.mySynchronizedBlock());
        }
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> self.reentrancy());
        }
        executor.shutdown();
    }

}
