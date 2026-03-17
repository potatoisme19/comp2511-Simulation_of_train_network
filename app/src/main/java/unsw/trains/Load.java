package unsw.trains;

public abstract class Load {
    private String id;
    private String startStationId;
    private String destStationId;

    public Load(String id, String startStationId, String destStationId) {
        this.id = id;
        this.startStationId = startStationId;
        this.destStationId = destStationId;
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

    public abstract String getType();

    public abstract int getWeight();

    public abstract boolean isPerishable();

    public abstract void tick();

    public abstract boolean hasExpired();
}
