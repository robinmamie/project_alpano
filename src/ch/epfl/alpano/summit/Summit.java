package ch.epfl.alpano.summit;

import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * 
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Summit {
    
    private final String name;
    private final GeoPoint position;
    private final int elevation;

    public Summit(String name, GeoPoint position, int elevation) {
        this.name = name;
        this.position = requireNonNull(position);
        this.elevation = elevation;
    }

    public String name() {
        return name;
    }

    public GeoPoint position() {
        return position;
    }

    public int elevation() {
        return elevation;
    }

    @Override
    public String toString() {
        return new StringBuilder(name)
                .append(" ")
                .append(position)
                .append(" ")
                .append(elevation)
                .toString();
    }

    
}
