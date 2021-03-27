package threads;

public class ThreadsOperator {
    public static void main(String[] args) throws InterruptedException {
        var created = new Thread();
        created.start();

        Thread threadWithTask = new Thread(
                () -> System.out.println("Inside thread" + Thread.currentThread().getName())
        );
        threadWithTask.start();

        Runnable interruptibleTask = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Im not interrupted " + Thread.currentThread().getName());
            }
        };

        var interruptible = new Thread(interruptibleTask);
        interruptible.start();
        Thread.sleep(3000);
        interruptible.interrupt();
    }
}
