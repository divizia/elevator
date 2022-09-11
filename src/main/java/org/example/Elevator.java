package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;


@EqualsAndHashCode
@ToString
public class Elevator {

    @Getter
    private int maxPassengers;

    private ArrayList<Passenger> passengers;

    @Getter
    private Set<Integer> callsUp;

    @Getter
    private Set<Integer> callsDown;

    public Elevator(int maxPassengers) {
        if (maxPassengers < 1)
            throw new IllegalArgumentException("Max passengers must be > 0");

        this.maxPassengers = maxPassengers;
        this.passengers = new ArrayList<>(maxPassengers);

        this.callsUp = new HashSet<>();
        this.callsDown = new HashSet<>();
    }

    /**
     * Returns an unmodifiable view on the list of elements.
     * Changes in this object will be visible in the returned list.
     */
    public List<Passenger> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    public int getPassengersSize() {
        return passengers.size();
    }

    public boolean addPassenger(Passenger passenger) {
        if (passengers.size() < maxPassengers)
            return passengers.add(passenger);
        else
            return false;
    }

    public boolean removePassenger(Passenger passenger) {
        return passengers.remove(passenger);
    }

    public boolean removeAllPassengers(List<Passenger> passengers) {
        return this.passengers.removeAll(passengers);
    }

    public void addCall(Passenger passenger, int currentFloor) {
        if (passenger.getDesiredFloor() > currentFloor)
            callsUp.add(currentFloor);
        else if (passenger.getDesiredFloor() < currentFloor)
            callsDown.add(currentFloor);
    }

}
