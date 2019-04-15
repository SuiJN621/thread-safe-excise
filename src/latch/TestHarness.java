package latch;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author Sui
 * @date 2018.10.30 14:47
 */
public class TestHarness {

    public long timeTasks(int nThreads, Runnable task) throws InterruptedException {

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(nThreads);

        for(int n = 0; n < nThreads; n ++) {
            Thread t = new Thread(() -> {
                try {
                    startLatch.await();
                    try {
                        task.run();
                    } finally {
                        endLatch.countDown();
                    }
                } catch (InterruptedException ignored) { }
            });
            t.start();
        }

        long startTime = System.nanoTime();
        startLatch.countDown();
        endLatch.await();
        long time = System.nanoTime() - startTime;
        return time;
    }
}
