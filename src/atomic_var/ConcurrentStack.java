package atomic_var;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Sui
 * @date 2018.11.01 11:54
 */
public class ConcurrentStack<E> {

    private AtomicReference<Node<E>> top = new AtomicReference<>();

    public void push(E item) {
        Node<E> newNode = new Node<>(item);
        Node<E> oldNode;
        do {
            oldNode = top.get();
            newNode.next = oldNode;
        } while (!top.compareAndSet(oldNode, newNode));
    }

    public E pop() {
        Node<E> oldHead, newHead;
        do {
            oldHead = top.get();
            if (oldHead == null) {
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    private static class Node<E> {

        private E item;
        private Node<E> next;

        Node(E item) {
            this.item = item;
        }
    }
}
