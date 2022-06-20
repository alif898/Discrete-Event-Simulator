package cs2030.simulator;

import java.util.Optional;
import java.util.ArrayList;

/**
 * Server class with unique ID, its next available time, a customer queue and max queue length.
 */
class Server {

    private final int id;
    private final Double nextAvailableTime;
    private final ArrayList<Customer> customerQueue;
    private final int maxWait;

    /**
     * Constructor for new Server.
     */
    Server(int id) {
        this.id = id;
        this.nextAvailableTime = 0.0;
        this.customerQueue = new ArrayList<Customer>();
        this.maxWait = 1;
    }

    /**
     * Constructor for new Server, with specified max waiting customers.
     */
    Server(int id, int maxWait) {
        this.id = id;
        this.nextAvailableTime = 0.0;
        this.customerQueue = new ArrayList<Customer>();
        this.maxWait = maxWait;
    }

    Server(int id, Double nextAvailableTime, ArrayList<Customer> customerQueue, int maxWait) {
        this.id = id;
        this.nextAvailableTime = nextAvailableTime;
        this.customerQueue = customerQueue;
        this.maxWait = maxWait;
    }

    public int getServerId() {
        return this.id;
    }

    public Double getNextAvailableTime() {
        return this.nextAvailableTime;
    }

    public ArrayList<Customer> getCustomerQueue() {
        return this.customerQueue;
    }

    public int getMaxWait() {
        return this.maxWait;
    }

    public int getQueueSize() {
        return this.customerQueue.size();
    }

    public boolean isSelfCheckout() {
        return false;
    }

    /**
     * Method that tries to fetch first customer in queue.
     * @return first customer in queue
     */
    public Customer getCustomer() {
        Optional<Customer> result;
        try {
            result = Optional.ofNullable(this.getCustomerQueue().get(0));
        } catch (Exception e) {
            result = Optional.empty();
        }
        return result.orElse(new Customer(-1, 0.0));
    }

    /**
     * Checks if customer should be LEAVE, WAIT or SERVE.
     * using customer arrival time to check
     * -1: LEAVE
     * 0: WAIT
     * 1: SERVE
     * @param time Customer arrivalTime to check against
     * @return int, see above
     */
    public int canServe(Double time) {
        if (this.getQueueSize() >= this.getMaxWait()) {
            return -1;
        } else if (time < this.getNextAvailableTime()) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * To be used to update server time, when servicing or resting.
     * @param time nextAvailableTime to set to
     * @return new Server with updated next available time
     */
    public Server updateTime(Double time) {
        return new Server(this.getServerId(), time, this.getCustomerQueue(), this.getMaxWait());
    }

    /**
     * Adds customer to queue if there is space.
     * @param customer next Customer that needs to be added
     * @return new Server with customer added in queue
     */
    public Server addCustomer(Customer customer) {
        if (this.getQueueSize() < this.getMaxWait()) {
            this.customerQueue.add(customer);
            return new Server(this.getServerId(), this.getNextAvailableTime(),
                    this.getCustomerQueue(), this.getMaxWait());
        } else {
            return this;
        }
    }

    /**
     * To be used when server has finished serving Customer.
     * @return new Server with updated queue
     */
    public Server serveCustomer(Customer customer) {
        if (this.getQueueSize() > 0) {
            this.customerQueue.remove(0);
        }
        return new Server(this.getServerId(), this.getNextAvailableTime(),
                this.getCustomerQueue(), this.getMaxWait());
    }

    @Override
    public String toString() {
        return String.format("server %d", this.getServerId());
    }
}