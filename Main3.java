import cs2030.simulator.Simulator;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * External facing class that takes in inputs using a scanner to generate simulation.
 * Level 3
 */
class Main3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Double> arrivalTimes = new ArrayList<Double>();
        List<Double> serviceTimes = new ArrayList<Double>();
        List<Double> serverRestTimes = new ArrayList<Double>();

        int numServers = sc.nextInt();
        int maxQueue = sc.nextInt();
        int numCustomers = sc.nextInt();
        sc.nextLine();
        while (numCustomers > 0) {
            arrivalTimes.add(sc.nextDouble());
            serviceTimes.add(sc.nextDouble());
            numCustomers -= 1;
        }
        while (sc.hasNextDouble()) {
            serverRestTimes.add(sc.nextDouble());
        }
        Simulator s = new Simulator(numServers, maxQueue);
        s.simulate(arrivalTimes, serviceTimes, serverRestTimes);
    }
}
