package task_cancel;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Sui
 * @date 2018.10.31 14:25
 */
public class InterruptCancel extends Thread {

    private final BlockingQueue<BigInteger> queue = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            //重复检查获取更好的响应
            //检查是否中断
            while(!Thread.currentThread().isInterrupted()) {
                //阻塞方法会检查抛出InterruptedException
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException consumed) {
            //允许线程退出
        }
    }

    public void cancel() {
        //请求中断
        interrupt();
    }
}
