package cs2030.simulator;

import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;

/**
 * Simulator class uses input from Main class to run simulation.
 * All the logic in handling events going through the PriorityQueue is stored here.
 */
public class Simulator {

    /**
     * ArrayList of servers in current simulation.
     */
    private final ArrayList<Server> serverList;
    private final int maxQueue;

    /**
     * Constructor to generate serverList.
     * @param numServers number of servers to generate
     * @param maxQueue to set maximum queue length of each server
     */
    public Simulator(int numServers, int maxQueue) {
        ArrayList<Server> servers = new ArrayList<Server>();
        for (int i = 1; i < (numServers + 1); i++) {
            servers.add(new Server(i, maxQueue));
        }
        this.serverList = servers;
        this.maxQueue = maxQueue;
    }

    /**
     * Updates serverList by searching by serverId, which is unique.
     * needed as Server is immutable
     * @param server to update
     */
    void updateServerList(Server server) {
        for (int i = 0; i < this.serverList.size(); i++) {
            if (this.serverList.get(i).getServerId() == server.getServerId()) {
                this.serverList.set(i, server);
            }
        }
    }

    /**
     * Returns next available Server.
     * server.canServe returns 1 for SERVE, 0 for WAIT, -1 for LEAVE
     * @param time time to check against
     * @return nearest available Server
     */
    Server selectServer(Double time) {
        for (Server server : this.serverList) {
            if (server.canServe(time) == 1) {
                return server;
            }
        }

        for (Server server : this.serverList) {
            if (server.canServe(time) == 0) {
                return server;
            }
        }
        return new Server(-1);
    }

    /**
     * Returns next available Server for GreedyCustomer.
     * server.canServe returns 1 for SERVE, 0 for WAIT, -1 for LEAVE
     * @param time time to check against
     * @return nearest available Server with minimum queue length
     */
    Server greedySelectServer(Double time) {
        for (Server server : this.serverList) {
            if (server.canServe(time) == 1) {
                return server;
            }
        }
        /*
         * finding minimum queue size among all servers
         */
        int minQueue = this.serverList
                .stream()
                .map(Server::getQueueSize)
                .min(Integer::compare)
                .orElse(-1);
        if (minQueue == - 1) {
            for (Server server : this.serverList) {
                if (server.canServe(time) == 0) {
                    return server;
                }
            }
        } else {
            for (Server server : this.serverList) {
                if (server.canServe(time) == 0 && server.getQueueSize() == minQueue) {
                    return server;
                }
            }
        }
        return new Server(-1);
    }

    /**
     * Level 1.
     * @param arrivalTimes list of customer arrivalTimes
     */
    public void simulate(List<Double> arrivalTimes) {

        /*
         * Creates new Statistics and PriorityQueue based on arrivalTimes.
         */
        Statistics stats = new Statistics();
        PriorityQueue<Event> pq = new PriorityQueue<Event>(arrivalTimes.size(),
                new EventComparator());
        for (int i = 0; i < arrivalTimes.size(); i++) {
            Event nextEvent = new Event(new Customer(i + 1, arrivalTimes.get(i)));
            pq.add(nextEvent);
        }

        /*
         * Processing events.
         */
        while (!pq.isEmpty()) {
            Event event = pq.poll();
            if (event.isArrive()) {
                Event handledEvent = arriveHandler(event);
                pq.add(handledEvent);
            } else if (event.isServe()) {
                Event doneEvent = serveHandler(event);
                pq.add(doneEvent);
                stats = stats.addServed(event.getTime() - event.getCustomer().getArrivalTime());
            } else if (event.isDone()) {
                Event nextEvent = doneHandler(event);
                if (nextEvent.isServe()) {
                    pq.add(nextEvent);
                }
            } else if (event.isWait()) {
                waitHandler(event);
            } else if (event.isLeave()) {
                stats = stats.addLeft();
            }
            System.out.println(event);
        }
        /*
         * Print out stats after finishing simulation.
         */
        System.out.println(stats);
    }

    /**
     * Level 2.
     * @param arrivalTimes list of customer arrivalTimes
     * @param serviceTimes list of customer serviceTimes
     */
    public void simulate(List<Double> arrivalTimes, List<Double> serviceTimes) {

        /*
         * Creates new Statistics and PriorityQueue based on arrivalTimes.
         */
        Statistics stats = new Statistics();
        PriorityQueue<Event> pq = new PriorityQueue<Event>(arrivalTimes.size(),
                new EventComparator());
        for (int i = 0; i < arrivalTimes.size(); i++) {
            Event nextEvent = new Event(new Customer(i + 1, arrivalTimes.get(i),
                    serviceTimes.get(i)));
            pq.add(nextEvent);
        }

        /*
         * Processing events.
         */
        while (!pq.isEmpty()) {
            Event event = pq.poll();
            if (event.isArrive()) {
                Event handledEvent = arriveHandler(event);
                pq.add(handledEvent);
            } else if (event.isServe()) {
                Event doneEvent = serveHandler(event);
                pq.add(doneEvent);
                stats = stats.addServed(event.getTime() - event.getCustomer().getArrivalTime());
            } else if (event.isDone()) {
                Event nextEvent = doneHandler(event);
                if (nextEvent.isServe()) {
                    pq.add(nextEvent);
                }
            } else if (event.isWait()) {
                waitHandler(event);
            } else if (event.isLeave()) {
                stats = stats.addLeft();
            }
            System.out.println(event);
        }
        /*
         * Print out stats after finishing simulation.
         */
        System.out.println(stats);
    }

    /**
     * Level 3.
     * @param arrivalTimes list of customer arrivalTimes
     * @param serviceTimes list of customer serviceTimes
     * @param serverRestTimes list of server rest times, inclusive of 0
     */
    public void simulate(List<Double> arrivalTimes,
                         List<Double> serviceTimes, List<Double> serverRestTimes) {

        /*
         * Creates new Statistics and PriorityQueue based on arrivalTimes.
         * restIndex to fetch corresponding serverRest
         */
        Statistics stats = new Statistics();
        PriorityQueue<Event> pq = new PriorityQueue<Event>(arrivalTimes.size(),
                new EventComparator());
        for (int i = 0; i < arrivalTimes.size(); i++) {
            Event nextEvent = new Event(new Customer(i + 1, arrivalTimes.get(i),
                    serviceTimes.get(i)));
            pq.add(nextEvent);
        }
        int restIndex = -1;

        /*
         * Processing events.
         */
        while (!pq.isEmpty()) {
            Event event = pq.poll();
            if (event.isArrive()) {
                Event handledEvent = arriveHandler(event);
                pq.add(handledEvent);
            } else if (event.isServe()) {
                Event doneEvent = serveHandler(event);
                pq.add(doneEvent);
                stats = stats.addServed(event.getTime() - event.getCustomer().getArrivalTime());
            } else if (event.isDone()) {
                restIndex += 1;
                Event nextEvent = doneHandler(event, serverRestTimes, restIndex);
                if (nextEvent.isServe() || nextEvent.isServerRest()) {
                    pq.add(nextEvent);
                }
            } else if (event.isWait()) {
                waitHandler(event);
            } else if (event.isLeave()) {
                stats = stats.addLeft();
            } else if (event.isServerRest()) {
                Event backEvent = restHandler(event, serverRestTimes, restIndex);
                pq.add(backEvent);
            } else if (event.isServerBack()) {
                Event nextEvent = backHandler(event);
                if (nextEvent.isServe()) {
                    pq.add(nextEvent);
                }
            }
            if (!event.isServerRest() && !event.isServerBack()) {
                System.out.println(event);
            }
        }
        /*
         * Print out stats after finishing simulation.
         */
        System.out.println(stats);
    }

    /**
     * Level 4.
     * @param numSelfCheckoutServers number of SelfCheckoutServer to generate
     * @param arrivalTimes list of customer arrivalTimes
     * @param serviceTimes list of customer serviceTimes
     * @param serverRestTimes list of server rest times, inclusive of 0
     */
    public void simulate(int numSelfCheckoutServers, List<Double> arrivalTimes,
                         List<Double> serviceTimes, List<Double> serverRestTimes) {

        /*
         * Adds new SelfCheckoutServers to serverList.
         * Creates new Statistics and PriorityQueue based on arrivalTimes.
         * restIndex to fetch corresponding serverRest
         */
        int firstSelfCheck = this.serverList.size();
        for (int i = 0; i < numSelfCheckoutServers; i++) {
            Server nextServer = new SelfCheckoutServer(i + firstSelfCheck + 1, this.maxQueue);
            this.serverList.add(nextServer);
        }
        Statistics stats = new Statistics();
        PriorityQueue<Event> pq = new PriorityQueue<Event>(arrivalTimes.size(),
                new EventComparator());
        for (int i = 0; i < arrivalTimes.size(); i++) {
            Event nextEvent = new Event(new Customer(i + 1, arrivalTimes.get(i),
                    serviceTimes.get(i)));
            pq.add(nextEvent);
        }
        int restIndex = -1;

        /*
         * processing events
         */
        while (!pq.isEmpty()) {
            Event event = pq.poll();
            if (event.isArrive()) {
                Event handledEvent = arriveHandler(event);
                pq.add(handledEvent);
            } else if (event.isServe()) {
                Event doneEvent = serveHandler(event);
                pq.add(doneEvent);
                stats = stats.addServed(event.getTime() - event.getCustomer().getArrivalTime());
            } else if (event.isDone()) {
                restIndex += 1;
                Event nextEvent = doneHandler(event, serverRestTimes, restIndex);
                if (nextEvent.isServe() || nextEvent.isServerRest()) {
                    pq.add(nextEvent);
                }
            } else if (event.isWait()) {
                waitHandler(event);
            } else if (event.isLeave()) {
                stats = stats.addLeft();
            } else if (event.isServerRest()) {
                Event backEvent = restHandler(event, serverRestTimes, restIndex);
                if (event.getServer().isSelfCheckout()) {
                    restIndex -= 1;
                }
                pq.add(backEvent);
            } else if (event.isServerBack()) {
                Event nextEvent = backHandler(event);
                if (nextEvent.isServe()) {
                    pq.add(nextEvent);
                }
            }
            if (!event.isServerRest() && !event.isServerBack()) {
                if (event.isWait() && event.getServer().isSelfCheckout()) {
                    String selfCheck = String.format("%.3f %d waits at %s",
                            event.getTime(), event.getCustomer().getCustomerId(),
                            this.serverList.get(firstSelfCheck).toString());
                    System.out.println(selfCheck);
                } else {
                    System.out.println(event);
                }
            }
        }
        /*
         * print out stats after finishing simulation
         */
        System.out.println(stats);

    }

    /**
     * Level 5.
     * @param baseSeed for RandomGenerator object
     * @param numSelfCheckoutServers number of SelfCheckoutServer to generate
     * @param numCustomers number of Customer to generate
     * @param arrivalRate parameter for the arrival rate, lambda
     * @param serviceRate parameter for the service rate, mu
     * @param restingRate parameter for the resting rate, rho
     * @param probRest probability of resting
     * @param probGreedy probability of a GreedyCustomer occurring
     */
    public void simulate(int baseSeed, int numSelfCheckoutServers, int numCustomers,
                         Double arrivalRate, Double serviceRate, Double restingRate,
                         Double probRest, Double probGreedy) {

        /*
         * Creates RandomGenerator instance.
         * Adds new SelfCheckoutServers to serverList.
         * Creates new Statistics and PriorityQueue.
         * Generates new Customer/GreedyCustomer and adds to pq.
         */
        RandomGenerator rng = new RandomGenerator(baseSeed, arrivalRate, serviceRate, restingRate);
        int firstSelfCheck = this.serverList.size();
        for (int i = 0; i < numSelfCheckoutServers; i++) {
            Server nextServer = new SelfCheckoutServer(i + firstSelfCheck + 1, this.maxQueue);
            this.serverList.add(nextServer);
        }
        Statistics stats = new Statistics();
        PriorityQueue<Event> pq = new PriorityQueue<Event>(new EventComparator());
        /*
         * Preparing arrivalTimes.
         */
        List<Double> arrivalTimes = new ArrayList<Double>();
        arrivalTimes.add(0.0);
        for (int j = 1; j < numCustomers; j++) {
            Double arrivalTime = rng.genInterArrivalTime();
            arrivalTimes.add(arrivalTime + arrivalTimes.get(arrivalTimes.size() - 1));
        }
        /*
         * Preparing Customer/GreedyCustomer.
         */
        for (int k = 0; k < numCustomers; k++) {
            Double randomNumber = rng.genCustomerType();
            Customer newCustomer;
            if (randomNumber < probGreedy) {
                newCustomer = new GreedyCustomer(k + 1, arrivalTimes.get(k));
            } else {
                newCustomer = new Customer(k + 1, arrivalTimes.get(k));
            }
            Event nextEvent = new Event(newCustomer);
            pq.add(nextEvent);
        }

        /*
         * Processing events.
         */
        while (!pq.isEmpty()) {
            Event event = pq.poll();
            if (event.isArrive()) {
                Event handledEvent = arriveHandler(event);
                pq.add(handledEvent);
            } else if (event.isServe()) {
                Event doneEvent = serveHandler(event, rng);
                pq.add(doneEvent);
                stats = stats.addServed(event.getTime() - event.getCustomer().getArrivalTime());
            } else if (event.isDone()) {
                Event nextEvent = doneHandler(event, rng, probRest);
                if (nextEvent.isServe() || nextEvent.isServerRest()) {
                    pq.add(nextEvent);
                }
            } else if (event.isWait()) {
                waitHandler(event);
            } else if (event.isLeave()) {
                stats = stats.addLeft();
            } else if (event.isServerRest()) {
                Event backEvent = restHandler(event, rng);
                pq.add(backEvent);
            } else if (event.isServerBack()) {
                Event nextEvent = backHandler(event);
                if (nextEvent.isServe()) {
                    pq.add(nextEvent);
                }
            }
            if (!event.isServerRest() && !event.isServerBack()) {
                if (event.isWait() && event.getServer().isSelfCheckout()) {
                    String selfCheck = String.format("%.3f %s waits at %s",
                            event.getTime(), event.getCustomer().toString(),
                            this.serverList.get(firstSelfCheck).toString());
                    System.out.println(selfCheck);
                } else {
                    System.out.println(event);
                }
            }
        }
        /*
         * Print out stats after finishing simulation.
         */
        System.out.println(stats);

    }

    /**
     * Checks server availability based on Customer arrivalTime.
     * generates corresponding event
     * able to handle GreedyCustomer
     * @param event ARRIVE event
     * @return depending on whether servers are available, returns SERVE, WAIT or LEAVE event
     */
    Event arriveHandler(Event event) {
        Customer customer = event.getCustomer();
        Double customerArrivalTime = customer.getArrivalTime();
        Server server;
        if (customer.isGreedy()) {
            server = greedySelectServer(customerArrivalTime);
        } else {
            server = selectServer(customerArrivalTime);
        }
        if (server.getServerId() == -1) {
            Event leaveEvent = event.leave();
            return leaveEvent;
        }
        if (server.canServe(customerArrivalTime) == 1) {
            Event serveEvent = event.serve(customer, customerArrivalTime, server);
            return serveEvent;
        } else if (server.canServe(customerArrivalTime) == 0) {
            Event waitEvent = event.wait(server);
            return waitEvent;
        }
        Event leaveEvent = event.leave();
        return leaveEvent;
    }

    /**
     * Updates server with waiting Customer.
     * @param event WAIT event
     */
    void waitHandler(Event event) {
        Server server = event.getServer();
        Customer customer = event.getCustomer();
        server = server.addCustomer(customer);
        updateServerList(server);
    }

    /**
     * Updates server time and generates DONE event.
     * @param event SERVE event
     * @return DONE event
     */
    Event serveHandler(Event event) {
        Server server = event.getServer();
        Double endTime = event.getTime() + event.getCustomer().getServiceTime();
        server = server.updateTime(endTime);
        updateServerList(server);
        Event doneEvent = event.done(endTime);
        return doneEvent;
    }

    /**
     * Overloaded method for level 5.
     * Updates server time and generates DONE event.
     * @param event SERVE event
     * @param rng RandomGenerator instance used to generate service time
     * @return DONE event
     */
    Event serveHandler(Event event, RandomGenerator rng) {
        Server server = event.getServer();
        Double serviceTime = rng.genServiceTime();
        Double endTime = event.getTime() + serviceTime;
        server = server.updateTime(endTime);
        updateServerList(server);
        Event doneEvent = event.done(endTime);
        return doneEvent;
    }

    /**
     * Checks if server that just finished serving has a next Customer.
     * if yes, generates corresponding SERVE event
     * @param event DONE event
     * @return SERVE event
     */
    Event doneHandler(Event event) {
        Server server = event.getServer();
        Customer nextCustomer = server.getCustomer();
        server = server.serveCustomer(event.getCustomer());
        updateServerList(server);
        Double time = event.getTime();
        if (nextCustomer.getCustomerId() != -1) {
            Event serveEvent = event.serve(nextCustomer, time, server);
            return serveEvent;
        } else {
            return event;
        }
    }

    /**
     * Overloaded method for level 3 & level 4.
     * checks if server that just finished serving needs to rest
     * if yes, generates corresponding SERVERREST event
     * if no, checks if there is a next customer
     * if yes, generates corresponding SERVE event
     * @param event DONE event
     * @param serverRestTimes list of serverRestTimes
     * @param restIndex current restIndex to fetch
     * @return SERVE or SERVERREST event
     */
    Event doneHandler(Event event, List<Double> serverRestTimes, int restIndex) {
        Double time = event.getTime();
        if (restIndex < serverRestTimes.size()) {
            Event restEvent = event.serverRest(time);
            return restEvent;
        } else {
            Server server = event.getServer();
            Customer nextCustomer = server.getCustomer();
            server = server.serveCustomer(event.getCustomer());
            updateServerList(server);
            if (nextCustomer.getCustomerId() != -1) {
                Event serveEvent = event.serve(nextCustomer, time, server);
                return serveEvent;
            } else {
                return event;
            }
        }
    }

    /**
     * Overloaded method for level 5.
     * @param event DONE event
     * @param rng RandomGenerator instance used to check if server needs to rest
     * @param probRest probability of resting to test against
     * @return SERVE or SERVERREST event
     */
    Event doneHandler(Event event, RandomGenerator rng, Double probRest) {
        Double time = event.getTime();
        Server server = event.getServer();
        /*
         * If server is SelfCheckoutServer, do not fetch rest chance from rng.
         */
        if (server.isSelfCheckout()) {
            Customer nextCustomer = server.getCustomer();
            server = server.serveCustomer(event.getCustomer());
            updateServerList(server);
            if (nextCustomer.getCustomerId() != -1) {
                Event serveEvent = event.serve(nextCustomer, time, server);
                return serveEvent;
            } else {
                return event;
            }
        } else {
            Double randomNumber = rng.genRandomRest();
            if (randomNumber < probRest) {
                Event restEvent = event.serverRest(time);
                return restEvent;
            } else {
                Customer nextCustomer = server.getCustomer();
                server = server.serveCustomer(event.getCustomer());
                updateServerList(server);
                if (nextCustomer.getCustomerId() != -1) {
                    Event serveEvent = event.serve(nextCustomer, time, server);
                    return serveEvent;
                } else {
                    return event;
                }
            }
        }
    }

    /**
     * Updates server with endRestTime, unless server is SelfCheckoutServer.
     * generates corresponding SERVERBACK event
     * @param event SERVERREST event
     * @param serverRestTimes list of serverRestTimes
     * @param restIndex current restIndex to fetch
     * @return SERVERBACK event
     */
    Event restHandler(Event event, List<Double> serverRestTimes, int restIndex) {
        Double addedTime = 0.0;
        Server server = event.getServer();
        if (!(server.isSelfCheckout())) {
            addedTime = serverRestTimes.get(restIndex);
        }
        Double endRestTime = event.getTime() + addedTime;
        server = server.updateTime(endRestTime);
        updateServerList(server);
        Event backEvent = event.serverBack(endRestTime);
        return backEvent;
    }

    /**
     * Overloaded method for level 5.
     * Updates server with endRestTime, unless server is SelfCheckoutServer.
     * generates corresponding SERVERBACK event
     * @param event SERVERREST event
     * @param rng RandomGenerator instance used to generate rest time
     * @return SERVERBACK event
     */
    Event restHandler(Event event, RandomGenerator rng) {
        Double addedTime = 0.0;
        Server server = event.getServer();
        if (!(server.isSelfCheckout())) {
            addedTime = rng.genRestPeriod();
        }
        Double endRestTime = event.getTime() + addedTime;
        server = server.updateTime(endRestTime);
        updateServerList(server);
        Event backEvent = event.serverBack(endRestTime);
        return backEvent;
    }

    /**
     * Checks if server that just finished resting has a next Customer.
     * if yes, generates corresponding SERVE event
     * @param event SERVERBACK event
     * @return SERVE event
     */
    Event backHandler(Event event) {
        Server server = event.getServer();
        Customer nextCustomer = server.getCustomer();
        server = server.serveCustomer(event.getCustomer());
        updateServerList(server);
        Double time = event.getTime();
        if (nextCustomer.getCustomerId() != -1) {
            Event serveEvent = event.serve(nextCustomer, time, server);
            return serveEvent;
        } else {
            return event;
        }
    }
}