import java.util.ArrayList;
import java.util.List;

/**
 * Состояние пробирок
 */
public class TubeState {
    private final List<Tube> tubes;
    private final int hash;

    public TubeState(List<Tube> tubes) {
        this.tubes = new ArrayList<>();
        for (Tube tube : tubes) {
            this.tubes.add(new Tube(tube.getColors(), tube.getCapacity()));
        }
        this.hash = calculateHash();
    }

    private int calculateHash() {
        return tubes.hashCode();
    }

    public boolean canMove(int from, int to) {
        if (from == to) return false;

        Tube fromTube = tubes.get(from);
        Tube toTube = tubes.get(to);

        if (fromTube.isEmpty()) return false;
        if (toTube.isFull()) return false;
        if (toTube.isEmpty()) return true;

        return fromTube.getTopColor() == toTube.getTopColor();
    }

    public TubeState move(Move move) {
        return move(move.getFromTube(), move.getToTube());
    }

    public TubeState move(int from, int to) {
        if (!canMove(from, to)) {
            throw new IllegalArgumentException("Invalid move: " + from + " -> " + to);
        }

        Tube fromTube = tubes.get(from);
        Tube toTube = tubes.get(to);

        byte color = fromTube.getTopColor();
        int amount = Math.min(fromTube.getTopGroupSize(), toTube.getFreeSpace());

        List<Tube> newTubes = new ArrayList<>();
        for (int i = 0; i < tubes.size(); i++) {
            if (i == from) {
                newTubes.add(removeTopLiquid(fromTube, amount));
            } else if (i == to) {
                newTubes.add(addLiquid(toTube, color, amount));
            } else {
                newTubes.add(new Tube(tubes.get(i).getColors(), tubes.get(i).getCapacity()));
            }
        }

        return new TubeState(newTubes);
    }

    private Tube removeTopLiquid(Tube tube, int amount) {
        byte[] colors = tube.getColors();
        int height = tube.getTopHeight();

        for (int i = 0; i < amount; i++) {
            colors[height - 1 - i] = 0;
        }

        return new Tube(colors, tube.getCapacity());
    }

    private Tube addLiquid(Tube tube, byte color, int amount) {
        byte[] colors = tube.getColors();
        int height = tube.getTopHeight();

        for (int i = 0; i < amount; i++) {
            colors[height + i] = color;
        }

        return new Tube(colors, tube.getCapacity());
    }

    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();

        for (int from = 0; from < tubes.size(); from++) {
            for (int to = 0; to < tubes.size(); to++) {
                if (canMove(from, to)) {
                    moves.add(new Move(from, to));
                }
            }
        }

        return moves;
    }

    public boolean isSolved() {
        for (Tube tube : tubes) {
            if (!tube.isEmpty() && !tube.isComplete()) {
                return false;
            }
        }
        return true;
    }

    public List<Tube> getTubes() {
        List<Tube> copy = new ArrayList<>();
        for (Tube tube : tubes) {
            copy.add(new Tube(tube.getColors(), tube.getCapacity()));
        }
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TubeState tubeState = (TubeState) obj;
        return tubes.equals(tubeState.tubes);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tubes.size(); i++) {
            sb.append("[").append(i).append("] ").append(tubes.get(i)).append("\n");
        }
        return sb.toString();
    }
}