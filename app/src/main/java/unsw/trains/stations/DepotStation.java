package unsw.trains.stations;

import unsw.trains.Passenger;
import unsw.trains.Cargo;

public class DepotStation extends Station {

    public DepotStation(String id, double x, double y) {
        super(id, x, y, 8);
    }

    public boolean canLoadPassengers() {
        return false;
    }

    public boolean canLoadCargo() {
        return false;
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
    public void addCargo(Cargo cargo) {
        return;
    }

    @Override
    public void removeCargo(String cargoId) {
        return;
    }

}
