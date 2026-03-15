package unsw.trains.stations;

public class CentralStation extends Station {

    public CentralStation(String id, double x, double y) {
        super(id, x, y, 8);
    }

    public boolean canLoadPassengers() {
        return true;
    }

    public boolean canLoadCargo() {
        return true;
    }

}
