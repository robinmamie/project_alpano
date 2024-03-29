package ch.epfl.alpano.draw;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.toRadians;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;

/**
 * Dessine un panorama test.
 *
 * @author Robin Mamie
 */
final class DrawPanorama {

	private static final File HGT_FILE = new File("N46E007.hgt");

	private static final int IMAGE_WIDTH = 500;
	private static final int IMAGE_HEIGHT = 200;

	private static final double ORIGIN_LON = toRadians(7.65);
	private static final double ORIGIN_LAT = toRadians(46.73);
	private static final int ELEVATION = 600;
	private static final double CENTER_AZIMUTH = toRadians(180);
	private static final double HORIZONTAL_FOV = toRadians(60);
	private static final int MAX_DISTANCE = 100_000;

	private static final PanoramaParameters PARAMS = new PanoramaParameters(new GeoPoint(ORIGIN_LON, ORIGIN_LAT),
			ELEVATION, CENTER_AZIMUTH, HORIZONTAL_FOV, MAX_DISTANCE, IMAGE_WIDTH, IMAGE_HEIGHT);

	public static void main(final String[] args) throws Exception {
		final var start = System.nanoTime();
		final var dDEM = new HgtDiscreteElevationModel(HGT_FILE);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var p = new PanoramaComputer(cDEM).computePanorama(PARAMS);

		final var i = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, TYPE_INT_RGB);

		for (var x = 0; x < IMAGE_WIDTH; ++x) {
			for (var y = 0; y < IMAGE_HEIGHT; ++y) {
				final var d = p.distanceAt(x, y);
				final var c = (d == Float.POSITIVE_INFINITY) ? 0x87_CE_EB : gray((d - 2_000) / 15_000);
				i.setRGB(x, y, c);
			}
		}
		ImageIO.write(i, "png", new File("niesen.png"));

		final var stop = System.nanoTime();
		System.out.printf("DrawPanorama took %.3f ms.%n", (stop - start) * 1e-6);
	}

	private static int gray(final double v) {
		final var clampedV = max(0, min(v, 1));
		final var gray = (int) (255.9999 * clampedV);
		return (gray << 16) | (gray << 8) | gray;
	}
}
