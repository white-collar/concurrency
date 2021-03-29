package fork.join;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class UsingForkJoin {

    public ForkJoinPool getCommonPool() {
        return ForkJoinPool.commonPool();
    }

    public ForkJoinPool customForkJoinPool(int parallelism,
                                           ForkJoinPool.ForkJoinWorkerThreadFactory factory,
                                           Thread.UncaughtExceptionHandler handler,
                                           boolean asyncMode) {
        return new ForkJoinPool(parallelism, factory, handler, asyncMode);
    }

    static class RecSumTask extends RecursiveTask<BigInteger> {

        private static final long serialVersionUID = 1L;
        public static final int DIVIDE_AT = 500;

        private List<Integer> numbers;

        public RecSumTask(List<Integer> numbers) {
            this.numbers = numbers;
        }

        @Override
        protected BigInteger compute() {
            var subTasks = new LinkedList<RecSumTask>();
            if (numbers.size() < DIVIDE_AT) {
                // directly
                var subSum = BigInteger.ZERO;
                for (Integer number : numbers) {
                    subSum = subSum.add(BigInteger.valueOf(number));
                }
                return subSum;
            } else {
                // Divide to conquer
                var size = numbers.size();
                var numbersLeft = numbers.subList(0, size / 2);
                var numbersRight = numbers.subList(size / 2, size);

                var recSumLeft = new RecSumTask(numbersLeft);
                var recSumRight = new RecSumTask(numbersRight);

                subTasks.add(recSumRight);
                subTasks.add(recSumLeft);

                // Fork Child Tasks
                recSumLeft.fork();
                recSumRight.fork();
            }

            var sum = BigInteger.ZERO;
            for (var recSum : subTasks) {
                // Join Child Tasks
                sum = sum.add(recSum.join());
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        // prepares dataset for the example
        var numbers = new LinkedList<Integer>();
        for (int i = 0; i < 500_000; i++) {
            numbers.add(i);
        }

        // Usage
        var commonPool = ForkJoinPool.commonPool();
        var task = new RecSumTask(numbers);
        BigInteger result = commonPool.invoke(task);
        System.out.println("Result is: " + result);
        System.out.println("\n\n");
    }


}
