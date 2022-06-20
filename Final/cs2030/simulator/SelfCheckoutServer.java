package cs2030.simulator;

import java.util.Optional;
import java.util.ArrayList;

/**
 * SelfCheckoutServer that is child class of Server.
 * SelfCheckoutServer shares a queue amongst all instances and does not rest.
 */
class SelfCheckoutServer extends Server {

    /**
     * sharedQueue is the shared queue among all SelfCheckoutServer.
     * totalMaxWait is shared among all SelfCheckoutServer.
     */
    private static final ArrayList<Customer> sharedQueue = new ArrayList<Customer>();
    private static int totalMaxWait;

    SelfCheckoutServer(int id, int maxWait) {
        super(id, maxWait);
        totalMaxWait = maxWait;
    }

    SelfCheckoutServer(int id, Double nextAvailableTime,
                       ArrayList<Customer> customerQueue, int maxWait) {
        super(id, nextAvailableTime, customerQueue, maxWait);
    }

    /**
     * Static helper function to fetch queue size.
     * @return queue size
     */
    static int helperQueueSize() {
        return sharedQueue.size();
    }

    /**
     * Static helper function to add customer.
     * @param customer next Customer that needs to be added
     */
    static void helperAdd(Customer customer) {
        sharedQueue.add(customer);
    }

    /**
     * Static helper function to remove customer.
     */
    static void helperRemove(Customer customer) {
        int removeIndex = -1;
        for (int i = 0; i < sharedQueue.size(); i++) {
            if (sharedQueue.get(i).getCustomerId() == customer.getCustomerId()) {
                removeIndex = i;
            }
        }
        if (removeIndex != -1) {
            sharedQueue.remove(removeIndex);
        }
    }

    /**
     * Helper function using CustomerComparator.
     * @return waiting customer that arrived earliest
     */
    static Optional<Customer> getNext() {
        return sharedQueue.stream().min(new CustomerComparator());
    }

    /**
     * Override in order to return sharedQueue instead.
     * @return static sharedQueue
     */
    @Override
    public ArrayList<Customer> getCustomerQueue() {
        return sharedQueue;
    }

    /**
     * Override in order to return totalMaxWait instead.
     * @return totalMaxWait, maxWait * no. of SelfCheckoutServer
     */
    @Override
    public int getMaxWait() {
        return totalMaxWait;
    }

    /**
     * Override in order to check sharedQueue instead.
     * @return queue size
     */
    @Override
    public int getQueueSize() {
        return helperQueueSize();
    }

    @Override
    public boolean isSelfCheckout() {
        return true;
    }

    /**
     * Override in order to get from sharedQueue instead.
     * @return first customer in queue
     */
    @Override
    public Customer getCustomer() {
        Optional<Customer> result;
        result = getNext();
        result.ifPresent(SelfCheckoutServer::helperRemove);
        return result.orElse(new Customer(-1, 0.0));
    }

    /**
     * Override in order to return a SelfCheckoutServer.
     * @param time nextAvailableTime to set to
     * @return itself
     */
    @Override
    public SelfCheckoutServer updateTime(Double time) {
        return new SelfCheckoutServer(super.getServerId(), time,
                super.getCustomerQueue(), super.getMaxWait());
    }

    /**
     * Override in order to update sharedQueue instead.
     * @param customer next Customer that needs to be added
     * @return itself
     */
    @Override
    public SelfCheckoutServer addCustomer(Customer customer) {
        if (this.getQueueSize() < this.getMaxWait()) {
            helperAdd(customer);
        }
        return this;
    }

    /**
     * Override in order to update sharedQueue instead.
     * @return itself
     */
    @Override
    public SelfCheckoutServer serveCustomer(Customer customer) {
        SelfCheckoutServer result;
        try {
            helperRemove(customer);
        } finally {
            result = this;
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("self-check %d", this.getServerId());
    }
}