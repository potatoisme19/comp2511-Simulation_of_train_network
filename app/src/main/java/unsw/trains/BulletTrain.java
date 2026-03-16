package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public class BulletTrain extends Train {
    private List<Passenger> passengers;
    private List<Cargo> cargo;
    private int maxPassengers;
    private int maxCargoWeight;
    private int currentCargoWeight;
    private static final int MAX_CARGO_WEIGHT = 5000;

    public BulletTrain(String id, String stationId, List<String> route,
            double speed, boolean isCyclical, double x, double y, int maxPassengers) {
        super(id, stationId, route, speed, isCyclical, x, y);
        this.passengers = new ArrayList<>();
        this.cargo = new ArrayList<>();
        this.maxPassengers = maxPassengers;
        this.maxCargoWeight = MAX_CARGO_WEIGHT;
        this.currentCargoWeight = 0;
    }

    @Override
    public String getType() {
        return "BulletTrain";
    }

    @Override
    public int getCurrentLoad() {
        int passengerWeight = passengers.size() * 70;
        return passengerWeight + currentCargoWeight;
    }

    @Override
    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    @Override
    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    @Override
    public boolean canBoardPassenger() {
        return true;
    }

    @Override
    public boolean canBoardCargo() {
        return true;
    }

    @Override
    public boolean isRepairTrain() {
        for (Passenger p : passengers) {
            if (p.isMechanic())
                return true;
        }
        return false;
    }

    @Override
    public int getMaxPassengerCapacity() {
        return maxPassengers;
    }

    @Override
    public int getMaxCargoCapacity() {
        return maxCargoWeight;
    }

    @Override
    public void addPassenger(Passenger p) {
        if (passengers.size() < maxPassengers) {
            passengers.add(p);
        }
    }

    @Override
    public void addCargo(Cargo c) {
        if (currentCargoWeight + c.getWeight() <= maxCargoWeight) {
            cargo.add(c);
            currentCargoWeight += c.getWeight();
        }
    }

    @Override
    public void removePassenger(Passenger p) {
        passengers.remove(p);
    }

    @Override
    public void removeCargo(Cargo c) {
        if (cargo.remove(c)) {
            currentCargoWeight -= c.getWeight();
        }
    }

    @Override
    public void removePassenger(String passengerId) {
        passengers.removeIf(p -> p.getId().equals(passengerId));
    }

    @Override
    public void removeCargo(String cargoId) {
        cargo.removeIf(c -> c.getId().equals(cargoId));
        currentCargoWeight = 0;
        for (Cargo c : cargo) {
            currentCargoWeight += c.getWeight();
        }
    }

    @Override
    public void removeArrivedPassengers(String stationId) {
        passengers.removeIf(p -> p.getDestStationId().equals(stationId));
    }

    @Override
    public void removeArrivedCargo(String stationId) {
        List<Cargo> toRemove = new ArrayList<>();
        for (Cargo c : cargo) {
            if (c.getDestStationId().equals(stationId)) {
                toRemove.add(c);
            }
        }
        for (Cargo c : toRemove) {
            removeCargo(c);
        }
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }

    public int getMaxCargoWeight() {
        return maxCargoWeight;
    }
}
