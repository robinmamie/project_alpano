package ch.epfl.alpano;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.PI;
import static java.lang.Math.floorMod;
import static java.lang.Math.nextUp;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

class PanoramaParametersTest {

	// Default (and valid) arguments for constructor
	private static GeoPoint O_POS() {
		return new GeoPoint(toRadians(4), toRadians(4));
	}

	private static int O_EL = 1000;
	private static double C_AZ = toRadians(180);
	private static double H_FOV = toRadians(60);
	private static int MAX_D = 1000;
	private static int W = 100, H = 100;

	@Test
	void constructorFailsWithNullObserverPosition() {
		assertThrows(NullPointerException.class, () -> new PanoramaParameters(null, O_EL, C_AZ, H_FOV, MAX_D, W, H));
	}

	@Test
	void constructorFailsWithNonCanonicalAzimuth() {
		final var oPos = O_POS();
		assertThrows(IllegalArgumentException.class, () -> new PanoramaParameters(oPos, O_EL, 42d, H_FOV, MAX_D, W, H));
	}

	@Test
	void constructorFailsWithZeroFieldOfView() {
		final var oPos = O_POS();
		assertThrows(IllegalArgumentException.class, () -> new PanoramaParameters(oPos, O_EL, C_AZ, 0, MAX_D, W, H));
	}

	@Test
	void constructorFailsWithTooLargeOfView() {
		final var oPos = O_POS();
		final var fov = nextUp(2d * PI);
		assertThrows(IllegalArgumentException.class, () -> new PanoramaParameters(oPos, O_EL, C_AZ, fov, MAX_D, W, H));
	}

	@Test
	void constructorFailsWithZeroWidth() {
		final var oPos = O_POS();
		assertThrows(IllegalArgumentException.class,
				() -> new PanoramaParameters(oPos, O_EL, C_AZ, H_FOV, MAX_D, 0, H));
	}

	@Test
	void constructorFailsWithZeroHeight() {
		final var oPos = O_POS();
		assertThrows(IllegalArgumentException.class,
				() -> new PanoramaParameters(oPos, O_EL, C_AZ, H_FOV, MAX_D, W, 0));
	}

	@Test
	void constructorFailsWithZeroMaxDistance() {
		final var oPos = O_POS();
		assertThrows(IllegalArgumentException.class, () -> new PanoramaParameters(oPos, O_EL, C_AZ, H_FOV, 0, W, H));
	}

	@Test
	void verticalFieldOfViewIsCorrect() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		assertEquals(p.verticalFieldOfView(), toRadians(20), 1e-10);
	}

	@Test
	void azimuthForXFailsForNegativeX() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.azimuthForX(-1));
	}

	@Test
	void azimuthForXFailsForTooBigX() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.azimuthForX(W + 1));
	}

	@Test
	void azimuthForXWorksForFullCircle() {
		int centralAzDeg = 90;
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, toRadians(centralAzDeg), Math2.PI2, MAX_D, 361,
				201);
		for (int azDeg = 0; azDeg < 360; ++azDeg) {
			double expectedAz = toRadians(floorMod(azDeg - centralAzDeg, 360));
			double actualAz = p.azimuthForX(azDeg);
			assertEquals(expectedAz, actualAz, 1e-10);
		}
	}

	@Test
	void xForAzimuthFailsForTooSmallAzimuth() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, toRadians(10), toRadians(40), MAX_D, W, H);
		final var angle = toRadians(349.99);
		assertThrows(IllegalArgumentException.class, () -> p.xForAzimuth(angle));
	}

	@Test
	void xForAzimuthFailsForTooBigAzimuth() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, toRadians(10), toRadians(40), MAX_D, W, H);
		final var angle = toRadians(50.01);
		assertThrows(IllegalArgumentException.class, () -> p.xForAzimuth(angle));
	}

	@Test
	void altitudeForYFailsForNegativeY() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.altitudeForY(-1));
	}

	@Test
	void altitueForYFailsForTooBigY() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.altitudeForY(H + 1));
	}

	@Test
	void altitudeForYWorks() {
		int height = 201;
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, height);
		double halfVerticalFOV = toRadians(20) / 2d;
		double delta = toRadians(0.1);
		for (int y = 0; y < height; ++y) {
			assertEquals(halfVerticalFOV - y * delta, p.altitudeForY(y), 1e-9);
		}
	}

	@Test
	void yForAltitudeFailsForTooSmallAltitude() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		final var angle = toRadians(-10.01);
		assertThrows(IllegalArgumentException.class, () -> p.yForAltitude(angle));
	}

	@Test
	void yForAltitudeFailsForTooBigAltitude() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		final var angle = toRadians(10.01);
		assertThrows(IllegalArgumentException.class, () -> p.yForAltitude(angle));
	}

	@Test
	void azimuthForXAndXForAzimuthAreInverse() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		Random rng = newRandom();
		for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
			int x = 1 + rng.nextInt(600);
			assertEquals(x, p.xForAzimuth(p.azimuthForX(x)), 1e-10);
		}
	}

	@Test
	void altitudeForYAndYForAltitudeAreInverse() {
		PanoramaParameters p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		Random rng = newRandom();
		for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
			int x = 1 + rng.nextInt(200);
			assertEquals(x, p.yForAltitude(p.altitudeForY(x)), 1e-10);
		}
	}

	private static PanoramaParameters p = new PanoramaParameters(
			new GeoPoint(0, 0), 1000, 0, Math.PI / 2.0, 3000, 101, 51);

	@Test
	void verticalFieldOfViewWorksOnRandomPanorama() {
		double expectedValue = Math.PI / 4.0;
		double actualValue = p.verticalFieldOfView();
		assertEquals(expectedValue, actualValue, 1e-20);
	}

	@Test
	void azimuthForXWorksForMidPixel() {
		double expectedAzimuth = 0;
		double actualAzimuth = p.azimuthForX(50.0);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void azimuthForXWorksForLowPixel() {
		double expectedAzimuth = Azimuth.canonicalize(-Math.PI / 4);
		double actualAzimuth = p.azimuthForX(0);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void azimuthForXWorksForUppPixel() {
		double expectedAzimuth = Math.PI / 4;
		double actualAzimuth = p.azimuthForX(100);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void xForAzimuthWorksForMidAzimuth() {
		double expectedAzimuth = 50.0;
		double actualAzimuth = p.xForAzimuth(0);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void xForAzimuthWorksForLowPixel() {
		double expectedAzimuth = 0.0;
		double actualAzimuth = p.xForAzimuth(7.0 * Math.PI / 4);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void xForAzimuthWorksForUppPixel() {
		double expectedAzimuth = 100.0;
		double actualAzimuth = p.xForAzimuth(Math.PI / 4);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void altitudeForYWorksForMidPixel() {
		double expectedAltitude = 0;
		double actualAltitude = p.altitudeForY(25.0);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void altitudeForYWorksForLowPixel() {
		double expectedAltitude = Azimuth.canonicalize(Math.PI / 8);
		double actualAltitude = p.altitudeForY(0);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void altitudeForYWorksForUppPixel() {
		double expectedAzimuth = -Math.PI / 8;
		double actualAzimuth = p.altitudeForY(50);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void yForAltitudeWorksForMidAzimuth() {
		double expectedAltitude = 25.0;
		double actualAltitude = p.yForAltitude(0);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void yForAltitudeWorksForLowPixel() {
		double expectedAltitude = 0.0;
		double actualAltitude = p.yForAltitude(Math.PI / 8);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void yForAltitudeWorksForUppPixel() {
		double expectedAltitude = 50.0;
		double actualAltitude = p.yForAltitude(-Math.PI / 8);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}
}
