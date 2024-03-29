package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.io.File;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

/**
 * Représente un MNT discret obtenu d'un fichier au format HGT. Classe immuable.
 *
 * @author Robin Mamié
 */
public final class SuperHgtDiscreteElevationModel implements DiscreteElevationModel {

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

	public SuperHgtDiscreteElevationModel(final int lonMin, final int lonMax, final int latMin, final int latMax) {
		this.sizeHor = lonMax - lonMin;
		final var sizeVer = latMax - latMin;
		final var size = sizeHor * sizeVer;
		checkArgument(lonMin < lonMax && latMin < latMax);
		checkArgument(BASE_LON <= lonMin && lonMax <= MAX_LON);
		checkArgument(BASE_LAT <= latMin && latMax <= MAX_LAT);
		this.sources = new HgtDiscreteElevationModel[size];
		for (var i = 0; i < size; ++i) {
			sources[i] = new HgtDiscreteElevationModel(new File(
					"N" + (latMin + i / sizeHor) + "E" + String.format("%03d", (lonMin + (i % sizeHor))) + ".hgt"));
		}

		final var lonIndex = lonMin * SAMPLES_PER_DEGREE;
		this.baseLonIndex = lonIndex + SAMPLES_PER_DEGREE;
		final var latIndex = latMin * SAMPLES_PER_DEGREE;
		this.baseLatIndex = latIndex + SAMPLES_PER_DEGREE;
		this.extent = new Interval2D(new Interval1D(lonIndex, lonIndex + SAMPLES_PER_DEGREE * sizeHor),
				new Interval1D(latIndex, latIndex + SAMPLES_PER_DEGREE * sizeVer));

	}

	private SuperHgtDiscreteElevationModel() {
		this(BASE_LON, MAX_LON, BASE_LAT, MAX_LAT);
	}

	@Override
	public Interval2D extent() {
		return extent;
	}

	private int getArray(int x, int y) {
		var i = 0;
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
	public double elevationSample(final int x, final int y) {
		checkArgument(extent().contains(x, y), "The HgtDEM does not contain the given index.");
		return sources[getArray(x, y)].elevationSample(x, y);
	}

}
