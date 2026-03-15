package unsw.trains;

import java.util.ArrayList;
import java.util.List;

import unsw.exceptions.InvalidRouteException;
import unsw.response.models.*;

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
        int maxTrains;
        switch (type) {
            case "PassengerStation":
                maxTrains = 2;
                break;
            case "CargoStation":
                maxTrains = 4;
                break;
            case "CentralStation":
                maxTrains = 8;
                break;
            case "DepotStation":
                maxTrains = 8;
                break;
            default:
                maxTrains = 0;
        }

        Station station = new Station(stationId, type, x, y, maxTrains);
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
            throw new InvalidRouteException("PassengerTrain and CargoTrain can only be in linear routes");
        }

        double speed;
        if (type.equals("PassengerTrain"))
            speed = 2.0;
        else if (type.equals("CargoTrain"))
            speed = 3.0;
        else
            speed = 5.0;

        Station station = findStationById(stationId);
        double x = station.getX();
        double y = station.getY();

        Train train = new Train(trainId, type, stationId, route, speed, isCyclical, x, y);
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
        // Todo: Task avii
        return null;
    }

    public StationInfoResponse getStationInfo(String stationId) {
        // Todo: Task aviii
        return null;
    }

    public TrackInfoResponse getTrackInfo(String trackId) {
        // Todo: Task aix
        return null;
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
