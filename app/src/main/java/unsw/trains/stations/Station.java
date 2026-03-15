package unsw.trains.stations;

import java.util.ArrayList;
import java.util.List;

import unsw.trains.Cargo;
import unsw.trains.Passenger;

public abstract class Station {
    private String id;
    private double x;
    private double y;
    private int maxTrains;
    private List<String> stoppedTrains = new ArrayList<>();
    private List<Passenger> passengers = new ArrayList<>();
    private List<Cargo> cargo = new ArrayList<>();

    public Station(String id, double x, double y, int maxTrains) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.maxTrains = maxTrains;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean canStopTrain() {
        return stoppedTrains.size() < maxTrains;
    }

    public void addTrain(String trainId) {
        if (canStopTrain()) {
            stoppedTrains.add(trainId);
        }
    }

    public void removeTrain(String trainId) {
        stoppedTrains.remove(trainId);
    }

    public List<String> getStoppedTrains() {
        return new ArrayList<>(stoppedTrains);
    }

    public abstract boolean canLoadPassengers();

    public abstract boolean canLoadCargo();

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

    public void removePassenger(String passengerId) {
        passengers.removeIf(p -> p.getId().equals(passengerId));
    }

    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    public void addCargo(Cargo cargoItem) {
        cargo.add(cargoItem);
    }

    public void removeCargo(String cargoId) {
        cargo.removeIf(c -> c.getId().equals(cargoId));
    }

    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }
}
