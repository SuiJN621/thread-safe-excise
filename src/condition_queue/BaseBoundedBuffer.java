package condition_queue;

/**
 * @author Suis
 * @date 2018.10.31 18:00
 */
public class BaseBoundedBuffer<V> {

    protected final V[] buf;
    protected int tail;
    protected int head;
    protected int count;

    @SuppressWarnings("unchecked")
    public BaseBoundedBuffer(int size) {
        this.buf = (V[]) new Object[size];
    }

    protected synchronized final void put(V item) {
        buf[tail] = item;
        if (++tail == buf.length) {
            tail = 0;
        }
        ++count;
    }

    protected synchronized final V take() {
        V v = buf[head];
        buf[head] = null;
        if (++head == buf.length) {
            head = 0;
        }
        --count;
        return v;
    }

    protected synchronized final boolean isFull() {
        return count == buf.length;
    }

    protected synchronized final boolean isEmpty() {
        return count == 0;
    }

    public static void main(String[] args) {
        System.out.println(9999L/1000L);
    }
}
