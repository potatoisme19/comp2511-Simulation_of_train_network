package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.*;
import unsw.utils.Position;

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
        // Todo: Task bi
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
        // Todo: Task bii
    }

    public void createCargo(String startStationId, String destStationId, String cargoId, int weight) {
        // Todo: Task bii
    }

    public void createPerishableCargo(String startStationId, String destStationId, String cargoId, int weight,
            int minsTillPerish) {
        // Todo: Task biii
    }

    public void createTrack(String trackId, String fromStationId, String toStationId, boolean isBreakable) {
        // Todo: Task ci
    }

    public void createPassenger(String startStationId, String destStationId, String passengerId, boolean isMechanic) {
        // Todo: Task cii
    }
}
