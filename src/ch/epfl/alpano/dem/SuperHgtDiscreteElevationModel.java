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

    private static final int AMOUNT = 18;

    protected static final int BASE_LON = 6;
    protected static final int BASE_LAT = 45;
    private static final int BASE_LON_INDEX = (BASE_LON + 1)
            * SAMPLES_PER_DEGREE;
    private static final int BASE_LAT_INDEX = (BASE_LAT + 1)
            * SAMPLES_PER_DEGREE;
    private static final int SIZE_HOR = 6;

    private final HgtDiscreteElevationModel[] sources;
    
    /**
     * L'étendue du HgtDEM calculée dans le constructeur.
     */
    private final Interval2D extent;

    public SuperHgtDiscreteElevationModel() {
        // TODO allow custom import
        sources = new HgtDiscreteElevationModel[AMOUNT];

        for (int i = 0; i < AMOUNT; ++i)
            sources[i] = new HgtDiscreteElevationModel(
                    new File("N" + (BASE_LAT + i / SIZE_HOR) + "E"
                            + String.format("%03d", (BASE_LON + (i % SIZE_HOR)))
                            + ".hgt"));

        int lonIndex = BASE_LON * SAMPLES_PER_DEGREE;
        int latIndex = BASE_LAT * SAMPLES_PER_DEGREE;
        this.extent = new Interval2D(
                new Interval1D(lonIndex, lonIndex + SAMPLES_PER_DEGREE * 6),
                new Interval1D(latIndex, latIndex + SAMPLES_PER_DEGREE * 3));
    }

    @Override
    public Interval2D extent() {
        return extent;
    }

    private int getArray(int x, int y) {
        int i = 0;
        while (x > BASE_LON_INDEX) {
            x -= SAMPLES_PER_DEGREE;
            i += 1;
        }
        while (y > BASE_LAT_INDEX) {
            y -= SAMPLES_PER_DEGREE;
            i += SIZE_HOR;
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
