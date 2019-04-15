package executors;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sui
 * @date 2018.10.31 15:02
 */
public class ConcurrentPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final ExecutorService exec;
    private final ConcurrentMap<P, Boolean> seen;
    //为了在找到某个解答后停止任务, 引入闭锁
    final ValueLatch<Puzzle.Node<P, M>> solution = new ValueLatch<>();
    //记录活动线程数量, 当全部搜索完后能正确结束
    private final AtomicInteger taskCount = new AtomicInteger(0);

    //contructor
    public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
        this.exec = Executors.newCachedThreadPool();
        this.seen = new ConcurrentHashMap<>();
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            exec.execute(newTask(p, null, null));
            Puzzle.Node<P, M> goal = solution.getValue();
            return goal == null ? null : goal.asMoveList();
        } finally {
            exec.shutdown();
        }
    }


    private Runnable newTask(P p, M m, Puzzle.Node<P, M> n) {
        return new SolverTask(p, m, n);
        //return new CountingSolverTask(p, m, n);
    }

    class SolverTask extends Puzzle.Node<P, M> implements Runnable {

        public SolverTask(P pos, M move, Puzzle.Node<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            if (solution.isSet() || seen.putIfAbsent(pos, true) != null) {
                return;
            }
            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            }
            for (M m : puzzle.legalMoves(pos)) {
                exec.execute(newTask(puzzle.move(pos, m), m, this));
            }
        }
    }

    class ValueLatch<T> {
        private final CountDownLatch done = new CountDownLatch(1);
        private T value = null;

        public T getValue() throws InterruptedException {
            done.await();
            synchronized (this) {
                return value;
            }
        }

        public boolean isSet() {
            return done.getCount() == 0;
        }

        public synchronized void setValue(T newValue) {
            if (!isSet()) {
                value = newValue;
                done.countDown();
            }
        }
    }

    class CountingSolverTask extends SolverTask {
        CountingSolverTask(P pos, M move, Puzzle.Node<P, M> prev) {
            super(pos, move, prev);
            //任务计数器+1
            taskCount.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                //如果所有任务都结束了, 将结果设置为null使得getValue不阻塞, 结束线程池
                if (taskCount.decrementAndGet() == 0) {
                    solution.setValue(null);
                }
            }
        }
    }
}