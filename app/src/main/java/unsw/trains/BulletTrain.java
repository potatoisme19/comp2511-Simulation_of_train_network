package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public class BulletTrain extends Train {
    private List<Passenger> passengers;
    private List<Cargo> cargo;
    private int maxPassengers;
    private int maxCargoWeight;
    private int currentCargoWeight;
    private static final int PASSENGER_WEIGHT = 70;

    public BulletTrain(String id, String stationId, List<String> route,
            double speed, boolean isCyclical, double x, double y, int maxPassengers) {
        super(id, stationId, route, speed, isCyclical, x, y);
        this.passengers = new ArrayList<>();
        this.cargo = new ArrayList<>();
        this.maxPassengers = maxPassengers;
        this.maxCargoWeight = 5000;
        this.currentCargoWeight = 0;
    }

    @Override
    public String getNextStation() {
        if (isCyclical()) {
            int nextIndex = (getRouteIndex() + 1) % getRoute().size();
            return getRoute().get(nextIndex);
        } else {
            int nextIndex = calculateNextIndex();
            return getRoute().get(nextIndex);
        }
    }

    @Override
    public String getType() {
        return "BulletTrain";
    }

    @Override
    public int getCurrentLoad() {
        return (passengers.size() * PASSENGER_WEIGHT) + currentCargoWeight;
    }

    @Override
    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    @Override
    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }

    public int getMaxCargoWeight() {
        return maxCargoWeight;
    }

    public void addPassenger(Passenger passenger) {
        if (passengers.size() < maxPassengers) {
            passengers.add(passenger);
        }
    }

    public void removePassenger(Passenger passenger) {
        passengers.remove(passenger);
    }

    public void removePassenger(String passengerId) {
        passengers.removeIf(p -> p.getId().equals(passengerId));
    }

    public void removeArrivedPassengers(String stationId) {
        passengers.removeIf(p -> p.getDestStationId().equals(stationId));
    }

    public void addCargo(Cargo newCargo) {
        if (currentCargoWeight + newCargo.getWeight() <= maxCargoWeight) {
            cargo.add(newCargo);
            currentCargoWeight += newCargo.getWeight();
        }
    }

    public void removeCargo(Cargo cargoToRemove) {
        if (cargo.remove(cargoToRemove)) {
            currentCargoWeight -= cargoToRemove.getWeight();
        }
    }

    public void removeCargo(String cargoId) {
        cargo.removeIf(c -> c.getId().equals(cargoId));
    }

    public void removeArrivedCargo(String stationId) {
        cargo.removeIf(c -> c.getDestStationId().equals(stationId));
    }

    public boolean hasCapacityForPassenger() {
        return passengers.size() < maxPassengers;
    }

    public boolean hasCapacityForCargo(Cargo newCargo) {
        return currentCargoWeight + newCargo.getWeight() <= maxCargoWeight;
    }
}
