package ch.epfl.alpano.draw;

import static ch.epfl.alpano.Distance.toRadians;

import java.io.File;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;

abstract class AbstractDrawPanorama {

	protected static final File HGT_FILE = new File(DrawPanoramaGray.class.getResource("/N46E007.hgt").getFile());

	protected static final int IMAGE_WIDTH = 500;
	protected static final int IMAGE_HEIGHT = 200;

	private static final double ORIGIN_LON = toRadians(7.65);
	private static final double ORIGIN_LAT = toRadians(46.73);
	private static final int ELEVATION = 600;
	private static final double CENTER_AZIMUTH = toRadians(180);
	private static final double HORIZONTAL_FOV = toRadians(60);
	private static final int MAX_DISTANCE = 100_000;

	protected static final PanoramaParameters PARAMS = new PanoramaParameters(new GeoPoint(ORIGIN_LON, ORIGIN_LAT),
			ELEVATION, CENTER_AZIMUTH, HORIZONTAL_FOV, MAX_DISTANCE, IMAGE_WIDTH, IMAGE_HEIGHT);

	protected AbstractDrawPanorama() {
		// Static class
	}

	protected static Panorama panorama() throws InterruptedException {
		final var dDEM = new HgtDiscreteElevationModel(HGT_FILE);
		final var cDEM = new ContinuousElevationModel(dDEM);
		return new PanoramaComputer(cDEM).computePanorama(PARAMS);
	}

}
