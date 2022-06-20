package cs2030.simulator;

/**
 * Statistics class that stores the various statistics of the current simulation.
 */
class Statistics {

    private final Double totalWaitingTime;
    private final int servedCustomers;
    private final int leftCustomers;

    Statistics() {
        this.totalWaitingTime = 0.0;
        this.servedCustomers = 0;
        this.leftCustomers = 0;
    }

    Statistics(Double totalWaitingTime, int servedCustomers, int leftCustomers) {
        this.totalWaitingTime = totalWaitingTime;
        this.servedCustomers = servedCustomers;
        this.leftCustomers = leftCustomers;
    }

    Double getTotalWaitingTime() {
        return this.totalWaitingTime;
    }

    int getServedCustomers() {
        return this.servedCustomers;
    }

    int getLeftCustomers() {
        return this.leftCustomers;
    }

    public Statistics addServed(Double waitingTime) {
        return new Statistics(this.getTotalWaitingTime() + waitingTime, 
                this.getServedCustomers() + 1, this.getLeftCustomers());
    }

    public Statistics addLeft() {
        return new Statistics(this.getTotalWaitingTime(), 
                this.getServedCustomers(), this.getLeftCustomers() + 1);
    }

    @Override
    public String toString() {
        Double avgWaitingTime = (this.getServedCustomers() == 0) 
            ? 0.0 : (this.getTotalWaitingTime() / (double) this.getServedCustomers());
        return String.format("[%.3f %d %d]", avgWaitingTime, 
                this.getServedCustomers(), this.getLeftCustomers());
    }
}


