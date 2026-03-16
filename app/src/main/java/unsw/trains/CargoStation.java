package unsw.trains;

import java.util.List;

public class CargoStation extends Station {

    public CargoStation(String id, double x, double y) {
        super(id, "CargoStation", x, y, 4);
    }

    @Override
    public boolean canLoadPassengers() {
        return false;
    }

    @Override
    public boolean canLoadCargo() {
        return true;
    }

    @Override
    public List<Passenger> getPassengers() {
        return new java.util.ArrayList<>();
    }
}
