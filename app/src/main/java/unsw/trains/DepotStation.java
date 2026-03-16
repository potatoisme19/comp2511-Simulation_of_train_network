package unsw.trains;

import java.util.List;

public class DepotStation extends Station {

    public DepotStation(String id, double x, double y) {
        super(id, "DepotStation", x, y, 8);
    }

    @Override
    public boolean canLoadPassengers() {
        return false;
    }

    @Override
    public boolean canLoadCargo() {
        return false;
    }

    @Override
    public List<Passenger> getPassengers() {
        return new java.util.ArrayList<>();
    }

    @Override
    public List<Cargo> getCargo() {
        return new java.util.ArrayList<>();
    }
}
