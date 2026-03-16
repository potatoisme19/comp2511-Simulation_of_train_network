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

    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {
        boolean isCyclical = hasTrackBetween(route.get(0), route.get(route.size() - 1));

        if ((type.equals("PassengerTrain") || type.equals("CargoTrain")) && isCyclical) {
            throw new InvalidRouteException("PassengerTrain and CargoTrain can only be on linear route");
        }

        Station station = findStationById(stationId);
        double x = station.getX();
        double y = station.getY();

        Train train;
        double speed;

        if (type.equals("PassengerTrain")) {
            speed = 2.0;
            train = new PassengerTrain(trainId, stationId, route, speed, isCyclical, x, y, 50);
        } else if (type.equals("CargoTrain")) {
            speed = 3.0;
            train = new CargoTrain(trainId, stationId, route, speed, isCyclical, x, y, 5000);
        } else {
            speed = 5.0;
            train = new BulletTrain(trainId, stationId, route, speed, isCyclical, x, y, 71);
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
                List<LoadInfoResponse> loads = combineLoads(t.getPassengers(), t.getCargo());

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
                List<LoadInfoResponse> loads = combineLoads(s.getPassengers(), s.getCargo());
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

    private String getPassengerType(Passenger p) {
        if (p.isMechanic()) {
            return "Mechanic";
        } else {
            return "Passenger";
        }
    }

    private String getCargoType(Cargo c) {
        if (c.isPerishable()) {
            return "PerishableCargo";
        } else {
            return "Cargo";
        }
    }

    private List<LoadInfoResponse> passengersToLoads(List<Passenger> passengers) {
        List<LoadInfoResponse> loads = new ArrayList<>();
        for (Passenger p : passengers) {
            loads.add(new LoadInfoResponse(p.getId(), getPassengerType(p)));
        }
        return loads;
    }

    private List<LoadInfoResponse> cargoToLoads(List<Cargo> cargoList) {
        List<LoadInfoResponse> loads = new ArrayList<>();
        for (Cargo c : cargoList) {
            loads.add(new LoadInfoResponse(c.getId(), getCargoType(c)));
        }
        return loads;
    }

    private List<LoadInfoResponse> combineLoads(List<Passenger> passengers, List<Cargo> cargoList) {
        List<LoadInfoResponse> loads = new ArrayList<>();
        loads.addAll(passengersToLoads(passengers));
        loads.addAll(cargoToLoads(cargoList));
        return loads;
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

        handleDeparture(train);

        if (isTrackBroken(train, nextStationId)) {
            return;
        }

        if (currentPos.isInBound(destPos, train.getSpeed())) {
            handleArrival(train, nextStation, currentPos, destPos);
        } else {
            moveTrainPartway(train, currentPos, destPos);
        }
    }

    private boolean isTrackBroken(Train train, String nextStationId) {
        String currentStationId = train.getCurrentStation();
        Track track = findTrackBetween(currentStationId, nextStationId);
        return (track != null && track.getType() == TrackType.BROKEN);
    }

    private void updateTrackDurability(Track track) {
        if (track != null && track.getType() != TrackType.NORMAL) {
            track.setDurability(track.getDurability() - 1);
        }
    }

    private void updateTrackStatus(Track track) {
        if (track.getType() == TrackType.NORMAL) {
            return;
        }

        if (track.getDurability() <= 0) {
            track.setType(TrackType.BROKEN);
        } else {
            track.setType(TrackType.UNBROKEN);
        }
    }

    private void updateAllTrackStatuses() {
        for (Track track : tracks) {
            updateTrackStatus(track);
        }
    }

    private void handleDeparture(Train train) {
        String currentStationId = train.getCurrentStation();
        if (currentStationId != null) {
            Station currentStation = findStationById(currentStationId);
            boardLoadsToTrain(train, currentStation);
            updateTrainSpeed(train);
        }
    }

    private void handleArrival(Train train, Station nextStation, Position currentPos, Position destPos) {
        train.setPosition(destPos.getX(), destPos.getY());

        if (nextStation.canStopTrain()) {
            completeArrival(train, nextStation);
        } else {
            train.setPosition(currentPos.getX(), currentPos.getY());
        }
    }

    private void completeArrival(Train train, Station station) {
        String currentStationId = train.getCurrentStation();
        String newStationId = station.getId();

        train.setCurrentStation(newStationId);
        station.addTrain(train.getId());

        if (currentStationId != null) {
            Station oldStation = findStationById(currentStationId);
            oldStation.removeTrain(train.getId());
        }

        updateTrainRouteIndex(train);

        Track track = findTrackBetween(currentStationId, newStationId);
        updateTrackDurability(track);

        unloadLoadsFromTrain(train, station);

        applyMechanicRepair(train, station);
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

        boardPassengersToTrain(train, station);
        boardCargoToTrain(train, station);
    }

    private void boardPassengersToTrain(Train train, Station station) {
        if (!(train instanceof PassengerTrain || train instanceof BulletTrain)) {
            return;
        }

        for (Passenger passenger : station.getPassengers()) {
            if (shouldBoardPassenger(train, passenger)) {
                attemptBoardPassenger(train, passenger, station);
            }
        }
    }

    private void boardCargoToTrain(Train train, Station station) {
        if (!(train instanceof CargoTrain || train instanceof BulletTrain)) {
            return;
        }

        List<Cargo> boardableCargo = getBoardableCargoAtStation(train, station);
        for (Cargo cargo : boardableCargo) {
            if (canTrainTakeCargo(train, cargo)) {
                if (cargo.isPerishable()) {
                    tryBoardPerishableCargo(train, cargo, station);
                } else {
                    tryBoardRegularCargo(train, cargo, station);
                }
            }
        }
    }

    private boolean canTrainCarryCargo(Train train) {
        return (train instanceof CargoTrain || train instanceof BulletTrain);
    }

    private List<Cargo> getBoardableCargoAtStation(Train train, Station station) {
        List<Cargo> boardableCargo = new ArrayList<>();

        for (Cargo cargo : station.getCargo()) {
            if (willTrainReachDestination(train, cargo.getDestStationId())) {
                boardableCargo.add(cargo);
            }
        }

        return boardableCargo;
    }

    private boolean canTrainTakeCargo(Train train, Cargo cargo) {
        if (train instanceof CargoTrain) {
            return canCargoTrainTakeCargo((CargoTrain) train, cargo);
        } else if (train instanceof BulletTrain) {
            return canBulletTrainTakeCargo((BulletTrain) train, cargo);
        }
        return false;
    }

    private boolean canCargoTrainTakeCargo(CargoTrain train, Cargo cargo) {
        return train.getCurrentLoad() + cargo.getWeight() <= train.getMaxCargoWeight();
    }

    private boolean canBulletTrainTakeCargo(BulletTrain train, Cargo cargo) {
        return train.getCurrentLoad() + cargo.getWeight() <= train.getMaxCargoWeight();
    }

    private void tryBoardPerishableCargo(Train train, Cargo cargo, Station station) {
        if (canReachBeforeExpiry(train, cargo)) {
            boardCargoToTrain(train, cargo, station);
        }
    }

    private void tryBoardRegularCargo(Train train, Cargo cargo, Station station) {
        boardCargoToTrain(train, cargo, station);
    }

    private void boardCargoToTrain(Train train, Cargo cargo, Station station) {
        if (train instanceof CargoTrain) {
            boardCargoToCargoTrain((CargoTrain) train, cargo, station);
        } else if (train instanceof BulletTrain) {
            boardCargoToBulletTrain((BulletTrain) train, cargo, station);
        }
    }

    private void boardCargoToCargoTrain(CargoTrain train, Cargo cargo, Station station) {
        train.addCargo(cargo);
        station.removeCargo(cargo.getId());
    }

    private void boardCargoToBulletTrain(BulletTrain train, Cargo cargo, Station station) {
        train.addCargo(cargo);
        station.removeCargo(cargo.getId());
    }

    private boolean canReachBeforeExpiry(Train train, Cargo cargo) {
        if (!cargo.isPerishable())
            return true;

        double timeToDestination = calculateTimeToDestination(train, cargo.getDestStationId());
        return timeToDestination <= cargo.getMinsTillPerish();
    }

    private boolean shouldBoardPassenger(Train train, Passenger passenger) {
        return willTrainReachDestination(train, passenger.getDestStationId());
    }

    private boolean shouldBoardCargo(Train train, Cargo cargo) {
        return willTrainReachDestination(train, cargo.getDestStationId());
    }

    private void attemptBoardPassenger(Train train, Passenger passenger, Station station) {
        if (train instanceof PassengerTrain) {
            boardPassengerToPassengerTrain((PassengerTrain) train, passenger, station);
        } else if (train instanceof BulletTrain) {
            boardPassengerToBulletTrain((BulletTrain) train, passenger, station);
        }
    }

    private void boardPassengerToPassengerTrain(PassengerTrain train, Passenger passenger, Station station) {
        if (train.getPassengers().size() < train.getMaxPassengers()) {
            train.addPassenger(passenger);
            station.removePassenger(passenger.getId());
        }
    }

    private void boardPassengerToBulletTrain(BulletTrain train, Passenger passenger, Station station) {
        if (train.getPassengers().size() < train.getMaxPassengers()) {
            train.addPassenger(passenger);
            station.removePassenger(passenger.getId());
        }
    }

    private void attemptBoardCargo(Train train, Cargo cargo, Station station) {
        if (train instanceof CargoTrain) {
            boardCargoToCargoTrain((CargoTrain) train, cargo, station);
        } else if (train instanceof BulletTrain) {
            boardCargoToBulletTrain((BulletTrain) train, cargo, station);
        }
    }

    private void updateTrainSpeed(Train train) {
        if (train instanceof PassengerTrain) {
            train.setSpeed(2.0);
        } else if (train instanceof CargoTrain) {
            CargoTrain ct = (CargoTrain) train;
            int totalWeight = ct.getCurrentLoad();
            double baseSpeed = 3.0;

            double slowdown = totalWeight * 0.0001;
            double newSpeed = baseSpeed * (1.0 - slowdown);

            train.setSpeed(Math.max(0.1, newSpeed));
        } else if (train instanceof BulletTrain) {
            BulletTrain bt = (BulletTrain) train;
            int totalWeight = bt.getCurrentLoad();
            double baseSpeed = 5.0;

            double slowdown = totalWeight * 0.0001;
            double newSpeed = baseSpeed * (1.0 - slowdown);

            train.setSpeed(Math.max(0.1, newSpeed));
        }
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

        unloadPassengersFromTrain(train, station);
        unloadCargoFromTrain(train, station);
    }

    private void unloadPassengersFromTrain(Train train, Station station) {
        if (train instanceof PassengerTrain) {
            unloadPassengersFromPassengerTrain((PassengerTrain) train, station);
        } else if (train instanceof BulletTrain) {
            unloadPassengersFromBulletTrain((BulletTrain) train, station);
        }
    }

    private void unloadCargoFromTrain(Train train, Station station) {
        if (train instanceof CargoTrain) {
            unloadCargoFromCargoTrain((CargoTrain) train, station);
        } else if (train instanceof BulletTrain) {
            unloadCargoFromBulletTrain((BulletTrain) train, station);
        }
    }

    private void unloadPassengersFromPassengerTrain(PassengerTrain train, Station station) {
        List<Passenger> arrivedPassengers = findArrivedPassengers(train.getPassengers(), station.getId());

        for (Passenger passenger : arrivedPassengers) {
            train.removePassenger(passenger);
        }
    }

    private void unloadPassengersFromBulletTrain(BulletTrain train, Station station) {
        List<Passenger> arrivedPassengers = findArrivedPassengers(train.getPassengers(), station.getId());

        for (Passenger passenger : arrivedPassengers) {
            train.removePassenger(passenger);
        }
    }

    private void unloadCargoFromCargoTrain(CargoTrain train, Station station) {
        List<Cargo> arrivedCargo = findArrivedCargo(train.getCargo(), station.getId());

        for (Cargo cargo : arrivedCargo) {
            train.removeCargo(cargo);
            station.addCargo(cargo);
        }
    }

    private void unloadCargoFromBulletTrain(BulletTrain train, Station station) {
        List<Cargo> arrivedCargo = findArrivedCargo(train.getCargo(), station.getId());

        for (Cargo cargo : arrivedCargo) {
            train.removeCargo(cargo);
            station.addCargo(cargo);
        }
    }

    private List<Passenger> findArrivedPassengers(List<Passenger> passengers, String stationId) {
        List<Passenger> arrived = new ArrayList<>();
        for (Passenger p : passengers) {
            if (p.getDestStationId().equals(stationId)) {
                arrived.add(p);
            }
        }
        return arrived;
    }

    private List<Cargo> findArrivedCargo(List<Cargo> cargoList, String stationId) {
        List<Cargo> arrived = new ArrayList<>();
        for (Cargo c : cargoList) {
            if (c.getDestStationId().equals(stationId)) {
                arrived.add(c);
            }
        }
        return arrived;
    }

    private boolean willTrainReachDestination(Train train, String destStationId) {
        List<String> route = train.getRoute();
        int currentIndex = train.getRouteIndex();
        int destIndex = route.indexOf(destStationId);

        if (destIndex == -1)
            return false;

        if (train.isCyclical()) {
            return true;
        } else {
            if (train.getDirection() > 0) {
                return destIndex > currentIndex;
            } else {
                return destIndex < currentIndex;
            }
        }
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

    private void applyMechanicRepair(Train train, Station station) {
        // TODO: Implement mechanic repair logic
        // This will be implemented in Task C
    }

    private void deterioratePerishables() {
        for (Train train : trains) {
            if (train instanceof CargoTrain) {
                removeExpiredCargoFromCargoTrain((CargoTrain) train);
            } else if (train instanceof BulletTrain) {
                removeExpiredCargoFromBulletTrain((BulletTrain) train);
            }
        }

        // Deteriorate cargo at stations
        for (Station station : stations) {
            removeExpiredCargoFromStation(station);
        }
    }

    private void removeExpiredCargoFromCargoTrain(CargoTrain train) {
        List<Cargo> expired = new ArrayList<>();

        for (Cargo cargo : train.getCargo()) {
            cargo.decrementPerishTime();
            if (cargo.hasExpired()) {
                expired.add(cargo);
            }
        }

        for (Cargo cargo : expired) {
            train.removeCargo(cargo);
        }

        if (!expired.isEmpty()) {
            updateTrainSpeed(train);
        }
    }

    private void removeExpiredCargoFromBulletTrain(BulletTrain train) {
        List<Cargo> expired = new ArrayList<>();

        for (Cargo cargo : train.getCargo()) {
            cargo.decrementPerishTime();
            if (cargo.hasExpired()) {
                expired.add(cargo);
            }
        }

        for (Cargo cargo : expired) {
            train.removeCargo(cargo);
        }

        if (!expired.isEmpty()) {
            updateTrainSpeed(train);
        }
    }

    private void removeExpiredCargoFromStation(Station station) {
        List<Cargo> expired = new ArrayList<>();

        for (Cargo cargo : station.getCargo()) {
            cargo.decrementPerishTime();
            if (cargo.hasExpired()) {
                expired.add(cargo);
            }
        }

        for (Cargo cargo : expired) {
            station.removeCargo(cargo.getId());
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

    public void createTrack(String trackId, String fromStationId, String toStationId, boolean isBreakable) {
        // Todo: Task ci
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId, boolean isMechanic) {
        Passenger passenger = new Passenger(passengerId, startStationId, destStationId);
        Station station = findStationById(startStationId);
        station.addPassenger(passenger);
    }
}
