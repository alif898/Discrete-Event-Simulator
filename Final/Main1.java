import cs2030.simulator.Simulator;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * External facing class that takes in inputs using a scanner to generate simulation.
 * Level 1
 */
class Main1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Double> arrivalTimes = new ArrayList<Double>();

        int numServers = sc.nextInt();
        sc.nextLine();
        while (sc.hasNextDouble()) {
            arrivalTimes.add(sc.nextDouble());
        }
        Simulator s = new Simulator(numServers, 1);
        s.simulate(arrivalTimes);
    }
}
