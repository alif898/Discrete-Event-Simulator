package cs2030.simulator;

/**
 * Customer class that has unique ID, and arrivalTime and serviceTime.
 */
class Customer {

    private final int id;
    private final Double arrivalTime;
    private final Double serviceTime;

    Customer(int id, Double arrivalTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = 1.0;
    }

    Customer(int id, Double arrivalTime, Double serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getCustomerId() {
        return this.id;
    }

    public Double getArrivalTime() {
        return this.arrivalTime;
    }

    public Double getServiceTime() {
        return this.serviceTime;
    }

    public boolean isGreedy() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%d", this.getCustomerId());
    }
}
