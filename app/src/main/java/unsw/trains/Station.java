package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public abstract class Station {
    private String id;
    private String type;
    private double x;
    private double y;
    private int maxTrains;
    private List<String> stoppedTrains;
    private List<Passenger> passengers;
    private List<Cargo> cargo;

    public Station(String id, String type, double x, double y, int maxTrains) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.maxTrains = maxTrains;
        this.stoppedTrains = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.cargo = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getMaxTrains() {
        return maxTrains;
    }

    public void addTrain(String trainId) {
        stoppedTrains.add(trainId);
    }

    public void removeTrain(String trainId) {
        stoppedTrains.remove(trainId);
    }

    public List<String> getStoppedTrains() {
        return new ArrayList<>(stoppedTrains);
    }

    public boolean canStopTrain() {
        return stoppedTrains.size() < maxTrains;
    }

    public abstract boolean canLoadPassengers();

    public abstract boolean canLoadCargo();

    public void addPassenger(Passenger passenger) {
        if (canLoadPassengers()) {
            passengers.add(passenger);
        }
    }

    public void removePassenger(String passengerId) {
        if (canLoadPassengers()) {
            passengers.removeIf(p -> p.getId().equals(passengerId));
        }
    }

    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    public void addCargo(Cargo cargoItem) {
        if (canLoadCargo()) {
            cargo.add(cargoItem);
        }
    }

    public void removeCargo(String cargoId) {
        if (canLoadCargo()) {
            cargo.removeIf(c -> c.getId().equals(cargoId));
        }
    }

    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }
}
