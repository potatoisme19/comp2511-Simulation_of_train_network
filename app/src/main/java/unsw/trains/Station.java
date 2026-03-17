package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Station {
    private String id;
    private String type;
    private double x;
    private double y;
    private int maxTrains;
    private List<String> stoppedTrains;
    private List<Load> loads;

    public Station(String id, String type, double x, double y, int maxTrains) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.maxTrains = maxTrains;
        this.stoppedTrains = new ArrayList<>();
        this.loads = new ArrayList<>(); // Now using Load list
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

    public void addLoad(Load load) {
        if (load instanceof Passenger && canLoadPassengers()) {
            loads.add(load);
        } else if (load instanceof Cargo && canLoadCargo()) {
            loads.add(load);
        }
    }

    public void removeLoad(String loadId) {
        loads.removeIf(l -> l.getId().equals(loadId));
    }

    public List<Load> getLoads() {
        return new ArrayList<>(loads);
    }

    public void addPassenger(Passenger passenger) {
        if (canLoadPassengers()) {
            loads.add(passenger);
        }
    }

    public void removePassenger(String passengerId) {
        if (canLoadPassengers()) {
            loads.removeIf(l -> l instanceof Passenger && l.getId().equals(passengerId));
        }
    }

    public List<Passenger> getPassengers() {
        return loads.stream()
                .filter(l -> l instanceof Passenger)
                .map(l -> (Passenger) l)
                .collect(Collectors.toList());
    }

    public void addCargo(Cargo cargoItem) {
        if (canLoadCargo()) {
            loads.add(cargoItem);
        }
    }

    public void removeCargo(String cargoId) {
        if (canLoadCargo()) {
            loads.removeIf(l -> l instanceof Cargo && l.getId().equals(cargoId));
        }
    }

    public List<Cargo> getCargo() {
        return loads.stream()
                .filter(l -> l instanceof Cargo)
                .map(l -> (Cargo) l)
                .collect(Collectors.toList());
    }

    public void tick() {
        List<Load> expired = new ArrayList<>();
        for (Load load : loads) {
            load.tick();
            if (load.hasExpired()) {
                expired.add(load);
            }
        }
        loads.removeAll(expired);
    }
}
