package task_cancel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 线程生命周期管理
 */
public class LogService {

    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    //关闭标记
    private boolean isShutdown;
    //消息数量
    private int reservations;

    public LogService(int cap) {
        this.queue = new LinkedBlockingDeque<>(cap);
        this.loggerThread = new LoggerThread();
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown) {
                throw new IllegalStateException();
            }
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        //检查服务是否还可用
                        synchronized (LogService.this) {
                            //服务关闭并且无消息, 结束方法
                            if (isShutdown && reservations == 0) {
                                break;
                            }
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        System.out.println(msg);
                    } catch (InterruptedException e) {
                        //retry
                    }
                }
            } finally { System.out.println("jieshu"); }
        }
    }
}