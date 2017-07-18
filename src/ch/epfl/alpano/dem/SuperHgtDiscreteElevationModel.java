package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.io.File;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

/**
 * Représente un MNT discret obtenu d'un fichier au format HGT. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class SuperHgtDiscreteElevationModel
        implements DiscreteElevationModel {

    public static final SuperHgtDiscreteElevationModel FULL = new SuperHgtDiscreteElevationModel();

    protected static final int BASE_LON = 6;
    protected static final int MAX_LON = 12;
    protected static final int BASE_LAT = 45;
    protected static final int MAX_LAT = 48;

    private final HgtDiscreteElevationModel[] sources;

    private final int baseLonIndex;
    private final int baseLatIndex;
    private final int sizeHor;

    /**
     * L'étendue du HgtDEM calculée dans le constructeur.
     */
    private final Interval2D extent;

    public SuperHgtDiscreteElevationModel(int lonMin, int lonMax, int latMin,
            int latMax) {

        this.sizeHor = lonMax - lonMin;
        int sizeVer = latMax - latMin;
        int size = sizeHor * sizeVer;
        checkArgument(lonMin < lonMax && latMin < latMax);
        checkArgument(BASE_LON <= lonMin && lonMax <= MAX_LON);
        checkArgument(BASE_LAT <= latMin && latMax <= MAX_LAT);
        sources = new HgtDiscreteElevationModel[size];
        for (int i = 0; i < size; ++i)
            sources[i] = new HgtDiscreteElevationModel(
                    new File("N" + (latMin + i / sizeHor) + "E"
                            + String.format("%03d", (lonMin + (i % sizeHor)))
                            + ".hgt"));

        int lonIndex = lonMin * SAMPLES_PER_DEGREE;
        this.baseLonIndex = lonIndex + SAMPLES_PER_DEGREE;
        int latIndex = latMin * SAMPLES_PER_DEGREE;
        this.baseLatIndex = latIndex + SAMPLES_PER_DEGREE;
        this.extent = new Interval2D(
                new Interval1D(lonIndex,
                        lonIndex + SAMPLES_PER_DEGREE * sizeHor),
                new Interval1D(latIndex,
                        latIndex + SAMPLES_PER_DEGREE * sizeVer));

    }

    private SuperHgtDiscreteElevationModel() {
        this(BASE_LON, MAX_LON, BASE_LAT, MAX_LAT);
    }

    @Override
    public Interval2D extent() {
        return extent;
    }

    private int getArray(int x, int y) {
        int i = 0;
        while (x > baseLonIndex) {
            x -= SAMPLES_PER_DEGREE;
            i += 1;
        }
        while (y > baseLatIndex) {
            y -= SAMPLES_PER_DEGREE;
            i += sizeHor;
        }
        return i;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y),
                "The HgtDEM does not contain the given index.");
        return sources[getArray(x, y)].elevationSample(x, y);
    }

}
