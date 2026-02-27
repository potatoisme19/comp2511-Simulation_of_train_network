package unsw.response.models;

import java.util.Objects;

/**
 * Represents a generic response for load information.
 *
 * @note You can't store this class as an attribute in another, and should just instantiate it
 *       when needed. In other words, there should never exist a "has-a" relationship between
 *       this class and any other class. Doing so will make you lose marks for domain modelling.
 *
 *       You shouldn't modify or move this file. Refer to the assignment specification.
 *
 * @author Daniel Khuu
 */
public final class LoadInfoResponse {
    /**
     * The unique ID of the load.
     */
    private final String loadId;
    /**
     * The type of the load i.e. exactly "Passenger" or "Cargo" or "PerishableCargo" or "Mechanic"
     */
    private final String type;

    public LoadInfoResponse(String loadId, String type) {
        this.loadId = loadId;
        this.type = type;
    }

    public String getLoadId() {
        return loadId;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof LoadInfoResponse other) {
            return Objects.equals(this.type, other.getType()) && Objects.equals(this.loadId, other.getLoadId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "LoadInfoResponse [loadId=" + loadId + ", type=" + type + "]";
    }
}
