import cs2030.simulator.Simulator;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * External facing class that takes in inputs using a scanner to generate simulation.
 * Level 2
 */
class Main2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Double> arrivalTimes = new ArrayList<Double>();
        List<Double> serviceTimes = new ArrayList<Double>();

        int numServers = sc.nextInt();
        int maxQueue = sc.nextInt();
        sc.nextLine();
        while (sc.hasNextDouble()) {
            arrivalTimes.add(sc.nextDouble());
            serviceTimes.add(sc.nextDouble());
        }
        Simulator s = new Simulator(numServers, maxQueue);
        s.simulate(arrivalTimes, serviceTimes);
    }
}
