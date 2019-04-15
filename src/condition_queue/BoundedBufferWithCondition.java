package condition_queue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sui
 * @date 2018.11.01 10:24
 */
public class BoundedBufferWithCondition<V> {

    protected final V[] buf;
    protected int tail;
    protected int head;
    protected int count;

    @SuppressWarnings("unchecked")
    public BoundedBufferWithCondition(int size) {
        this.buf = (V[]) new Object[size];
    }

    protected final Lock lock = new ReentrantLock();
    protected final Condition notFull = lock.newCondition();
    protected final Condition notEmpty = lock.newCondition();

    public void doPut(V v) throws InterruptedException {
        lock.lock();
        try {
            while (count == buf.length) {
                notFull.await();
            }
            buf[tail] = v;
            if (++tail == buf.length) {
                tail = 0;
            }
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public V doTake() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            V v = buf[head];
            buf[head] = null;
            if (++head == buf.length) {
                head = 0;
            }
            --count;
            notFull.signal();
            return v;
        } finally {
            lock.unlock();
        }
    }
}
