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
    public void addLoad(Load load) {
        String type = load.getType();
        if (type.equals("Cargo") || type.equals("PerishableCargo")) {
            addCargo((Cargo) load);
        }
    }

    @Override
    public void addCargo(Cargo c) {
        if (currentCargoWeight + c.getWeight() <= maxCargoWeight) {
            cargo.add(c);
            currentCargoWeight += c.getWeight();
            super.addLoad(c);
        }
    }

    @Override
    public void removeLoad(String loadId) {
        for (Load load : getLoads()) {
            if (load.getId().equals(loadId) && load.getType().equals("Cargo")) {
                Cargo c = (Cargo) load;
                currentCargoWeight -= c.getWeight();
                break;
            }
        }
        super.removeLoad(loadId);
    }

    @Override
    public void unloadAtStation(String stationId) {
        for (Load load : getLoads()) {
            if (load.getDestStationId().equals(stationId) && load.getType().equals("Cargo")) {
                Cargo c = (Cargo) load;
                currentCargoWeight -= c.getWeight();
            }
        }
        super.unloadAtStation(stationId);
    }

    @Override
    public void updateSpeed() {
        double baseSpeed = CARGO_TRAIN_SPEED;
        int totalWeight = getCurrentLoad();
        double slowdown = totalWeight * SLOWDOWN_RATE;
        double newSpeed = baseSpeed * (1.0 - slowdown);
        setSpeed(Math.max(0.1, newSpeed));
    }

    public int getMaxCargoWeight() {
        return maxCargoWeight;
    }

    public boolean hasCapacityFor(Cargo newCargo) {
        return currentCargoWeight + newCargo.getWeight() <= maxCargoWeight;
    }
}
