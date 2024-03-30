package ch.epfl.alpano.draw;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.toRadians;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;

/**
 * Dessine un HgtDEM.
 *
 * @author Robin Mamié
 */
final class DrawHgtDEM {

	private static final Logger logger = LogManager.getLogger(DrawHgtDEM.class);

	private static final File HGT_FILE = new File(DrawHgtDEM.class.getResource("/N46E006.hgt").getFile());
	private static final double ORIGIN_LON = toRadians(6);
	private static final double ORIGIN_LAT = toRadians(45);
	private static final double WIDTH = toRadians(3);
	private static final int IMAGE_SIZE = 300;
	private static final double MIN_ELEVATION = 200;
	private static final double MAX_ELEVATION = 1_500;

	public static void main(final String[] args) throws Exception {
		final var startTime = System.nanoTime();

		final var dDEM = new HgtDiscreteElevationModel(HGT_FILE);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var step = WIDTH / (IMAGE_SIZE - 1);
		final var i = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, TYPE_INT_RGB);
		for (var x = 0; x < IMAGE_SIZE; ++x) {
			final var lon = ORIGIN_LON + x * step;
			for (var y = 0; y < IMAGE_SIZE; ++y) {
				final var lat = ORIGIN_LAT + y * step;
				final var p = new GeoPoint(lon, lat);
				final var el = (cDEM.elevationAt(p) - MIN_ELEVATION) / (MAX_ELEVATION - MIN_ELEVATION);
				i.setRGB(x, IMAGE_SIZE - 1 - y, gray(el));
			}
		}
		ImageIO.write(i, "png", new File("dem.png"));

		final var endTime = System.nanoTime();
		logger.info("DrawHgtDEM took {} ms.", (endTime - startTime) / 1e6);
	}

	private static int gray(final double v) {
		final var clampedV = max(0, min(v, 1));
		final var gray = (int) (255.9999 * clampedV);
		return (gray << 16) | (gray << 8) | gray;
	}
}
