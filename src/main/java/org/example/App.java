package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Paths.get("application.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int minRange = Integer.valueOf(properties.getProperty("building.floor.minRange"));
        int maxRange = Integer.valueOf(properties.getProperty("building.floor.maxRange"));
        int maxPassengersPerFloor = Integer.valueOf(properties.getProperty("building.floor.maxPassengers"));
        int maxPassengersInElevator = Integer.valueOf(properties.getProperty("building.maxPassengersInElevator"));

        int maxFloor = (int) (Math.random() * (maxRange - minRange + 1) + minRange);
        int passengersPerFloor = (int) (Math.random() * (maxPassengersPerFloor + 1));

        Building building = new Building(maxFloor, maxPassengersInElevator);
        building.setFrameFrequency(Integer.valueOf(properties.getProperty("building.frameFrequency")));

        boolean canDesireCurrentFloor = Boolean.valueOf(properties.getProperty("passenger.canDesireCurrentFloor"));
        for (int i = 0; i < maxFloor; i++) {
            for (int j = 0; j < passengersPerFloor; j++) {
                Passenger passenger = new Passenger(canDesireCurrentFloor);
                passenger.setRandomDesiredFloor(maxFloor, i + 1);
                building.addPassengerToFloor(i + 1, passenger);
            }
        }

        while (!building.allPassengersOnTheirFloor())
            building.doStep();

    }

}
