package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
     * For add or remove passengers to/from elevator, use
     * addPassenger, removePassenger.
     * This method return COPY of passenger list
     *
     * @return copy of passenger list
     */
    public ArrayList<Passenger> getPassengers() {
        return (ArrayList<Passenger>) passengers.clone();
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
