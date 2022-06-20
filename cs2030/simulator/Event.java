package cs2030.simulator;

import java.util.Optional;

/**
 * Event class that can store customer, event time, a server and also the current event state.
 * uses EventState enum to track event states
 */
class Event {

    private final Customer customer;
    private final Double time;
    private final Optional<Server> server;
    private final EventState currentState;

    /**
     * Default constructor, for ARRIVE events.
     * @param customer customer that just arrived
     */
    Event(Customer customer) {
        this.customer = customer;
        this.time = customer.getArrivalTime();
        this.server = Optional.empty();
        this.currentState = EventState.ARRIVE;
    }

    /**
     * Overloaded constructor when changing event states.
     * @param customer customer
     * @param time time
     * @param server server
     * @param currentState new event state
     */
    Event(Customer customer, Double time, Server server, EventState currentState) {
        this.customer = customer;
        this.time = time;
        this.server = Optional.ofNullable(server);
        this.currentState = currentState;
    }

    /**
     * overloaded constructor when changing event states.
     * @param event previous event to fetch attributes from
     * @param currentState new event state
     */
    Event(Event event, EventState currentState) {
        this.customer = event.getCustomer();
        this.time = event.getTime();
        this.server = Optional.ofNullable(event.getServer());
        this.currentState = currentState;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Double getTime() {
        return this.time;
    }

    public Server getServer() {
        return this.server.orElse(new Server(-1));
    }

    public boolean isArrive() {
        return this.currentState == EventState.ARRIVE;
    }

    public boolean isServe() {
        return this.currentState == EventState.SERVE;
    }

    public boolean isWait() {
        return this.currentState == EventState.WAIT;
    }

    public boolean isLeave() {
        return this.currentState == EventState.LEAVE;
    }

    public boolean isDone() {
        return this.currentState == EventState.DONE;
    }

    public boolean isServerRest() {
        return this.currentState == EventState.SERVERREST;
    }

    public boolean isServerBack() {
        return this.currentState == EventState.SERVERBACK;
    }

    public Event serve(Customer customer, Double time, Server server) {
        return new Event(customer, time, server, EventState.SERVE);
    }

    public Event wait(Server server) {
        return new Event(this.getCustomer(), this.getCustomer().getArrivalTime(),
                server, EventState.WAIT);
    }

    public Event done(Double time) {
        return new Event(this.getCustomer(), time, this.getServer(), EventState.DONE);
    }

    public Event leave() {
        return new Event(this, EventState.LEAVE);
    }

    public Event serverRest(Double time) {
        return new Event(this.getCustomer(), time, this.getServer(), EventState.SERVERREST);
    }

    public Event serverBack(Double time) {
        return new Event(this.getCustomer(), time, this.getServer(), EventState.SERVERBACK);
    }

    /**
     * Checks event type before creating correct toString.
     * @return corresponding toString
     */
    @Override
    public String toString() {
        if (this.isArrive()) {
            return String.format("%.3f %s arrives", this.getTime(),
                    this.getCustomer().toString());
        } else if (this.isServe()) {
            return String.format("%.3f %s serves by %s",
                    this.getTime(), this.getCustomer().toString(),
                    this.getServer().toString());
        } else if (this.isWait()) {
            return String.format("%.3f %s waits at %s",
                    this.getTime(), this.getCustomer().toString(),
                    this.getServer().toString());
        } else if (this.isLeave()) {
            return String.format("%.3f %s leaves", this.getTime(),
                    this.getCustomer().toString());
        } else if (this.isDone()) {
            return String.format("%.3f %s done serving by %s",
                    this.getTime(), this.getCustomer().toString(),
                    this.getServer().toString());
        } else {
            return "SERVER REST";
        }
    }
}
