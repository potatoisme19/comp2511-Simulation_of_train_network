package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public class PassengerTrain extends Train {
    private List<Passenger> passengers;
    private int maxPassengers;

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
        return passengers.size();
    }

    @Override
    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    @Override
    public List<Cargo> getCargo() {
        return new ArrayList<>();
    }

    public int getMaxPassengers() {
        return maxPassengers;
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
}
