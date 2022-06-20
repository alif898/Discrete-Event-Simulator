package cs2030.simulator;

import java.util.Comparator;

/**
 * Comparator class for Customer, prioritising earlier arrival time, then by ascending customer ID.
 */
class CustomerComparator implements Comparator<Customer> {

    @Override
    public int compare(Customer customer, Customer other) {
        if (customer.getArrivalTime() < other.getArrivalTime()) {
            return -1;
        } else if (customer.getArrivalTime() > other.getArrivalTime()) {
            return 1;
        } else {
            if (customer.getCustomerId() < other.getCustomerId()) {
                return -1;
            } else if (customer.getCustomerId() > other.getCustomerId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
