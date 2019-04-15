package task_manange;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Sui
 * @date 2018.10.30 17:29
 */
public class FutureCached<A, V> {

    private ConcurrentHashMap<A, Future<V>> cache = new ConcurrentHashMap<>();
    //private Computor c;

    public V compute(A arg) throws InterruptedException, ExecutionException {
        Future<V> f = cache.get(arg);
        if(f == null) {
            FutureTask<V> ft = new FutureTask<>(() -> {
                //c.compute();
                return null;
            });
            f = cache.putIfAbsent(arg, ft);
            if(f == null) {
                f = ft;
                ft.run();
            }
        }
        try {
            return f.get();
        } catch (CancellationException e) {
            cache.remove(arg, f);
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }
}
