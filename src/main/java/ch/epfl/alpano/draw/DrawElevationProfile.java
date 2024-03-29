package ch.epfl.alpano.draw;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.toRadians;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;

/**
 * Dessine un ElevationProfile.
 *
 * @author Robin Mamié
 */
final class DrawElevationProfile {

	private static final File HGT_FILE = new File("N46E006.hgt");
	private static final double MAX_ELEVATION = 1_500;
	private static final int LENGTH = 111_000;
	private static final double AZIMUTH = toRadians(27.97);
	private static final double LONGITUDE = toRadians(6.15432);
	private static final double LATITUDE = toRadians(46.20562);
	private static final int WIDTH = 800;
	private static final int HEIGHT = 100;
	private static final int BLACK = 0x00_00_00;
	private static final int WHITE = 0xFF_FF_FF;

	public static void main(final String[] args) throws Exception {
		final var startTime = System.nanoTime();

		final var dDEM = new HgtDiscreteElevationModel(HGT_FILE);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var o = new GeoPoint(LONGITUDE, LATITUDE);
		final var p = new ElevationProfile(cDEM, o, AZIMUTH, LENGTH);

		final var i = new BufferedImage(WIDTH, HEIGHT, TYPE_INT_RGB);
		for (var x = 0; x < WIDTH; ++x) {
			final var pX = x * (double) LENGTH / (WIDTH - 1);
			final var pY = p.elevationAt(pX);
			final var yL = (int) ((pY / MAX_ELEVATION) * (HEIGHT - 1));
			for (var y = 0; y < HEIGHT; ++y) {
				final var color = y < yL ? BLACK : WHITE;
				i.setRGB(x, HEIGHT - 1 - y, color);
			}
		}
		ImageIO.write(i, "png", new File("profile.png"));

		final var endTime = System.nanoTime();
		System.out.printf("DrawElevationProfile took %.3f ms.%n", (endTime - startTime) / 1e6);
	}
}
