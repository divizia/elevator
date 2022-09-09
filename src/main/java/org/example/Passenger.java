package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Passenger {

    private static int passengers;

    private int id;

    @Setter
    private int desiredFloor;

    public boolean canDesireCurrentFloor;

    public Passenger() {
        id = ++passengers;
        desiredFloor = 1;
    }

    public Passenger(boolean canDesireCurrentFloor) {
        this();
        this.canDesireCurrentFloor = canDesireCurrentFloor;
    }

    public void setRandomDesiredFloor(int maxFloor, int currentFloor) {
        do {
            this.desiredFloor = (int) (Math.random() * maxFloor + 1);
        }
        while (!canDesireCurrentFloor && this.desiredFloor == currentFloor);
    }
}
