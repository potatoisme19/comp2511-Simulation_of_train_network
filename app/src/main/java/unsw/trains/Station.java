package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Station {
    private String id;
    private String type;
    private double x;
    private double y;
    private int maxTrains;
    private List<String> stoppedTrains;
    private List<Load> loads;

    public Station(String id, String type, double x, double y, int maxTrains) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.maxTrains = maxTrains;
        this.stoppedTrains = new ArrayList<>();
        this.loads = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getMaxTrains() {
        return maxTrains;
    }

    public void addTrain(String trainId) {
        stoppedTrains.add(trainId);
    }

    public void removeTrain(String trainId) {
        stoppedTrains.remove(trainId);
    }

    public List<String> getStoppedTrains() {
        return new ArrayList<>(stoppedTrains);
    }

    public boolean canStopTrain() {
        return stoppedTrains.size() < maxTrains;
    }

    public abstract boolean canLoadPassengers();

    public abstract boolean canLoadCargo();

    public void addLoad(Load load) {
        boolean canAccept = (load.getType().equals("Passenger") && canLoadPassengers())
                || (load.getType().equals("Cargo") && canLoadCargo());

        if (canAccept) {
            loads.add(load);
        }
    }

    public void removeLoad(String loadId) {
        loads.removeIf(l -> l.getId().equals(loadId));
    }

    public List<Load> getLoads() {
        return new ArrayList<>(loads);
    }

    public List<Load> getPassengers() {
        return loads.stream()
                .filter(l -> l.getType().equals("Passenger"))
                .collect(Collectors.toList());
    }

    public List<Load> getCargo() {
        return loads.stream()
                .filter(l -> l.getType().equals("Cargo") || l.getType().equals("PerishableCargo"))
                .collect(Collectors.toList());
    }

    public void tick() {
        List<Load> expired = new ArrayList<>();
        for (Load load : loads) {
            load.tick();
            if (load.hasExpired()) {
                expired.add(load);
            }
        }
        loads.removeAll(expired);
    }
}
