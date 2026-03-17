package unsw.trains;

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
}
