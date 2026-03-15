package unsw.trains;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private String id;
    private String type;
    private String currentStation;
    private List<String> route;
    private int routeIndex;
    private int direction;
    private double speed;
    private List<Passenger> passengers;
    private List<Cargo> cargo;
    private double x;
    private double y;
    private boolean isCyclical;
    private int maxPassengers;
    private int maxCargoWeight;
    private int currentLoad;

    public Train(String id, String type, String stationId, List<String> route,
            double speed, boolean isCyclical, double x, double y) {
        this.id = id;
        this.type = type;
        this.currentStation = stationId;
        this.route = new ArrayList<>(route);
        this.routeIndex = route.indexOf(stationId);
        this.direction = 1;
        this.speed = speed;
        this.passengers = new ArrayList<>();
        this.cargo = new ArrayList<>();
        this.isCyclical = isCyclical;
        this.x = x;
        this.y = y;
    }

    public String getNextStation() {
        if (type.equals("PassengerTrain") || type.equals("CargoTrain")) {
            int nextIndex = routeIndex + direction;
            if (nextIndex < 0 || nextIndex >= route.size()) {
                direction *= -1;
                nextIndex = routeIndex + direction;
            }
            return route.get(nextIndex);
        } else {
            if (isCyclical) {
                int nextIndex = (routeIndex + 1) % route.size();
                return route.get(nextIndex);
            } else {
                int nextIndex = routeIndex + direction;
                if (nextIndex < 0 || nextIndex >= route.size()) {
                    direction *= -1;
                    nextIndex = routeIndex + direction;
                }
                return route.get(nextIndex);
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public double getSpeed() {
        return speed;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public List<Cargo> getCargo() {
        return cargo;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getMaxPassengers() {
        return maxPassengers;
    }

    public int getMaxCargoWeight() {
        return maxCargoWeight;
    }

    public int getCurrentLoad() {
        return currentLoad;
    }
}
