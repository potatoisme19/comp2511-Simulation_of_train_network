package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public abstract class Train {
    private String id;
    private String currentStation;
    private List<String> route;
    private int routeIndex;
    private int direction;
    private double speed;
    private double x;
    private double y;
    private boolean isCyclical;

    public Train(String id, String stationId, List<String> route,
            double speed, boolean isCyclical, double x, double y) {
        this.id = id;
        this.currentStation = stationId;
        this.route = new ArrayList<>(route);
        this.routeIndex = route.indexOf(stationId);
        this.direction = 1;
        this.speed = speed;
        this.isCyclical = isCyclical;
        this.x = x;
        this.y = y;
    }

    public String getNextStation() {
        int nextIndex = calculateNextIndex();
        return getRoute().get(nextIndex);
    }

    public abstract String getType();

    public abstract int getCurrentLoad();

    public abstract List<Passenger> getPassengers();

    public abstract List<Cargo> getCargo();

    public abstract boolean canBoardPassenger();

    public abstract boolean canBoardCargo();

    public abstract boolean isRepairTrain();

    public abstract int getMaxPassengerCapacity();

    public abstract int getMaxCargoCapacity();

    public abstract void addPassenger(Passenger p);

    public abstract void addCargo(Cargo c);

    public abstract void removePassenger(Passenger p);

    public abstract void removeCargo(Cargo c);

    public abstract void removePassenger(String passengerId);

    public abstract void removeCargo(String cargoId);

    public abstract void removeArrivedPassengers(String stationId);

    public abstract void removeArrivedCargo(String stationId);

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(String stationId) {
        this.currentStation = stationId;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public List<String> getRoute() {
        return new ArrayList<>(route);
    }

    public boolean isCyclical() {
        return isCyclical;
    }

    public int getRouteIndex() {
        return routeIndex;
    }

    public void setRouteIndex(int index) {
        this.routeIndex = index;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    protected int calculateNextIndex() {
        int nextIndex = routeIndex + direction;
        if (nextIndex < 0 || nextIndex >= route.size()) {
            direction *= -1;
            nextIndex = routeIndex + direction;
        }
        return nextIndex;
    }
}
