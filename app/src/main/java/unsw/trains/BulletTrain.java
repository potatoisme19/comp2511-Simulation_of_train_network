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
        return super.getCurrentLoad();
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
    public void updateSpeed() {
        double baseSpeed = BULLET_TRAIN_SPEED;
        int totalWeight = getCurrentLoad();
        double slowdown = totalWeight * SLOWDOWN_RATE;
        double newSpeed = baseSpeed * (1.0 - slowdown);

        setSpeed(Math.max(0.1, newSpeed));
    }

    @Override
    public void addLoad(Load load) {
        String type = load.getType();
        if (type.equals("Passenger") || type.equals("Mechanic")) {
            Passenger p = (Passenger) load;
            if (passengers.size() < maxPassengers) {
                passengers.add(p);
                super.addLoad(load);
            }
        } else if (type.equals("Cargo") || type.equals("PerishableCargo")) {
            Cargo c = (Cargo) load;
            if (currentCargoWeight + c.getWeight() <= maxCargoWeight) {
                cargo.add(c);
                super.addLoad(load);
                currentCargoWeight += c.getWeight();
            }
        }
    }

    @Override
    public void removeLoad(String loadId) {
        for (Load load : getLoads()) {
            if (load.getId().equals(loadId)) {
                if (load instanceof Cargo) {
                    currentCargoWeight -= ((Cargo) load).getWeight();
                    cargo.remove(load);
                } else if (load instanceof Passenger) {
                    passengers.remove(load);
                }
                break;
            }
        }
        super.removeLoad(loadId);
    }

    @Override
    public void unloadAtStation(String stationId) {
        List<Load> toRemove = new ArrayList<>();
        for (Load load : getLoads()) {
            if (load.getDestStationId().equals(stationId)) {
                toRemove.add(load);
                if (load instanceof Cargo) {
                    currentCargoWeight -= ((Cargo) load).getWeight();
                    cargo.remove(load);
                } else if (load instanceof Passenger) {
                    passengers.remove(load);
                }
            }
        }
        super.unloadAtStation(stationId);
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }

    public int getMaxCargoWeight() {
        return maxCargoWeight;
    }
}
