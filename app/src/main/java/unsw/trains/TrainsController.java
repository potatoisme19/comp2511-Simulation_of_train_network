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
    // Add any fields here if necessary
    public void createStation(String stationId, String type, double x, double y) {
        // Todo: Task ai
    }

    public void createTrack(String trackId, String fromStationId, String toStationId) {
        // Todo: Task aii
    }

    public void createTrain(String trainId, String type, String stationId, List<String> route)
            throws InvalidRouteException {
        // Todo: Task aiii
    }

    public List<String> listStationIds() {
        // Todo: Task aiv
        return new ArrayList<>();
    }

    public List<String> listTrackIds() {
        // Todo: Task av
        return new ArrayList<>();
    }

    public List<String> listTrainIds() {
        // Todo: Task avi
        return new ArrayList<>();
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
