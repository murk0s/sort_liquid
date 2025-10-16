import java.util.ArrayList;
import java.util.List;

/**
 * Класс решения
 */
public class Solution {
    private final List<Move> moves;
    private final int statesProcessed;

    public Solution(List<Move> moves, int statesProcessed) {
        this.moves = new ArrayList<>(moves);
        this.statesProcessed = statesProcessed;
    }

    public boolean isSolved() {
        return !moves.isEmpty();
    }

    public List<Move> getMoves() {
        return new ArrayList<>(moves);
    }

    public int getStatesProcessed() {
        return statesProcessed;
    }

    public int getMoveCount() {
        return moves.size();
    }
}