package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public class PassengerTrain extends Train {
    private List<Passenger> passengers;
    private int maxPassengers;
    private static final int PASSENGER_WEIGHT = 70;

    public PassengerTrain(String id, String stationId, List<String> route,
            double speed, boolean isCyclical, double x, double y, int maxPassengers) {
        super(id, stationId, route, speed, isCyclical, x, y);
        this.passengers = new ArrayList<>();
        this.maxPassengers = maxPassengers;
    }

    @Override
    public String getType() {
        return "PassengerTrain";
    }

    @Override
    public int getCurrentLoad() {
        return passengers.size() * PASSENGER_WEIGHT;
    }

    @Override
    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    @Override
    public List<Cargo> getCargo() {
        return new ArrayList<>();
    }

    @Override
    public boolean canBoardPassenger() {
        return true;
    }

    @Override
    public boolean canBoardCargo() {
        return false;
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
        return 0;
    }

    @Override
    public void addPassenger(Passenger p) {
        if (passengers.size() < maxPassengers) {
            passengers.add(p);
            super.addLoad(p);
        }
    }

    @Override
    public void addLoad(Load load) {
        if (load.getType().equals("Passenger") || load.getType().equals("Mechanic")) {
            addPassenger((Passenger) load);
        }
    }

    @Override
    public void addCargo(Cargo c) {
    }

    @Override
    public void removePassenger(Passenger p) {
        if (passengers.remove(p)) {
            super.removeLoad(p.getId());
        }
    }

    @Override
    public void removeCargo(Cargo c) {
    }

    @Override
    public void removePassenger(String passengerId) {
        for (Passenger p : new ArrayList<>(passengers)) {
            if (p.getId().equals(passengerId)) {
                removePassenger(p);
                break;
            }
        }
    }

    @Override
    public void removeCargo(String cargoId) {
    }

    @Override
    public void removeArrivedPassengers(String stationId) {
        passengers.removeIf(p -> p.getDestStationId().equals(stationId));
    }

    @Override
    public void removeArrivedCargo(String stationId) {
    }

    @Override
    public void updateSpeed() {
        double baseSpeed = PASSENGER_TRAIN_SPEED;
        int totalWeight = getCurrentLoad();
        double slowdown = totalWeight * SLOWDOWN_RATE;
        double newSpeed = baseSpeed * (1.0 - slowdown);
        setSpeed(Math.max(0.1, newSpeed));
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }
}
