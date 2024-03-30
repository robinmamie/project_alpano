package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel.MAX_LAT;
import static ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel.MAX_LON;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ShortBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

/**
 * Représente un MNT discret obtenu d'un fichier au format HGT (Hilbert). Classe
 * immuable.
 *
 * @author Robin Mamié
 */
public final class HilbertDiscreteElevationModel implements DiscreteElevationModel {

	private static final Logger logger = LogManager.getLogger(HilbertDiscreteElevationModel.class);

	private static final int N = 8192;
	private static final long N_SQUARED = (long) N * N;

	private static final int BASE_LON = SuperHgtDiscreteElevationModel.BASE_LON * SAMPLES_PER_DEGREE;
	private static final int BASE_LAT = SuperHgtDiscreteElevationModel.BASE_LAT * SAMPLES_PER_DEGREE;

	private final File hilbertHGT;
	private final ShortBuffer source;
	private final Interval2D extent;

	public HilbertDiscreteElevationModel(final int x, final int y) {
		checkArgument(0 <= x && x <= (MAX_LON * SAMPLES_PER_DEGREE - BASE_LON) / N && 0 <= y
				&& y <= (MAX_LAT * SAMPLES_PER_DEGREE - BASE_LAT) / N, "Invalid .hhgt file number.");
		this.hilbertHGT = new File(getFileName(x, y));

		final var iX = new Interval1D(BASE_LON + x * N, BASE_LON + (x + 1) * N - 1);
		final var iY = new Interval1D(BASE_LAT + y * N, BASE_LAT + (y + 1) * N - 1);
		this.extent = new Interval2D(iX, iY);

		if (!hilbertHGT.exists() || hilbertHGT.length() != N_SQUARED * 2) {
			try {
				createFile();
			} catch (final IOException e) {
				throw new IllegalArgumentException("The file is either invalid, corrupt, or not found.");
			}
		}

		try (final var stream = new FileInputStream(hilbertHGT)) {
			this.source = stream.getChannel().map(READ_ONLY, 0, N_SQUARED * 2).asShortBuffer();
		} catch (final IOException e) {
			throw new IllegalArgumentException("The file is either invalid, corrupt, or not found.");
		}

	}

	private String getFileName(final int x, final int y) {
		return String.format("alpano%d%d.hhgt", x, y);
	}

	@Override
	public Interval2D extent() {
		return extent;
	}

	@Override
	public double elevationSample(int x, int y) {
		checkArgument(extent().contains(x, y), "The HilbertDEM does not contain the given index.");
		x -= extent().iX().includedFrom();
		y -= extent().iY().includedFrom();
		var rx = 0;
		var ry = 0;
		var s = 0;
		var d = 0;
		for (s = N / 2; s > 0; s /= 2) {
			rx = (x & s) > 0 ? 1 : 0;
			ry = (y & s) > 0 ? 1 : 0;
			d += s * s * ((3 * rx) ^ ry);
			if (ry == 0) {
				if (rx == 1) {
					x = s - 1 - x;
					y = s - 1 - y;
				}
				// Swap x and y
				final var temp = x;
				x = y;
				y = temp;
			}
		}
		return source.get(d);
	}

	private void createFile() throws IOException {
		final var dem = SuperHgtDiscreteElevationModel.FULL;
		final var start = System.nanoTime();
		try (final var stream = new FileOutputStream(hilbertHGT)) {
			logger.info("Creating the file {}", hilbertHGT.getName());
			for (var i = 0; i < N_SQUARED; ++i) {
				var t = i;
				var x = 0;
				var y = 0;
				for (var s = 1; s < N; s *= 2) {
					var rx = 1 & (t / 2);
					var ry = 1 & (t ^ rx);
					// ROT
					if (ry == 0) {
						if (rx == 1) {
							x = s - 1 - x;
							y = s - 1 - y;
						}
						// Swap x and y
						var temp = x;
						x = y;
						y = temp;
					}
					x += s * rx;
					y += s * ry;
					t /= 4;
				}
				x += BASE_LON;
				y += BASE_LAT;
				final var elevation = dem.extent().contains(x, y) ? (short) dem.elevationSample(x, y) : 0;
				final var b = new byte[] { (byte) (elevation >> 8), (byte) (elevation & 0xff) };
				stream.write(b);
			}
			logger.info("{} created in {} seconds.", hilbertHGT.getName(), (System.nanoTime() - start) * 1e-9);
		}
	}

}
