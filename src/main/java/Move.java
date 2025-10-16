/**
 * Класс дивжения
 */
public class Move {
    private final int fromTube;
    private final int toTube;

    public Move(int fromTube, int toTube) {
        this.fromTube = fromTube;
        this.toTube = toTube;
    }

    public int getFromTube() {
        return fromTube;
    }

    public int getToTube() {
        return toTube;
    }

    @Override
    public String toString() {
        return "(" + fromTube + ", " + toTube+")";
    }
}