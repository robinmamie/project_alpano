package ch.epfl.alpano.draw;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.toRadians;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;
import ch.epfl.alpano.dem.ContinuousElevationModel;

/**
 * Dessine un MNT test.
 *
 * @author Robin Mamié
 */
public final class DrawDEM {

	private static final Logger logger = LogManager.getLogger(DrawDEM.class);

	public static void main(final String[] args) throws IOException {
		final var startTime = System.nanoTime();

		final var dDEM1 = new WavyDEM(new Interval2D(new Interval1D(0, 50), new Interval1D(0, 100)));
		final var dDEM2 = new WavyDEM(new Interval2D(new Interval1D(50, 100), new Interval1D(0, 100)));
		final var dDEM = dDEM1.union(dDEM2);
		final var cDEM = new ContinuousElevationModel(dDEM);

		final var size = 300;
		final var scale = (100d / 3600d) / (size - 1);
		final var elI = new BufferedImage(size, size, TYPE_INT_RGB);
		final var slI = new BufferedImage(size, size, TYPE_INT_RGB);
		for (var x = 0; x < size; ++x) {
			for (var y = 0; y < size; ++y) {
				final var p = new GeoPoint(toRadians(x * scale), toRadians(y * scale));

				final var el = cDEM.elevationAt(p);
				elI.setRGB(x, y, gray(el / 1000d));

				final var sl = cDEM.slopeAt(p);
				slI.setRGB(x, y, gray(sl / (PI / 2d)));
			}
		}
		ImageIO.write(elI, "png", new File("elevation.png"));
		ImageIO.write(slI, "png", new File("slope.png"));

		final var endTime = System.nanoTime();
		logger.info("DrawDEM took {} ms.", (endTime - startTime) / 1e6);
	}

	private static int gray(final double v) {
		final var clampedV = max(0, min(v, 1));
		final var gray = (int) (255.9999 * clampedV);
		return (gray << 16) | (gray << 8) | gray;
	}
}
