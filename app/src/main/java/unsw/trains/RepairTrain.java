package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public class RepairTrain extends Train {
    private List<Passenger> passengers;
    private int maxPassengers;
    private static final int PASSENGER_WEIGHT = 70;

    public RepairTrain(String id, String stationId, List<String> route,
            double speed, boolean isCyclical, double x, double y, int maxPassengers) {
        super(id, stationId, route, speed, isCyclical, x, y);
        this.passengers = new ArrayList<>();
        this.maxPassengers = maxPassengers;
    }

    @Override
    public String getType() {
        return "RepairTrain";
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
        return true;
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
        }
    }

    @Override
    public void addCargo(Cargo c) {
    }

    @Override
    public void removePassenger(Passenger p) {
        passengers.remove(p);
    }

    @Override
    public void removeCargo(Cargo c) {
    }

    @Override
    public void removePassenger(String passengerId) {
        passengers.removeIf(p -> p.getId().equals(passengerId));
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

    public int countMechanics() {
        int count = 0;
        for (Passenger p : passengers) {
            if (p.isMechanic()) {
                count++;
            }
        }
        return count;
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }
}
