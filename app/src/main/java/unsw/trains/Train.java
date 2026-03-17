package unsw.trains;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Train {
    protected static final double PASSENGER_TRAIN_SPEED = 2.0;
    protected static final double CARGO_TRAIN_SPEED = 3.0;
    protected static final double BULLET_TRAIN_SPEED = 5.0;
    protected static final double REPAIR_TRAIN_SPEED = 2.0;
    protected static final double SLOWDOWN_RATE = 0.0001;

    private String id;
    private String currentStation;
    private List<String> route;
    private int routeIndex;
    private int direction;
    private double speed;
    private double x;
    private double y;
    private boolean isCyclical;
    private String journeyDepartureStation = null;
    private List<Load> loads;

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
        this.loads = new ArrayList<>();
    }

    public String getNextStation() {
        int nextIndex = calculateNextIndex();
        return getRoute().get(nextIndex);
    }

    public abstract String getType();

    public abstract boolean canBoardPassenger();

    public abstract boolean canBoardCargo();

    public abstract boolean isRepairTrain();

    public abstract int getMaxPassengerCapacity();

    public abstract int getMaxCargoCapacity();

    public abstract void updateSpeed();

    public boolean willReachDestination(String destStationId) {
        List<String> route = getRoute();
        int currentIndex = getRouteIndex();
        int destIndex = route.indexOf(destStationId);

        if (destIndex == -1) {
            return false;
        }

        if (isCyclical()) {
            return true;
        } else {
            return getDirection() > 0 ? destIndex > currentIndex : destIndex < currentIndex;
        }
    }

    public int countMechanics() {
        return (int) getPassengers().stream()
                .filter(Passenger::isMechanic)
                .count();
    }

    public void addLoad(Load load) {
        loads.add(load);
    }

    public void removeLoad(String loadId) {
        loads.removeIf(l -> l.getId().equals(loadId));
    }

    public List<Load> getLoads() {
        return new ArrayList<>(loads);
    }

    public int getCurrentLoad() {
        return loads.stream().mapToInt(Load::getWeight).sum();
    }

    public List<Passenger> getPassengers() {
        return loads.stream()
                .filter(l -> l instanceof Passenger)
                .map(l -> (Passenger) l)
                .collect(Collectors.toList());
    }

    public List<Cargo> getCargo() {
        return loads.stream()
                .filter(l -> l instanceof Cargo)
                .map(l -> (Cargo) l)
                .collect(Collectors.toList());
    }

    public void addPassenger(Passenger p) {
        if (canBoardPassenger()) {
            loads.add(p);
        }
    }

    public void addCargo(Cargo c) {
        if (canBoardCargo()) {
            loads.add(c);
        }
    }

    public void removePassenger(Passenger p) {
        loads.remove(p);
    }

    public void removeCargo(Cargo c) {
        loads.remove(c);
    }

    public void removePassenger(String passengerId) {
        loads.removeIf(l -> l instanceof Passenger && l.getId().equals(passengerId));
    }

    public void removeCargo(String cargoId) {
        loads.removeIf(l -> l instanceof Cargo && l.getId().equals(cargoId));
    }

    public void removeArrivedPassengers(String stationId) {
        loads.removeIf(l -> l instanceof Passenger && l.getDestStationId().equals(stationId));
    }

    public void removeArrivedCargo(String stationId) {
        loads.removeIf(l -> l instanceof Cargo && l.getDestStationId().equals(stationId));
    }

    public void tick() {
        List<Load> expired = new ArrayList<>();
        for (Load load : loads) {
            load.tick();
            if (load.hasExpired()) {
                expired.add(load);
            }
        }
        if (!expired.isEmpty()) {
            loads.removeAll(expired);
            updateSpeed();
        }
    }

    public void unloadAtStation(String stationId) {
        removeArrivedPassengers(stationId);
        removeArrivedCargo(stationId);
    }

    public boolean canCarry(Load load) {
        if (load instanceof Passenger) {
            return canBoardPassenger() && getPassengers().size() < getMaxPassengerCapacity();
        } else if (load instanceof Cargo) {
            return canBoardCargo() && getCurrentLoad() + load.getWeight() <= getMaxCargoCapacity();
        }
        return false;
    }

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

    public String getJourneyDepartureStation() {
        return journeyDepartureStation;
    }

    public void setJourneyDepartureStation(String stationId) {
        this.journeyDepartureStation = stationId;
    }
}
