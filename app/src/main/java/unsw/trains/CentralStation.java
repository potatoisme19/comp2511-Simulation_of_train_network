package unsw.trains;

public class CentralStation extends Station {

    public CentralStation(String id, double x, double y) {
        super(id, "CentralStation", x, y, 8);
    }

    @Override
    public boolean canLoadPassengers() {
        return true;
    }

    @Override
    public boolean canLoadCargo() {
        return true;
    }

}
