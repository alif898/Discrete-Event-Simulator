package cs2030.simulator;

/**
 * GreedyCustomer class that is child class of Customer.
 */
class GreedyCustomer extends Customer {

    GreedyCustomer(int id, Double arrivalTime) {
        super(id, arrivalTime);
    }

    @Override
    public boolean isGreedy() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("%d(greedy)", super.getCustomerId());
    }
}