package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

/**
 * Représente un MNT discret obtenu d'un fichier au format HGT. Classe immuable.
 *
 * @author Robin Mamié
 */
public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

	/**
	 * Taille d'un côté du MNT. Correspond au nombre de secondes d'arc par degré
	 * plus 1.
	 */
	private static final int SIDE = SAMPLES_PER_DEGREE + 1;

	/**
	 * Contient l'élévation de chaque point.
	 */
	private final ShortBuffer source;

	/**
	 * L'étendue du HgtDEM calculée dans le constructeur.
	 */
	private final Interval2D extent;

	/**
	 * Construit un MNT discret qui prend ses valeurs, des altitudes, dans un
	 * fichier.
	 * 
	 * @param file Un fichier au format .hgt et correspondant au restrictions
	 *             définies.
	 * 
	 * @throws IllegalArgumentException si le fichier ou son nom sont invalides.
	 */
	public HgtDiscreteElevationModel(File file) {

		checkArgument(file.isFile(), "The file does not exist.");

		final var fileName = file.getName();

		checkArgument(fileName.length() == 11, "The file name is invalid: it is too short or too long.");

		final var size = file.length();
		checkArgument(size == 2 * SIDE * SIDE, "The file is invalid: it does not comply to byte restrictions.");

		final var ns = fileName.charAt(0);
		final var ew = fileName.charAt(3);

		checkArgument(ns == 'N' || ns == 'S', "The file name is invalid: it isn't defined for North or South.");
		checkArgument(ew == 'E' || ew == 'W', "The file name is invalid: it isn't defined for East or West.");

		checkArgument(fileName.substring(7, 11).equals(".hgt"),
				"The file name is invalid: it doesn't end with the extension \".hgt\".");

		int lat;
		int lon;
		try {
			lat = Integer.parseInt(fileName.substring(1, 3));
			lon = Integer.parseInt(fileName.substring(4, 7));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"The file name is invalid: it does not contain numbers at the right places.");
		}

		checkArgument(0 <= lat, "The file name is invalid: it cannot have a negative latitude.");
		checkArgument(0 <= lon, "The file name is invalid: it cannot have a negative longitude.");

		checkArgument(ns != 'N' || lat < 90, "The file name is invalid: it cannot have this northern latitude.");
		checkArgument(lat <= 90, "The file name is invalid: it cannot have this latitude.");

		checkArgument(ew != 'E' || lon < 180, "The file name is invalid: it cannot have this eastern longitude.");
		checkArgument(lon <= 180, "The file name is invalid: it cannot have this longitude.");

		// Extraction des élévations.
		try (final var stream = new FileInputStream(file)) {
			this.source = stream.getChannel().map(READ_ONLY, 0, size).asShortBuffer();
		} catch (final IOException e) {
			throw new IllegalArgumentException("The file is either invalid, corrupt, or not found.");
		}

		// On enregistre l'étendue du MNT pour ne pas devoir le recalculer
		// à chaque fois que extent() est demandé. Toutes les informations
		// nécessaires ont été calculées dans ce constructeur, il paraît donc
		// judicieux de le faire ici.
		final var lonIndex = (ew == 'E' ? 1 : -1) * lon * SAMPLES_PER_DEGREE;
		final var latIndex = (ns == 'N' ? 1 : -1) * lat * SAMPLES_PER_DEGREE;
		this.extent = new Interval2D(
				new Interval1D(lonIndex, lonIndex + SAMPLES_PER_DEGREE),
				new Interval1D(latIndex, latIndex + SAMPLES_PER_DEGREE));
	}

	@Override
	public Interval2D extent() {
		return extent;
	}

	@Override
	public double elevationSample(final int x, final int y) {
		checkArgument(extent().contains(x, y), "The HgtDEM does not contain the given index.");
		return source.get((extent().iY().includedTo() - y) * SIDE + x - extent().iX().includedFrom());
	}

}
