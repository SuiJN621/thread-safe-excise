package semaphore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * @author Sui
 * @date 2018.10.30 15:04
 */
public class BoundedHashSet<T> {

    private final Set<T> set;
    private final Semaphore semaphore;

    public BoundedHashSet(int size) {
        set = Collections.synchronizedSet(new HashSet<>());
        semaphore = new Semaphore(size);
    }

    public boolean add(T element) throws InterruptedException {
        semaphore.acquire();
        boolean added = false;
        try {
            added = set.add(element);
            return added;
        } finally {
            if (!added) {
                semaphore.release();
            }
        }

    }

    public boolean remove(Object o) {
        boolean wasRemoved = set.remove(o);
        //成功删除释放许可
        if (wasRemoved) {
            semaphore.release();
        }
        return wasRemoved;
    }

}
