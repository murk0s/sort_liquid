import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Класс поиска решения
 */
public class TubeSolver {
    public Solution solve(TubeState initialState) {
        Queue<TubeState> queue = new LinkedList<>();
        Map<TubeState, TubeState> parentMap = new HashMap<>();
        Map<TubeState, Move> moveMap = new HashMap<>();

        queue.add(initialState);
        parentMap.put(initialState, null);

        int statesProcessed = 0;

        while (!queue.isEmpty()) {
            TubeState current = queue.poll();
            statesProcessed++;

            if (current.isSolved()) {
                return new Solution(
                        buildSolution(current, parentMap, moveMap),
                        statesProcessed
                );
            }

            for (Move move : current.getPossibleMoves()) {
                TubeState nextState = current.move(move);

                if (!parentMap.containsKey(nextState)) {
                    parentMap.put(nextState, current);
                    moveMap.put(nextState, move);
                    queue.add(nextState);
                }
            }

            if (statesProcessed > 100000) {
                break;
            }
        }

        return new Solution(Collections.emptyList(), statesProcessed);
    }

    private List<Move> buildSolution(TubeState endState,
                                     Map<TubeState, TubeState> parentMap,
                                     Map<TubeState, Move> moveMap) {
        List<Move> solution = new ArrayList<>();
        TubeState current = endState;

        while (parentMap.get(current) != null) {
            Move move = moveMap.get(current);
            solution.addFirst(move);
            current = parentMap.get(current);
        }

        return solution;
    }
}
