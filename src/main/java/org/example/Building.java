package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@EqualsAndHashCode
@ToString
public class Building {

    @Getter
    private final int maxFloor;

    @Getter
    private Elevator elevator;

    //In Ukraine, floors always starts from 1
    @Getter
    private int currentFloor = 1;

    @Getter
    private int steps;

    @Getter
    private boolean elevatorIsMovingUp;

    @Getter
    @Setter
    private int frameFrequency;

    private final TreeMap<Integer, List<Passenger>> floors;

    private final int maxFloorNumberLength;

    private boolean elevatorIsStopped = true;

    public Building(int maxFloor) {
        if (maxFloor < 2)
            throw new IllegalArgumentException("Max floor must be > 1");

        this.maxFloor = maxFloor;

        this.floors = new TreeMap(Comparator.reverseOrder());
        for (int i = 0; i < maxFloor; i++) {
            this.floors.put(i + 1, new ArrayList<>());
        }

        maxFloorNumberLength = numberLength(maxFloor);
    }

    public Building(int maxFloor, int maxPassengersInElevator) {
        this(maxFloor);

        this.elevator = new Elevator(maxPassengersInElevator);
        this.elevatorIsMovingUp = true;
    }

    public boolean setElevator(Elevator elevator) {
        if (this.elevator == null) {
            this.elevator = elevator;
            return true;
        } else {
            return false;
        }
    }

    public void addPassengerToFloor(int floor, Passenger passenger) {
        floors.get(floor).add(passenger);
        elevator.addCall(passenger, floor);
    }

    public boolean allPassengersOnTheirFloor() {
        return elevator.getPassengersSize() == 0 && elevator.getCallsUp().size() <= 0 && elevator.getCallsDown().size() <= 0;
    }

    public void doStep() {
        if (elevator == null)
            return;

        if (elevatorIsStopped)
            movePassengersToElevator();

        moveElevator();

        if (elevatorIsStopped)
            movePassengersToFloor();
    }

    private void moveElevator() {
        if (elevatorIsMovingUp)
            elevatorMoveUp();
        else
            elevatorMoveDown();

        tryToStopElevator();

        steps++;
        drawBuilding();
    }

    private void tryToStopElevator() {
        elevatorIsStopped =
                (elevatorIsMovingUp ? elevator.getCallsUp().contains(currentFloor) : elevator.getCallsDown().contains(currentFloor))
                        && elevator.getPassengersSize() < elevator.getMaxPassengers()
                        || elevator.getPassengers().stream().anyMatch(x -> x.getDesiredFloor() == currentFloor);
    }

    private void elevatorMoveUp() {
        if (currentFloor + 1 <= maxFloor) {
            currentFloor++;
        }

        elevatorIsMovingUp = currentFloor != maxFloor;
    }

    private void elevatorMoveDown() {
        if (currentFloor - 1 >= 1) {
            currentFloor--;
        }

        elevatorIsMovingUp = currentFloor == 1;
    }

    private boolean movePassengersToElevator() {
        if (elevator.getMaxPassengers() == elevator.getPassengersSize())
            return false;

        List<Passenger> passengersOnFloor = floors.get(currentFloor);

        if (elevator.getPassengersSize() == 0) {
            int passengersWantMovingUp = 0;
            int passengersWantMovingDown = 0;

            for (Passenger passenger : passengersOnFloor) {
                if (passenger.getDesiredFloor() > currentFloor)
                    passengersWantMovingUp++;
                else if (passenger.getDesiredFloor() < currentFloor)
                    passengersWantMovingDown++;
            }

            elevatorIsMovingUp = passengersWantMovingUp > passengersWantMovingDown;

            putWillingPassengersInElevator(elevator.getMaxPassengers(), passengersOnFloor);

        } else {
            putWillingPassengersInElevator(elevator.getMaxPassengers() - elevator.getPassengersSize(), passengersOnFloor);
        }

        steps++;
        drawBuilding();

        return true;
    }

    private void putWillingPassengersInElevator(int limit, List<Passenger> passengersOnFloor) {
        List<Passenger> passengersToRemoveFromFloor = new ArrayList<>();
        boolean cleanCall = true;

        for (Passenger passenger : passengersOnFloor) {
            if (elevatorIsMovingUp ? passenger.getDesiredFloor() > currentFloor : passenger.getDesiredFloor() < currentFloor) {
                if (limit-- != 0) {
                    elevator.addPassenger(passenger);
                    passengersToRemoveFromFloor.add(passenger);
                } else {
                    cleanCall = false;
                    break;
                }
            }
        }

        if (cleanCall) {
            if (elevatorIsMovingUp)
                elevator.getCallsUp().remove(currentFloor);
            else
                elevator.getCallsDown().remove(currentFloor);
        }

        passengersOnFloor.removeAll(passengersToRemoveFromFloor);
    }

    private boolean movePassengersToFloor() {
        if (elevator.getPassengersSize() == 0)
            return false;

        List<Passenger> passengersOnFloor = floors.get(currentFloor);
        List<Passenger> passengersToRemoveFromElevator = new ArrayList<>();

        for (Passenger passenger : elevator.getPassengers()) {
            if (passenger.getDesiredFloor() == currentFloor) {
                passengersOnFloor.add(passenger);
                passengersToRemoveFromElevator.add(passenger);

                passenger.setRandomDesiredFloor(maxFloor, currentFloor);
                elevator.addCall(passenger, currentFloor);
            }
        }

        elevator.removeAllPassengers(passengersToRemoveFromElevator);

        if (passengersToRemoveFromElevator.size() > 0) {
            steps++;
            drawBuilding();
        }

        return true;
    }

    private void drawBuilding() {
        if (frameFrequency > 0) {
            try {
                Thread.sleep(frameFrequency);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        StringBuilder drawFloor = new StringBuilder(spaces(7 + maxFloorNumberLength));

        drawFloor.append(String.format("*** Step %s ***\n\n", steps));

        for (Map.Entry<Integer, List<Passenger>> entry : floors.entrySet()) {

            boolean isElevatorOnCurrentFloor = entry.getKey() == currentFloor;
            int currentFloorNumberLength = numberLength(entry.getKey());

            drawFloor
                    .append(isElevatorOnCurrentFloor ? elevatorIsMovingUp ? "↑ " : "↓ " : "  ")
                    .append(isElevatorOnCurrentFloor ? elevatorIsStopped ? "● " : "◌ " : "  ")
                    .append(spaces(maxFloorNumberLength - currentFloorNumberLength))
                    .append(entry.getKey())
                    .append(" |");

            if (isElevatorOnCurrentFloor) {
                elevator.getPassengers().forEach(x -> drawFloor
                        .append(spaces(maxFloorNumberLength - numberLength(x.getDesiredFloor()) + 1))
                        .append(x.getDesiredFloor()));

                int freePlaces = elevator.getMaxPassengers() - elevator.getPassengersSize();
                for (int j = 0; j < freePlaces; j++) {
                    drawFloor.append(spaces(maxFloorNumberLength)).append(".");
                }
            } else {
                for (int i = 0; i < elevator.getMaxPassengers(); i++) {
                    drawFloor.append(spaces(maxFloorNumberLength + 1));
                }
            }

            drawFloor.append(" |");
            entry.getValue().forEach(x -> drawFloor
                    .append(spaces(maxFloorNumberLength - numberLength(x.getDesiredFloor()) + 1))
                    .append(x.getDesiredFloor()));

            drawFloor.append("\n");
        }

        System.out.println(drawFloor);
    }

    private String spaces(int count) {
        return " ".repeat(Math.max(0, count));
    }

    private int numberLength(int number) {
        return (int) Math.ceil(Math.log10(number + 0.5));
    }

}
