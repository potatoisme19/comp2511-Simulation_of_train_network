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

    public int getMaxCargoWeight() {
        return maxCargoWeight;
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

    public boolean hasCapacityFor(Cargo newCargo) {
        return currentCargoWeight + newCargo.getWeight() <= maxCargoWeight;
    }
}
