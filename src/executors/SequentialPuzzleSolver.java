package executors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sui
 * @date 2018.10.31 14:49
 */
public class SequentialPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final Set<P> seen = new HashSet<P>();

    //constructor
    public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        return search(new Puzzle.Node<>(pos, null, null));
    }

    private List<M> search(Puzzle.Node<P, M> node) {
        //找过的位置不重复找
        if (!seen.contains(node.pos)) {
            seen.add(node.pos);
            if (puzzle.isGoal(node.pos)) {
                return node.asMoveList();
            }
            //便利每个可能位置, 递归查找
            for (M move : puzzle.legalMoves(node.pos)) {
                P pos = puzzle.move(node.pos, move);
                Puzzle.Node<P, M> child = new Puzzle.Node<>(pos, move, node);
                List<M> result = search(child);
                if (result != null)
                    return result;
            }
        }
        return null;
    }
}
