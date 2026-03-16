package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public class CargoTrain extends Train {
    private List<Cargo> cargo;
    private int maxCargoWeight;
    private int currentCargoWeight;

    public CargoTrain(String id, String stationId, List<String> route,
            double speed, boolean isCyclical, double x, double y, int maxCargoWeight) {
        super(id, stationId, route, speed, isCyclical, x, y);
        this.cargo = new ArrayList<>();
        this.maxCargoWeight = maxCargoWeight;
        this.currentCargoWeight = 0;
    }

    @Override
    public String getType() {
        return "CargoTrain";
    }

    @Override
    public int getCurrentLoad() {
        return currentCargoWeight;
    }

    @Override
    public List<Passenger> getPassengers() {
        return new ArrayList<>();
    }

    @Override
    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    @Override
    public boolean canBoardPassenger() {
        return false;
    }

    @Override
    public boolean canBoardCargo() {
        return true;
    }

    @Override
    public boolean isRepairTrain() {
        return false;
    }

    @Override
    public int getMaxPassengerCapacity() {
        return 0;
    }

    @Override
    public int getMaxCargoCapacity() {
        return maxCargoWeight;
    }

    @Override
    public void addPassenger(Passenger p) {
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
    }

    @Override
    public void removeCargo(Cargo c) {
        if (cargo.remove(c)) {
            currentCargoWeight -= c.getWeight();
        }
    }

    @Override
    public void removePassenger(String passengerId) {
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

    public int getMaxCargoWeight() {
        return maxCargoWeight;
    }

    public boolean hasCapacityFor(Cargo newCargo) {
        return currentCargoWeight + newCargo.getWeight() <= maxCargoWeight;
    }
}
