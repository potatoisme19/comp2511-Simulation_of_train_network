package unsw.trains;

public class Passenger {
    private String id;
    private String startStationId;
    private String destStationId;
    private boolean isMechanic;

    public Passenger(String id, String startStationId, String destStationId) {
        this(id, startStationId, destStationId, false);
    }

    public Passenger(String id, String startStationId, String destStationId, boolean isMechanic) {
        this.id = id;
        this.startStationId = startStationId;
        this.destStationId = destStationId;
        this.isMechanic = isMechanic;
    }

    public String getId() {
        return id;
    }

    public String getStartStationId() {
        return startStationId;
    }

    public String getDestStationId() {
        return destStationId;
    }

    public boolean isMechanic() {
        return isMechanic;
    }
}
