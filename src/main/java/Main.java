public class Main {

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

        TubeState initialState = TubeFactory.createState(4, initialColors);

        System.out.println("Initial state:");
        System.out.println(initialState);

        TubeSolver solver = new TubeSolver();
        Solution solution = solver.solve(initialState);

        if (solution.isSolved()) {
            System.out.println("Solution found in " + solution.getMoveCount() + " moves:");
            for (int i = 0; i < solution.getMoves().size(); i++) {
                System.out.println((i + 1) + ". " + solution.getMoves().get(i));
            }
        } else {
            System.out.println("No solution found. States processed: " + solution.getStatesProcessed());
        }
    }
}
