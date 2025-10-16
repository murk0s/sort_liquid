import java.util.Arrays;

/**
 * Класс пробирки
 * будем считать, что 0 это пусто
 */
public class Tube {
    private final byte[] colors;
    private final int capacity;

    public Tube(byte[] colors, int capacity) {
        this.capacity = capacity;
        this.colors = colors.clone();
    }

    public boolean isEmpty() {
        return colors[0] == 0;
    }

    public boolean isFull() {
        return colors[capacity - 1] != 0;
    }

    public byte getTopColor() {
        for (int i = capacity - 1; i >= 0; i--) {
            if (colors[i] != 0) {
                return colors[i];
            }
        }
        return 0;
    }

    public int getTopHeight() {
        for (int i = 0; i < capacity; i++) {
            if (colors[i] == 0) {
                return i;
            }
        }
        return capacity;
    }

    public int getTopGroupSize() {
        byte topColor = getTopColor();
        if (topColor == 0) return 0;

        int height = getTopHeight();
        int count = 0;
        for (int i = height - 1; i >= 0; i--) {
            if (colors[i] == topColor) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public int getFreeSpace() {
        return capacity - getTopHeight();
    }

    public boolean isComplete() {
        if (isEmpty() || !isFull()) return false;
        byte firstColor = colors[0];
        for (int i = 1; i < capacity; i++) {
            if (colors[i] != firstColor) return false;
        }
        return true;
    }

    public byte[] getColors() {
        return colors.clone();
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tube tube = (Tube) obj;
        return Arrays.equals(colors, tube.colors);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(colors);
    }

    @Override
    public String toString() {
        return Arrays.toString(colors);
    }
}