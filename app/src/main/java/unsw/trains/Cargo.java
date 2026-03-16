package unsw.trains;

public class Cargo {
    private String id;
    private String startStationId;
    private String destStationId;
    private int weight;
    private boolean isPerishable;
    private Integer minsTillPerish;

    public Cargo(String id, String startStationId, String destStationId, int weight) {
        this(id, startStationId, destStationId, weight, 0);
    }

    public Cargo(String id, String startStationId, String destStationId, int weight, int minsTillPerish) {
        this.id = id;
        this.startStationId = startStationId;
        this.destStationId = destStationId;
        this.weight = weight;
        this.minsTillPerish = minsTillPerish;
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

    public int getWeight() {
        return weight;
    }

    public boolean isPerishable() {
        return minsTillPerish != null;
    }

    public Integer getMinsTillPerish() {
        return minsTillPerish;
    }

    public void decrementPerishTime() {
        if (isPerishable && minsTillPerish > 0) {
            minsTillPerish--;
        }
    }

    public boolean hasExpired() {
        return isPerishable && minsTillPerish <= 0;
    }
}
