package unsw.trains;

import unsw.utils.TrackType;

public class Track {
    private String id;
    private String fromStationId;
    private String toStationId;
    private TrackType type;
    private int durability;
    private double distance;

    public Track(String id, String fromStationId, String toStationId) {
        this(id, fromStationId, toStationId, TrackType.NORMAL, 10);
    }

    public Track(String id, String fromStationId, String toStationId, TrackType type, int durability) {
        this.id = id;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.type = type;
        this.durability = durability;
    }

    public String getId() {
        return id;
    }

    public String getFromStationId() {
        return fromStationId;
    }

    public String getToStationId() {
        return toStationId;
    }

    public TrackType getType() {
        return type;
    }

    public int getDurability() {
        return durability;
    }

    public void setType(TrackType type) {
        this.type = type;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public boolean connects(String stationId1, String stationId2) {
        return (fromStationId.equals(stationId1) && toStationId.equals(stationId2))
                || (fromStationId.equals(stationId2) && toStationId.equals(stationId1));
    }

    public String getOtherStation(String stationId) {
        if (stationId.equals(fromStationId))
            return toStationId;
        if (stationId.equals(toStationId))
            return fromStationId;
        return null;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }
}
