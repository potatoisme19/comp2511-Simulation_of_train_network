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
    private boolean isCyclical; // Store this once at creation

    public Train(String id, String type, String stationId, List<String> route, double speed, boolean isCyclical) {
        this.id = id;
        this.type = type;
        this.currentStation = stationId;
        this.route = new ArrayList<>(route);
        this.routeIndex = route.indexOf(stationId);
        this.direction = 1;
        this.speed = speed;
        this.passengers = new ArrayList<>();
        this.cargo = new ArrayList<>();
        this.isCyclical = isCyclical; // Store it
    }

    public String getNextStation() {
        if (type.equals("PassengerTrain") || type.equals("CargoTrain")) {
            // Linear route - bounce back and forth
            int nextIndex = routeIndex + direction;
            if (nextIndex < 0 || nextIndex >= route.size()) {
                direction *= -1;
                nextIndex = routeIndex + direction;
            }
            return route.get(nextIndex);
        } else { // BulletTrain
            if (isCyclical) {
                // Cyclical - wrap around
                int nextIndex = (routeIndex + 1) % route.size();
                return route.get(nextIndex);
            } else {
                // Linear - same as above
                int nextIndex = routeIndex + direction;
                if (nextIndex < 0 || nextIndex >= route.size()) {
                    direction *= -1;
                    nextIndex = routeIndex + direction;
                }
                return route.get(nextIndex);
            }
        }
    }
}
