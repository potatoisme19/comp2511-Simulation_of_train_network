package unsw.trains;

import unsw.utils.TrackType;

public class Track {
    private String id;
    private String fromStationId;
    private String toStationId;
    private TrackType type;
    private int durability;
    private boolean isBreakable;

    // Constructor for Task A (normal tracks)
    public Track(String id, String fromStationId, String toStationId) {
        this(id, fromStationId, toStationId, TrackType.NORMAL, 10, false);
    }

    // Constructor for Task C (breakable tracks)
    public Track(String id, String fromStationId, String toStationId, boolean isBreakable) {
        this(id, fromStationId, toStationId,
                isBreakable ? TrackType.UNBROKEN : TrackType.NORMAL,
                10,
                isBreakable);
    }

    // Full constructor
    public Track(String id, String fromStationId, String toStationId, TrackType type, int durability,
            boolean isBreakable) {
        this.id = id;
        this.fromStationId = fromStationId;
        this.toStationId = toStationId;
        this.type = type;
        this.durability = durability;
        this.isBreakable = isBreakable;
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

    public boolean isBreakable() {
        return isBreakable;
    }

    public void setType(TrackType type) {
        this.type = type;
    }

    public void setDurability(int durability) {
        this.durability = Math.max(0, Math.min(10, durability));
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

    public void repair(int amount) {
        if (isBreakable) {
            durability = Math.min(10, durability + amount);
        }
    }

    public void damage(int amount) {
        if (isBreakable) {
            durability = Math.max(0, durability - amount);
        }
    }
}
