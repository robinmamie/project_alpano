package ch.epfl.alpano.draw;

import static java.lang.Math.toRadians;

import java.io.File;

import javax.imageio.ImageIO;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.gui.ChannelPainter;
import ch.epfl.alpano.gui.ImagePainter;
import ch.epfl.alpano.gui.PanoramaRenderer;
import javafx.embed.swing.SwingFXUtils;

/**
 * Dessine un panorama en niveaux de gris.
 *
 * @author Robin Mamie
 */
final class DrawPanoramaGray {

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

		final var gray = ChannelPainter.maxDistanceToNeighbors(p).sub(500).div(4500).clamp().invert();

		final var distance = (ChannelPainter) p::distanceAt;
		final var opacity = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

		final var l = ImagePainter.gray(gray, opacity);

		final var i = PanoramaRenderer.renderPanorama(p, l, null);
		ImageIO.write(SwingFXUtils.fromFXImage(i, null), "png", new File("niesen-profile.png"));

		final var stop = System.nanoTime();
		System.out.printf("DrawPanoramaGray took %.3f ms.%n", (stop - start) * 1e-6);
	}
}
