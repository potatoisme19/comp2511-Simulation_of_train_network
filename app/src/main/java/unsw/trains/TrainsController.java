package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.*;
import unsw.utils.Position;
import unsw.utils.TrackType;

/**
 * The controller for the Trains system.
 *
 * The method signatures here are provided for you. Do NOT change the method signatures.
 */
public class TrainsController {
    private static final int PASSENGER_TRAIN_MAX_PASSENGERS = 50;
    private static final int CARGO_TRAIN_MAX_WEIGHT = 5000;
    private static final int BULLET_TRAIN_MAX_PASSENGERS = 71;

    private static final double PASSENGER_TRAIN_SPEED = 2.0;
    private static final double CARGO_TRAIN_SPEED = 3.0;
    private static final double BULLET_TRAIN_SPEED = 5.0;

    private static final int AUTO_REPAIR_AMOUNT = 1;

    private static final int WEIGHT_DIVISOR = 1000;

    private static final int REPAIR_TRAIN_MAX_PASSENGERS = 50;
    private static final double REPAIR_TRAIN_SPEED = 2.0;
    private static final int MECHANIC_REPAIR_BONUS = 2;

    private List<Station> stations = new ArrayList<>();
    private List<Track> tracks = new ArrayList<>();
    private List<Train> trains = new ArrayList<>();

    public void createStation(String stationId, String type, double x, double y) {
        Station station;
        switch (type) {
            case "PassengerStation":
                station = new PassengerStation(stationId, x, y);
                break;
            case "CargoStation":
                station = new CargoStation(stationId, x, y);
                break;
            case "CentralStation":
                station = new CentralStation(stationId, x, y);
                break;
            case "DepotStation":
                station = new DepotStation(stationId, x, y);
                break;
            default:
                station = null;
        }

        stations.add(station);
    }

    public void createTrack(String trackId, String fromStationId, String toStationId) {
        Track track = new Track(trackId, fromStationId, toStationId);
        tracks.add(track);
    }

    public void createTrack(String trackId, String fromStationId, String toStationId, boolean isBreakable) {
        Track track = new Track(trackId, fromStationId, toStationId, isBreakable);
        tracks.add(track);
    }

    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {
        boolean isCyclical = route.size() >= 3 && hasTrackBetween(route.get(0), route.get(route.size() - 1));

        if ((type.equals("PassengerTrain") || type.equals("CargoTrain")) && isCyclical) {
            throw new InvalidRouteException("PassengerTrain and CargoTrain can only be on linear route");
        }

        Station station = findStationById(stationId);
        double x = station.getX();
        double y = station.getY();

        Train train;
        double speed;

        if (type.equals("PassengerTrain")) {
            speed = PASSENGER_TRAIN_SPEED;
            train = new PassengerTrain(trainId, stationId, route, speed, isCyclical, x, y,
                    PASSENGER_TRAIN_MAX_PASSENGERS);
        } else if (type.equals("CargoTrain")) {
            speed = CARGO_TRAIN_SPEED;
            train = new CargoTrain(trainId, stationId, route, speed, isCyclical, x, y, CARGO_TRAIN_MAX_WEIGHT);
        } else if (type.equals("RepairTrain")) {
            speed = REPAIR_TRAIN_SPEED;
            train = new RepairTrain(trainId, stationId, route, speed, isCyclical, x, y, REPAIR_TRAIN_MAX_PASSENGERS);
        } else {
            speed = BULLET_TRAIN_SPEED;
            train = new BulletTrain(trainId, stationId, route, speed, isCyclical, x, y, BULLET_TRAIN_MAX_PASSENGERS);
        }

        trains.add(train);
        station.addTrain(trainId);
    }

    private boolean hasTrackBetween(String s1, String s2) {

        for (Track t : tracks) {
            if (t.connects(s1, s2))
                return true;
        }
        return false;
    }

    private Station findStationById(String stationId) {
        for (Station s : stations) {
            if (s.getId().equals(stationId)) {
                return s;
            }
        }
        return null;
    }

    public List<String> listStationIds() {
        List<String> ids = new ArrayList<>();
        for (Station s : stations) {
            ids.add(s.getId());
        }
        return ids;
    }

    public List<String> listTrackIds() {
        List<String> ids = new ArrayList<>();
        for (Track t : tracks) {
            ids.add(t.getId());
        }
        return ids;
    }

    public List<String> listTrainIds() {
        List<String> ids = new ArrayList<>();
        for (Train t : trains) {
            ids.add(t.getId());
        }
        return ids;
    }

    public TrainInfoResponse getTrainInfo(String trainId) {
        for (Train t : trains) {
            if (t.getId().equals(trainId)) {
                // Use getLoads() instead of separate passenger/cargo methods
                List<LoadInfoResponse> loads = loadsToLoadInfo(t.getLoads());

                return new TrainInfoResponse(
                        t.getId(),
                        t.getCurrentStation(),
                        t.getType(),
                        new Position(t.getX(), t.getY()),
                        loads);
            }
        }
        return null;
    }

    public StationInfoResponse getStationInfo(String stationId) {
        for (Station s : stations) {
            if (s.getId().equals(stationId)) {
                List<LoadInfoResponse> loads = loadsToLoadInfo(s.getLoads());
                List<TrainInfoResponse> trainsAtStation = getTrainInfos(s.getStoppedTrains());

                return new StationInfoResponse(
                        s.getId(),
                        s.getType(),
                        new Position(s.getX(), s.getY()),
                        loads,
                        trainsAtStation);
            }
        }
        return null;
    }

    private List<LoadInfoResponse> loadsToLoadInfo(List<Load> loads) {
        List<LoadInfoResponse> result = new ArrayList<>();
        for (Load load : loads) {
            result.add(new LoadInfoResponse(load.getId(), load.getType()));
        }
        return result;
    }

    public TrackInfoResponse getTrackInfo(String trackId) {
        for (Track t : tracks) {
            if (t.getId().equals(trackId)) {

                return new TrackInfoResponse(
                        t.getId(),
                        t.getFromStationId(),
                        t.getToStationId(),
                        t.getType(),
                        t.getDurability());
            }
        }
        return null;
    }

    private List<TrainInfoResponse> getTrainInfos(List<String> trainIds) {
        List<TrainInfoResponse> trainInfos = new ArrayList<>();
        for (String trainId : trainIds) {
            TrainInfoResponse trainInfo = getTrainInfo(trainId);
            if (trainInfo != null) {
                trainInfos.add(trainInfo);
            }
        }
        return trainInfos;
    }

    public void simulate() {
        trains.sort((t1, t2) -> t1.getId().compareTo(t2.getId()));

        for (Train train : trains) {
            processTrainMovement(train);
        }

        updateAllTrackStatuses();

        deterioratePerishables();
    }

    private void processTrainMovement(Train train) {
        Position currentPos = new Position(train.getX(), train.getY());
        String nextStationId = train.getNextStation();
        Station nextStation = findStationById(nextStationId);
        Position destPos = new Position(nextStation.getX(), nextStation.getY());

        String departureStationId = train.getCurrentStation();

        if (departureStationId != null && train.getJourneyDepartureStation() == null) {
            train.setJourneyDepartureStation(departureStationId);
        }

        handleDeparture(train);

        if (isTrackBroken(train, nextStationId)) {
            train.setJourneyDepartureStation(null);
            return;
        }

        if (departureStationId != null) {
            Track currentTrack = findTrackBetween(departureStationId, nextStationId);
            if (currentTrack != null && currentTrack.isBreakable()
                    && currentTrack.getType() == TrackType.BROKEN && train.isRepairTrain()) {
                applyRepairDuringMovement(train, currentTrack);
            }
        }

        if (currentPos.isInBound(destPos, train.getSpeed())) {
            String journeyDeparture = train.getJourneyDepartureStation();
            handleArrival(train, nextStation, currentPos, destPos, journeyDeparture);
            train.setJourneyDepartureStation(null);
        } else {
            moveTrainPartway(train, currentPos, destPos);
        }
    }

    private void applyRepairDuringMovement(Train train, Track track) {
        if (!train.isRepairTrain() || !track.isBreakable() || track.getType() != TrackType.BROKEN) {
            return;
        }

        RepairTrain repairTrain = (RepairTrain) train;
        int mechanicCount = repairTrain.countMechanics();

        // Repair rate = auto repair (1) + (mechanics * 2)
        int repairRate = AUTO_REPAIR_AMOUNT + (mechanicCount * MECHANIC_REPAIR_BONUS);

        // Apply repair
        int newDurability = Math.min(10, track.getDurability() + repairRate);
        track.setDurability(newDurability);

        // If repaired above 0, update type
        if (newDurability > 0 && track.getType() == TrackType.BROKEN) {
            track.setType(TrackType.UNBROKEN);
        }
    }

    private void handleArrival(Train train, Station nextStation, Position currentPos, Position destPos,
            String departureStationId) {
        train.setPosition(destPos.getX(), destPos.getY());

        if (nextStation.canStopTrain()) {
            completeArrival(train, nextStation, departureStationId);
        } else {
            train.setPosition(currentPos.getX(), currentPos.getY());
        }
    }

    private void completeArrival(Train train, Station station, String departureStationId) {
        String newStationId = station.getId();

        train.setCurrentStation(newStationId);
        station.addTrain(train.getId());

        if (departureStationId != null) {
            Station oldStation = findStationById(departureStationId);
            if (oldStation != null) {
                oldStation.removeTrain(train.getId());
            }
        }

        updateTrainRouteIndex(train);

        Track track = null;
        if (departureStationId != null) {
            track = findTrackBetween(departureStationId, newStationId);
        }

        updateTrackDurability(track, train);
        unloadLoadsFromTrain(train, station);
        updateTrainSpeed(train);
    }

    private boolean isTrackBroken(Train train, String nextStationId) {
        String currentStationId = train.getCurrentStation();
        if (currentStationId == null)
            return false;

        Track track = findTrackBetween(currentStationId, nextStationId);
        if (track == null)
            return false;

        if (track.getType() == TrackType.BROKEN) {
            return !train.isRepairTrain();
        }
        return false;
    }

    private void updateTrackDurability(Track track, Train train) {

        if (track == null || !track.isBreakable()) {
            return;
        }

        if (train.isRepairTrain()) {
            return;
        }

        int totalWeight = calculateTrainLoadWeight(train);
        int damage = 1 + (int) Math.ceil(totalWeight / (double) WEIGHT_DIVISOR);
        track.damage(damage);
    }

    private int calculateTrainLoadWeight(Train train) {
        return train
                .getLoads()
                .stream()
                .mapToInt(Load::getWeight)
                .sum();
    }

    private void updateTrackStatus(Track track) {
        if (!track.isBreakable()) {
            return;
        }

        if (track.getDurability() <= 0) {
            track.setType(TrackType.BROKEN);
        } else if (track.getDurability() >= 10) {
            track.setType(TrackType.UNBROKEN);
        }
    }

    private void updateAllTrackStatuses() {

        // Step 1: Store tracks that are BROKEN *before* updating status
        List<Track> previouslyBrokenTracks = new ArrayList<>();
        for (Track track : tracks) {
            if (track.isBreakable() && track.getType() == TrackType.BROKEN) {
                previouslyBrokenTracks.add(track);
            }
        }

        // Step 2: Update track status based on current durability
        for (Track track : tracks) {
            updateTrackStatus(track);
        }

        // Step 3: Repair tracks that were broken BEFORE this tick
        for (Track track : previouslyBrokenTracks) {
            if (track.isBreakable()) {
                int repairRate = calculateRepairRateForTrack(track);

                track.repair(repairRate);

                if (track.getDurability() > 10) {
                    track.setDurability(10);
                }

                if (track.getDurability() > 0) {
                    track.setType(TrackType.UNBROKEN);
                }
            }
        }
    }

    private int calculateRepairRateForTrack(Track track) {
        int repairRate = AUTO_REPAIR_AMOUNT;

        for (Train train : trains) {
            if (train.isRepairTrain() && isTrainOnTrack(train, track)) {
                RepairTrain repairTrain = (RepairTrain) train;
                int mechanicCount = repairTrain.countMechanics();
                repairRate += mechanicCount * MECHANIC_REPAIR_BONUS;
            }
        }

        return repairRate;
    }

    private boolean isTrainOnTrack(Train train, Track track) {
        String currentStation = train.getCurrentStation();
        String nextStation = train.getNextStation();
        String journeyDeparture = train.getJourneyDepartureStation();

        if (journeyDeparture != null && nextStation != null) {
            return track.connects(journeyDeparture, nextStation);
        }
        if (currentStation != null && nextStation != null) {
            return track.connects(currentStation, nextStation);
        }
        return false;
    }

    private void handleDeparture(Train train) {
        String currentStationId = train.getCurrentStation();

        if (currentStationId != null) {
            Station currentStation = findStationById(currentStationId);
            boardLoadsToTrain(train, currentStation);
            updateTrainSpeed(train);
        } else {
            return;
        }
    }

    private Track findTrackBetween(String stationId1, String stationId2) {
        if (stationId1 == null || stationId2 == null)
            return null;
        for (Track t : tracks) {
            if (t.connects(stationId1, stationId2)) {
                return t;
            }
        }
        return null;
    }

    private void moveTrainPartway(Train train, Position currentPos, Position destPos) {
        Position newPos = currentPos.calculateNewPosition(destPos, train.getSpeed());
        train.setPosition(newPos.getX(), newPos.getY());
        train.setCurrentStation(null);
    }

    private void boardLoadsToTrain(Train train, Station station) {
        if (station == null)
            return;

        for (Load load : station.getLoads()) {
            if (!train.canCarry(load))
                continue;

            if (!train.willReachDestination(load.getDestStationId()))
                continue;

            if (load instanceof Cargo) {
                Cargo cargo = (Cargo) load;
                if (cargo.isPerishable() && !canReachBeforeExpiry(train, cargo)) {
                    continue;
                }
            }

            train.addLoad(load);
            station.removeLoad(load.getId());
        }
    }

    private void updateTrainSpeed(Train train) {
        train.updateSpeed();
    }

    private void updateTrainRouteIndex(Train train) {
        String currentStation = train.getCurrentStation();
        List<String> route = train.getRoute();

        for (int i = 0; i < route.size(); i++) {
            if (route.get(i).equals(currentStation)) {
                train.setRouteIndex(i);
                break;
            }
        }
    }

    private void unloadLoadsFromTrain(Train train, Station station) {
        if (station == null)
            return;

        train.unloadAtStation(station.getId());
    }

    private boolean canReachBeforeExpiry(Train train, Cargo cargo) {
        if (!cargo.isPerishable())
            return true;

        double timeToDestination = calculateTimeToDestination(train, cargo.getDestStationId());
        return timeToDestination <= cargo.getMinsTillPerish();
    }

    private double calculateTimeToDestination(Train train, String destStationId) {
        List<String> route = train.getRoute();
        int currentIndex = train.getRouteIndex();
        int destIndex = route.indexOf(destStationId);

        if (destIndex == -1)
            return Double.MAX_VALUE;

        if (train.isCyclical()) {
            return calculateTimeForCyclicalRoute(train, currentIndex, destIndex);
        } else {
            return calculateTimeForLinearRoute(train, currentIndex, destIndex);
        }
    }

    private double calculateTimeForCyclicalRoute(Train train, int currentIndex, int destIndex) {
        List<String> route = train.getRoute();
        double totalTime = 0.0;

        int forwardDistance = (destIndex - currentIndex + route.size()) % route.size();
        int backwardDistance = (currentIndex - destIndex + route.size()) % route.size();
        int stops = Math.min(forwardDistance, backwardDistance);

        for (int i = 0; i < stops; i++) {
            int fromIdx = (currentIndex + i) % route.size();
            int toIdx = (currentIndex + i + 1) % route.size();
            totalTime += getSegmentTravelTime(route.get(fromIdx), route.get(toIdx), train.getSpeed());
            totalTime += 1.0;
        }

        return totalTime;
    }

    private double calculateTimeForLinearRoute(Train train, int currentIndex, int destIndex) {
        List<String> route = train.getRoute();
        double totalTime = 0.0;
        int direction = train.getDirection();
        int stops = calculateNumberOfStops(route.size(), currentIndex, destIndex, direction);

        String currentStation = train.getCurrentStation();
        for (int i = 0; i < stops; i++) {
            String nextStation = getNextStationInRoute(train, currentStation);
            totalTime += getSegmentTravelTime(currentStation, nextStation, train.getSpeed());
            totalTime += 1.0;
            currentStation = nextStation;
        }

        return totalTime;
    }

    private String getNextStationInRoute(Train train, String currentStationId) {
        List<String> route = train.getRoute();
        int currentIndex = route.indexOf(currentStationId);
        int nextIndex = currentIndex + train.getDirection();

        if (nextIndex < 0 || nextIndex >= route.size()) {
            if (train.isCyclical()) {
                nextIndex = (nextIndex + route.size()) % route.size();
            } else {
                nextIndex = currentIndex - train.getDirection();
            }
        }

        return route.get(nextIndex);
    }

    private int calculateNumberOfStops(int routeSize, int currentIndex, int destIndex, int direction) {
        if (direction > 0) {
            if (destIndex > currentIndex) {
                return destIndex - currentIndex;
            } else {
                return (routeSize - 1 - currentIndex) + (routeSize - 1 - destIndex);
            }
        } else {
            if (destIndex < currentIndex) {
                return currentIndex - destIndex;
            } else {
                return currentIndex + destIndex;
            }
        }
    }

    private double getSegmentTravelTime(String fromId, String toId, double speed) {
        Station from = findStationById(fromId);
        Station to = findStationById(toId);
        double distance = calculateDistance(from.getX(), from.getY(), to.getX(), to.getY());
        return distance / speed;
    }

    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void deterioratePerishables() {
        for (Train train : trains) {
            train.tick();
        }

        for (Station station : stations) {
            station.tick();
        }
    }

    /**
     * Simulate for the specified number of minutes. You should NOT modify
     * this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId) {
        Passenger passenger = new Passenger(passengerId, startStationId, destStationId);
        Station station = findStationById(startStationId);
        station.addPassenger(passenger);
    }

    public void createCargo(String startStationId, String destStationId, String cargoId, int weight) {
        Cargo cargo = new Cargo(cargoId, startStationId, destStationId, weight);
        Station station = findStationById(startStationId);
        station.addCargo(cargo);
    }

    public void createPerishableCargo(String startStationId, String destStationId, String cargoId, int weight,
            int minsTillPerish) {
        Cargo cargo = new Cargo(cargoId, startStationId, destStationId, weight, minsTillPerish);

        Station station = findStationById(startStationId);
        station.addCargo(cargo);
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId, boolean isMechanic) {
        Passenger passenger = new Passenger(passengerId, startStationId, destStationId, isMechanic);
        Station station = findStationById(startStationId);
        station.addPassenger(passenger);
    }
}
