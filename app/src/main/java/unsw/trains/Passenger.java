package unsw.trains;

public class Passenger extends Load {
    private static final int PASSENGER_WEIGHT = 70;
    private boolean isMechanic;

    public Passenger(String id, String startStationId, String destStationId) {
        super(id, startStationId, destStationId);
        this.isMechanic = false;
    }

    public Passenger(String id, String startStationId, String destStationId, boolean isMechanic) {
        super(id, startStationId, destStationId);
        this.isMechanic = isMechanic;
    }

    @Override
    public String getType() {
        if (isMechanic) {
            return "Mechanic";
        } else {
            return "Passenger";
        }
    }

    @Override
    public int getWeight() {
        return PASSENGER_WEIGHT;
    }

    @Override
    public boolean isPerishable() {
        return false;
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean hasExpired() {
        return false;
    }

    public boolean isMechanic() {
        return isMechanic;
    }
}
