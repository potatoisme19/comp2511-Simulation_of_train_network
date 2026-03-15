package unsw.trains.stations;

import java.util.ArrayList;
import java.util.List;
import unsw.trains.Cargo;

public class PassengerStation extends Station {

    public PassengerStation(String id, double x, double y) {
        super(id, x, y, 2);
    }

    public boolean canLoadPassengers() {
        return true;
    }

    public boolean canLoadCargo() {
        return false;
    }

    @Override
    public void addCargo(Cargo cargo) {
        return;
    }

    @Override
    public void removeCargo(String cargoId) {
        return;
    }

    @Override
    public List<Cargo> getCargo() {
        return new ArrayList<>();
    }
}
