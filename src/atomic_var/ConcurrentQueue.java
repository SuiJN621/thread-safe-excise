package atomic_var;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Sui
 * @date 2018.11.01 15:16
 */
public class ConcurrentQueue<E> {

    private static class Node<E> {

        private E item;
        private AtomicReference<Node<E>> next;

        public Node(E item, AtomicReference<Node<E>> next) {
            this.item = item;
            this.next = next;
        }
    }

    /** 哨兵节点 **/
    private final Node<E> dummy = new Node<>(null, null);
    /** 初始化时队列头节点和尾节点都指向该哨兵节点 **/
    private final AtomicReference<Node<E>> head = new AtomicReference<>(dummy);
    private final AtomicReference<Node<E>> tail = new AtomicReference<>(dummy);

    public boolean put(E item) {
        Node<E> newNode = new Node<>(item, null);
        while (true) {
            Node<E> curTail = tail.get();
            Node<E> tailNext = curTail.next.get();
            if (curTail == tail.get()) {
                //检查是否有其他线程已经插入了元素
                if (tailNext != null) {
                    //队列处于中间状态, 推进尾节点
                    tail.compareAndSet(curTail, tailNext);
                } else {
                    //处于稳定状态, 尝试插入新节点
                    if (curTail.next.compareAndSet(null, newNode)) {
                        //插入成功, 尝试推进尾节点
                        tail.compareAndSet(curTail, tailNext);
                        return true;
                    }
                }
            }
        }
    }
}
