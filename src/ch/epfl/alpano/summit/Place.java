package ch.epfl.alpano.summit;

import static java.util.Objects.requireNonNull;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.Serializable;

import ch.epfl.alpano.GeoPoint;

public class Place extends Labelizable implements Serializable {

    /**
     * Serial ID.
     */
    private static final long serialVersionUID = -580071766793003692L;

    private static final int PRIORITY_RANGE = 5;

    private final String name;
    private final GeoPoint position;
    private final int elevation;
    private final int priority;

    public Place(String name, GeoPoint position, int elevation, int priority) {
        this.name = requireNonNull(name, "The given name is null.");
        this.position = requireNonNull(position, "The given position is null.");
        this.elevation = elevation;
        this.priority = min(max(-PRIORITY_RANGE, priority), PRIORITY_RANGE);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public GeoPoint position() {
        return position;
    }

    @Override
    public int elevation() {
        return elevation;
    }
    
    @Override
    public int priority() {
        return priority;
    }

}
