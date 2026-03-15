package unsw.trains.stations;

import java.util.ArrayList;
import java.util.List;
import unsw.trains.Passenger;

public class CargoStation extends Station {

    public CargoStation(String id, double x, double y) {
        super(id, x, y, 4);
    }

    public boolean canLoadPassengers() {
        return false;
    }

    public boolean canLoadCargo() {
        return true;
    }

    @Override
    public void addPassenger(Passenger passenger) {
        return;
    }

    @Override
    public void removePassenger(String passengerId) {
        return;
    }

    @Override
    public List<Passenger> getPassengers() {
        return new ArrayList<>();
    }
}
