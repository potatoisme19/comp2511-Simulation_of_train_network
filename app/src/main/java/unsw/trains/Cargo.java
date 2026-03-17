package unsw.trains;

public class Cargo extends Load {
    private int weight;
    private int minsTillPerish;
    private boolean isPerishable;

    public Cargo(String id, String startStationId, String destStationId, int weight) {
        super(id, startStationId, destStationId);
        this.weight = weight;
        this.isPerishable = false;
        this.minsTillPerish = Integer.MAX_VALUE;
    }

    public Cargo(String id, String startStationId, String destStationId, int weight, int minsTillPerish) {
        super(id, startStationId, destStationId);
        this.weight = weight;
        this.isPerishable = true;
        this.minsTillPerish = minsTillPerish;
    }

    @Override
    public String getType() {
        if (isPerishable) {
            return "PerishableCargo";
        } else {
            return "Cargo";
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public boolean isPerishable() {
        return isPerishable;
    }

    @Override
    public void tick() {
        if (isPerishable) {
            minsTillPerish--;
        }
    }

    @Override
    public boolean hasExpired() {
        return isPerishable && minsTillPerish <= 0;
    }

    public int getMinsTillPerish() {
        return minsTillPerish;
    }
}
