import cs2030.simulator.Simulator;
import java.util.Scanner;

/**
 * External facing class that takes in inputs using a scanner to generate simulation.
 * Level 5
 */
class Main5 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int baseSeed = sc.nextInt();
        int numServers = sc.nextInt();
        int numSelfCheckoutServers = sc.nextInt();
        int maxQueue = sc.nextInt();
        int numCustomers = sc.nextInt();

        Double arrivalRate = sc.nextDouble();
        Double serviceRate = sc.nextDouble();
        Double restingRate = sc.nextDouble();
        Double probRest = sc.nextDouble();
        Double probGreedy = sc.nextDouble();

        Simulator s = new Simulator(numServers, maxQueue);
        s.simulate(baseSeed, numSelfCheckoutServers, numCustomers,
                arrivalRate, serviceRate, restingRate, probRest, probGreedy);
    }
}