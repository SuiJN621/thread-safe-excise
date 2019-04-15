package condition_queue;

/**
 * @author Sui
 * @date 2018.10.31 18:08
 */
public class BoundedBuffer<V> extends BaseBoundedBuffer<V> {

    public BoundedBuffer(int size) {
        super(size);
    }

    public void doPut(V v) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        boolean wasEmpty = isEmpty();
        put(v);
        if(wasEmpty) {
            notifyAll();
        }
    }

    public V doTake() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        boolean wasFull = isFull();
        V v = take();
        if(wasFull) {
            notifyAll();
        }
        return v;
    }

}
