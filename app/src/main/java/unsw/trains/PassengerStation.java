package unsw.trains;

import java.util.List;

public class PassengerStation extends Station {

    public PassengerStation(String id, double x, double y) {
        super(id, "PassengerStation", x, y, 2);
    }

    @Override
    public boolean canLoadPassengers() {
        return true;
    }

    @Override
    public boolean canLoadCargo() {
        return false;
    }

    @Override
    public List<Cargo> getCargo() {
        return new java.util.ArrayList<>();
    }
}
