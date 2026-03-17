package unsw.trains;

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
}
