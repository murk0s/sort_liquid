import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика для сооздания состояния
 */
public class TubeFactory {
    public static TubeState createState(int tubeCapacity, int[][] colors) {
        List<Tube> tubes = new ArrayList<>();

        for (int[] tubeColors : colors) {
            byte[] byteColors = new byte[tubeCapacity];
            for (int i = 0; i < tubeColors.length; i++) {
                byteColors[i] = (byte) tubeColors[i];
            }
            tubes.add(new Tube(byteColors, tubeCapacity));
        }

        return new TubeState(tubes);
    }
}