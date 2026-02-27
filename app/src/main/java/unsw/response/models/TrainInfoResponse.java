package unsw.response.models;

import unsw.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a generic response for train information.
 *
 * @note You can't store this class as an attribute in another, and should just instantiate it
 *       when needed. In other words, there should never exist a "has-a" relationship between
 *       this class and any other class. Doing so will make you lose marks for domain modelling.
 *
 *       You shouldn't modify or move this file. Refer to the assignment specification.
 *
 * @author Daniel Khuu
 */
public final class TrainInfoResponse {
    /**
     * The unique ID of the train.
     **/
    private final String trainId;
    /**
     * The ID of the station the train is docked at, or the track the train is on.
     **/
    private final String location;
    /**
     * The type of the train i.e. PassengerTrain, CargoTrain, RepairTrain, BulletTrain
     **/
    private final String type;
    /**
     * The position of the train on the map (x and y-coords, see Position.java).
     **/
    private final Position position;

    /**
     * A list of info responses of the loads currently present on the train.
     **/
    private final List<LoadInfoResponse> loads;

    /**
     * Constructor for a trainInfoResponse representing a train with no loads on it
     */
    public TrainInfoResponse(String trainId, String location, String type, Position position) {
        this(trainId, location, type, position, new ArrayList<>());
    }

    /**
     * Full constructor for a trainInfoResponse
     */
    public TrainInfoResponse(String trainId, String location, String type, Position position,
            List<LoadInfoResponse> loads) {
        this.trainId = trainId;
        this.location = location;
        this.type = type;
        this.position = position;
        this.loads = loads;
    }

    public Position getPosition() {
        return position;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public List<LoadInfoResponse> getLoads() {
        return loads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (o instanceof TrainInfoResponse other) {
            return Objects.equals(trainId, other.trainId) && Objects.equals(location, other.location)
                    && Objects.equals(type, other.type) && Objects.equals(position, other.position)
                    && Objects.equals(loads, other.loads);
        }
        return false;
    }

    @Override
    public String toString() {
        return "TrainInfoResponse [trainId=" + trainId + ", location=" + location + ", type=" + type + ", position="
                + position + ", loads=" + loads + "]";
    }
}
