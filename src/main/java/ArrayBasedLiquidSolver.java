import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class ArrayBasedLiquidSolver {

    public static final class StateTubes {
        private final byte[][] tubes;
        private final int capacity;
        private final int hash;

        public StateTubes(byte[][] tubes, int capacity) {
            this.capacity = capacity;
            this.tubes = deepCopy(tubes);
            this.hash = calculateHash();
        }

        private byte[][] deepCopy(byte[][] original) {
            byte[][] copy = new byte[original.length][];
            for (int i = 0; i < original.length; i++) {
                copy[i] = original[i].clone();
            }
            return copy;
        }

        private int calculateHash() {
            return Arrays.deepHashCode(tubes);
        }

        public boolean tubeIsEmpty(int index) {
            return tubes[index][0] == 0; // Первая позиция пуста = вся пробирка пуста
        }

        public boolean tubeIsFull(int index) {
            return tubes[index][capacity - 1] != 0; // Последняя позиция заполнена
        }

        public byte getTopColor(int index) {
            for (int i = capacity - 1; i >= 0; i--) {
                if (tubes[index][i] != 0) {
                    return tubes[index][i];
                }
            }
            return 0;
        }

        public int getTopHeight(int index) {
            for (int i = 0; i < capacity; i++) {
                if (tubes[index][i] == 0) {
                    return i; // Высота заполненной части
                }
            }
            return capacity; // Полная
        }

        public int getTopGroupSize(int index) {
            byte topColor = getTopColor(index);
            if (topColor == 0) return 0;

            int topHeight = getTopHeight(index);
            int count = 0;
            for (int i = topHeight - 1; i >= 0; i--) {
                if (tubes[index][i] == topColor) {
                    count++;
                } else {
                    break;
                }
            }
            return count;
        }

        public int getFreeSpace(int index) {
            return capacity - getTopHeight(index);
        }

        public boolean canMove(int from, int to) {
            if (tubeIsEmpty(from)) return false;
            if (tubeIsFull(to)) return false;
            if (tubeIsEmpty(to)) return true;
            return getTopColor(from) == getTopColor(to);
        }

        public boolean isTubeComplete(int index) {
            if (tubeIsEmpty(index) || !tubeIsFull(index)) return false;
            byte firstColor = tubes[index][0];
            for (int i = 1; i < capacity; i++) {
                if (tubes[index][i] != firstColor) return false;
            }
            return true;
        }

        public boolean isSolved() {
            for (int i = 0; i < tubes.length; i++) {
                if (!tubeIsEmpty(i) && !isTubeComplete(i)) {
                    return false;
                }
            }
            return true;
        }

        public MoveResult tryMove(int from, int to) {
            if (!canMove(from, to)) return null;

            byte color = getTopColor(from);
            int fromHeight = getTopHeight(from);
            int toHeight = getTopHeight(to);
            int freeSpace = getFreeSpace(to);
            int groupSize = getTopGroupSize(from);

            int amount = Math.min(groupSize, freeSpace);

            byte[][] newTubes = deepCopy(tubes);

            for (int i = 0; i < amount; i++) {
                newTubes[from][fromHeight - 1 - i] = 0;
            }

            for (int i = 0; i < amount; i++) {
                newTubes[to][toHeight + i] = color;
            }

            return new MoveResult(new StateTubes(newTubes, capacity), from, to, amount);
        }

        public List<MoveResult> getPossibleMoves() {
            List<MoveResult> moves = new ArrayList<>();

            for (int from = 0; from < tubes.length; from++) {
                if (tubeIsEmpty(from) || isTubeComplete(from)) continue;

                byte fromColor = getTopColor(from);
                int fromGroupSize = getTopGroupSize(from);

                for (int to = 0; to < tubes.length; to++) {
                    if (from == to || !canMove(from, to)) continue;
                    if (isTubeComplete(to)) continue; // Не лей в завершенную

                    MoveResult move = tryMove(from, to);
                    if (move != null) {
                        moves.add(move);
                    }
                }
            }

            moves.sort((a, b) -> Integer.compare(rateMove(b), rateMove(a)));
            return moves;
        }

        private int rateMove(MoveResult move) {
            int score = 0;
            StateTubes newState = move.newState;

            if (newState.isTubeComplete(move.to)) score += 10;

            if (newState.tubeIsEmpty(move.from)) score += 5;

            int newGroupSize = newState.getTopGroupSize(move.to);
            if (newGroupSize > 1) score += newGroupSize;

            return score;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            StateTubes that = (StateTubes) obj;
            return Arrays.deepEquals(tubes, that.tubes);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tubes.length; i++) {
                sb.append("Tube ").append(i).append(": ");
                for (int j = 0; j < capacity; j++) {
                    sb.append(tubes[i][j]).append(" ");
                }
                sb.append("\n");
            }
            return sb.toString();
        }

        public int getTubeCount() {
            return tubes.length;
        }
    }

    public static class MoveResult {
        public final StateTubes newState;
        public final int from;
        public final int to;
        public final int amount;

        public MoveResult(StateTubes newState, int from, int to, int amount) {
            this.newState = newState;
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }

    public static class Solution {
        public final List<String> moves;
        public final int statesExplored;
        public final long memoryUsed;

        public Solution(List<String> moves, int statesExplored, long memoryUsed) {
            this.moves = moves;
            this.statesExplored = statesExplored;
            this.memoryUsed = memoryUsed;
        }
    }

    public static Solution solveWithMemoryLimit(StateTubes initialState, int maxStates) {
        Set<StateTubes> visited = new HashSet<>();
        Map<StateTubes, StateInfo> stateInfo = new HashMap<>();
        PriorityQueue<StateTubes> queue = new PriorityQueue<>(
                Comparator.comparingInt(s -> heuristic(s) + stateInfo.get(s).depth)
        );

        stateInfo.put(initialState, new StateInfo(null, -1, -1, 0));
        visited.add(initialState);
        queue.add(initialState);

        int statesExplored = 0;

        while (!queue.isEmpty() && statesExplored < maxStates) {
            StateTubes current = queue.poll();
            statesExplored++;

            if (current.isSolved()) {
                return new Solution(
                        buildSolution(current, stateInfo),
                        statesExplored,
                        Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                );
            }

            for (MoveResult move : current.getPossibleMoves()) {
                if (!visited.contains(move.newState)) {
                    visited.add(move.newState);
                    stateInfo.put(move.newState,
                            new StateInfo(current, move.from, move.to,
                                    stateInfo.get(current).depth + 1));
                    queue.add(move.newState);

                    if (visited.size() > maxStates * 0.8) {
                        removeWorstStates(queue, visited, stateInfo, maxStates / 2);
                    }
                }
            }
        }

        return new Solution(Collections.emptyList(), statesExplored, 0);
    }

    private static class StateInfo {
        public final StateTubes parent;
        public final int from;
        public final int to;
        public final int depth;

        public StateInfo(StateTubes parent, int from, int to, int depth) {
            this.parent = parent;
            this.from = from;
            this.to = to;
            this.depth = depth;
        }
    }

    private static int heuristic(StateTubes state) {
        int score = 0;
        for (int i = 0; i < state.getTubeCount(); i++) {
            if (state.isTubeComplete(i)) score -= 5;    // Завершенные - хорошо
            else if (!state.tubeIsEmpty(i)) score += 2; // Незавершенные - плохо

            int topGroupSize = state.getTopGroupSize(i);
            if (topGroupSize > 0) {
                score += (state.getTopHeight(i) - topGroupSize);
            }
        }
        return score;
    }

    private static List<String> buildSolution(StateTubes endState,
                                              Map<StateTubes, StateInfo> stateInfo) {
        List<String> solution = new ArrayList<>();
        StateTubes current = endState;

        while (stateInfo.get(current).parent != null) {
            StateInfo info = stateInfo.get(current);
            solution.add(0, String.format("(%d, %d)", info.from, info.to));
            current = info.parent;
        }

        return solution;
    }

    private static void removeWorstStates(PriorityQueue<StateTubes> queue,
                                          Set<StateTubes> visited,
                                          Map<StateTubes, StateInfo> stateInfo,
                                          int keepCount) {
        List<StateTubes> bestStates = new ArrayList<>(queue);
        bestStates.sort(Comparator.comparingInt(ArrayBasedLiquidSolver::heuristic));

        if (bestStates.size() > keepCount) {
            queue.clear();
            visited.clear();

            for (int i = 0; i < keepCount; i++) {
                StateTubes state = bestStates.get(i);
                queue.add(state);
                visited.add(state);
            }

            stateInfo.keySet().retainAll(visited);
        }
    }

    public static StateTubes createInitialState(int tubeCount, int capacity, int[][] colors) {
        byte[][] tubes = new byte[tubeCount][capacity];

        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < colors[i].length; j++) {
                tubes[i][j] = (byte) colors[i][j];
            }
        }

        return new StateTubes(tubes, capacity);
    }

    public static void main(String[] args) {
        int[][] initialColors = {
                {4,4,10,2},
                {8,12,8,1},
                {9,5,7,10},
                {5,2,3,5},
                {7,8,11,6},
                {2,1,12,12},
                {11,8,7,4},
                {1,3,11,10},
                {9,9,7,10},
                {11,6,2,6},
                {3,9,6,4},
                {1,12,3,5},
                {0,0,0,0},
                {0,0,0,0}
        };

        StateTubes initialState = createInitialState(14, 4, initialColors);
        Solution solution = solveWithMemoryLimit(initialState, 10000);

        if (solution.moves.isEmpty()) {
            System.out.println("Solve not found! Count states analyzed: " + solution.statesExplored);
        } else {
            System.out.println("Solve found! Moves: " + solution.moves.size());
            System.out.println("Count states analyzed: " + solution.statesExplored);

            solution.moves.forEach(System.out::println);
        }
    }
}
