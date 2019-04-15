package executors;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Sui
 * @date 2018.10.31 14:49
 */
public interface Puzzle<P, M> {
    P initialPosition();
    boolean isGoal(P position);
    Set<M> legalMoves(P position);
    P move(P position, M move);

    class Node<P, M> {
        final P pos;
        final M move;
        final Node<P, M> prev;

        //constructor
        public Node(P pos, M move, Node<P, M> prev) {
            this.pos = pos;
            this.move = move;
            this.prev = prev;
        }

        //获取移动路径
        List<M> asMoveList() {
            List<M> solution = new LinkedList<M>();
            for (Node<P, M> n = this; n.move != null; n = n.prev) {
                solution.add(0, n.move);
            }
            return solution;
        }
    }
}
