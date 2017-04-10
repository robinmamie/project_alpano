package ch.epfl.alpano.dem;

import static java.util.Objects.requireNonNull;

import ch.epfl.alpano.Interval2D;

/**
 * Représente l'union de deux MNT discrets. Classe immuable.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
final class CompositeDiscreteElevationModel implements DiscreteElevationModel {

    /**
     * Le premier MNT.
     */
    private final DiscreteElevationModel dem1;

    /**
     * Le second MNT.
     */
    private final DiscreteElevationModel dem2;

    /**
     * Construit un CompositeDiscreteElevationModel, composé par l'union de deux
     * autres MNT.
     * 
     * @param dem1
     *            Premier MNT
     * @param dem2
     *            Second MNT
     * 
     * @throws NullPointerException
     *             si l'un des deux MNT passés en argument est null.
     */
    public CompositeDiscreteElevationModel(DiscreteElevationModel dem1,
            DiscreteElevationModel dem2) {
        this.dem1 = requireNonNull(dem1, "The first given DEM is null.");
        this.dem2 = requireNonNull(dem2, "The second given DEM is null.");
    }

    @Override
    public Interval2D extent() {
        return dem1.extent().union(dem2.extent());
    }

    @Override
    public double elevationSample(int x, int y) {
        if (dem1.extent().contains(x, y))
            return dem1.elevationSample(x, y);

        if (dem2.extent().contains(x, y))
            return dem2.elevationSample(x, y);

        throw new IllegalArgumentException(
                "The DEM does not contain the given index.");
    }

}
