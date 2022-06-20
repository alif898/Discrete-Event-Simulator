package cs2030.simulator;

import java.util.Comparator;

/**
 * Comparator class for Events, prioritising earlier events, then by ascending customer ID.
 */
class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event event, Event other) {
        if (event.getTime() < other.getTime()) {
            return -1;
        } else if (event.getTime() > other.getTime()) {
            return 1;
        } else {
            if (event.getCustomer().getCustomerId() < other.getCustomer().getCustomerId()) {
                return -1;
            } else if (event.getCustomer().getCustomerId() > other.getCustomer().getCustomerId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
