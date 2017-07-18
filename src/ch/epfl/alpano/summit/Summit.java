package ch.epfl.alpano.summit;

import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.GeoPoint;

/**
 * Représente un sommet alpin par son nom, sa position et son altitude. Classe
 * immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class Summit extends Labelizable {

    /**
     * Le nom du sommet.
     */
    private final String name;
    
    /**
     * La position du sommet.
     */
    private final GeoPoint position;
    
    /**
     * L'élévation du sommet.
     */
    private final int elevation;

    /**
     * Construit un sommet.
     * 
     * @param name
     *            Le nom du sommet.
     * @param position
     *            La position du sommet.
     * @param elevation
     *            L'altitude du sommet.
     * 
     * @throws NullPointerException
     *             si le nom ou la position sont null
     */
    public Summit(String name, GeoPoint position, int elevation) {
        this.name = requireNonNull(name, "The given name is null.");
        this.position = requireNonNull(position, "The given position is null.");
        this.elevation = elevation;
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
        return 0;
    }
    
}
