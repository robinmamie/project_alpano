package ch.epfl.alpano;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.PI;
import static java.lang.Math.floorMod;
import static java.lang.Math.nextUp;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class PanoramaParametersTest {

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
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		assertEquals(p.verticalFieldOfView(), toRadians(20), 1e-10);
	}

	@Test
	void azimuthForXFailsForNegativeX() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.azimuthForX(-1));
	}

	@Test
	void azimuthForXFailsForTooBigX() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.azimuthForX(W + 1));
	}

	@Test
	void azimuthForXWorksForFullCircle() {
		final var centralAzDeg = 90;
		final var p = new PanoramaParameters(O_POS(), O_EL, toRadians(centralAzDeg), Math2.PI2, MAX_D, 361, 201);
		for (var azDeg = 0; azDeg < 360; ++azDeg) {
			final var expectedAz = toRadians(floorMod(azDeg - centralAzDeg, 360));
			final var actualAz = p.azimuthForX(azDeg);
			assertEquals(expectedAz, actualAz, 1e-10);
		}
	}

	@Test
	void xForAzimuthFailsForTooSmallAzimuth() {
		final var p = new PanoramaParameters(O_POS(), O_EL, toRadians(10), toRadians(40), MAX_D, W, H);
		final var angle = toRadians(349.99);
		assertThrows(IllegalArgumentException.class, () -> p.xForAzimuth(angle));
	}

	@Test
	void xForAzimuthFailsForTooBigAzimuth() {
		final var p = new PanoramaParameters(O_POS(), O_EL, toRadians(10), toRadians(40), MAX_D, W, H);
		final var angle = toRadians(50.01);
		assertThrows(IllegalArgumentException.class, () -> p.xForAzimuth(angle));
	}

	@Test
	void altitudeForYFailsForNegativeY() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.altitudeForY(-1));
	}

	@Test
	void altitueForYFailsForTooBigY() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, H_FOV, MAX_D, W, H);
		assertThrows(IllegalArgumentException.class, () -> p.altitudeForY(H + 1));
	}

	@Test
	void altitudeForYWorks() {
		final var height = 201;
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, height);
		final var halfVerticalFOV = toRadians(20) / 2d;
		final var delta = toRadians(0.1);
		for (var y = 0; y < height; ++y) {
			assertEquals(halfVerticalFOV - y * delta, p.altitudeForY(y), 1e-9);
		}
	}

	@Test
	void yForAltitudeFailsForTooSmallAltitude() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		final var angle = toRadians(-10.01);
		assertThrows(IllegalArgumentException.class, () -> p.yForAltitude(angle));
	}

	@Test
	void yForAltitudeFailsForTooBigAltitude() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		final var angle = toRadians(10.01);
		assertThrows(IllegalArgumentException.class, () -> p.yForAltitude(angle));
	}

	@Test
	void azimuthForXAndXForAzimuthAreInverse() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = 1 + rng.nextInt(600);
			assertEquals(x, p.xForAzimuth(p.azimuthForX(x)), 1e-10);
		}
	}

	@Test
	void altitudeForYAndYForAltitudeAreInverse() {
		final var p = new PanoramaParameters(O_POS(), O_EL, C_AZ, toRadians(60), MAX_D, 601, 201);
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = 1 + rng.nextInt(200);
			assertEquals(x, p.yForAltitude(p.altitudeForY(x)), 1e-10);
		}
	}

	private static PanoramaParameters p = new PanoramaParameters(
			new GeoPoint(0, 0), 1000, 0, Math.PI / 2.0, 3000, 101, 51);

	@Test
	void verticalFieldOfViewWorksOnRandomPanorama() {
		final var expectedValue = Math.PI / 4.0;
		final var actualValue = p.verticalFieldOfView();
		assertEquals(expectedValue, actualValue, 1e-20);
	}

	@Test
	void azimuthForXWorksForMidPixel() {
		final var expectedAzimuth = 0;
		final var actualAzimuth = p.azimuthForX(50.0);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void azimuthForXWorksForLowPixel() {
		final var expectedAzimuth = Azimuth.canonicalize(-Math.PI / 4);
		final var actualAzimuth = p.azimuthForX(0);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void azimuthForXWorksForUppPixel() {
		final var expectedAzimuth = Math.PI / 4;
		final var actualAzimuth = p.azimuthForX(100);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void xForAzimuthWorksForMidAzimuth() {
		final var expectedAzimuth = 50.0;
		final var actualAzimuth = p.xForAzimuth(0);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void xForAzimuthWorksForLowPixel() {
		final var expectedAzimuth = 0.0;
		final var actualAzimuth = p.xForAzimuth(7.0 * Math.PI / 4);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void xForAzimuthWorksForUppPixel() {
		final var expectedAzimuth = 100.0;
		final var actualAzimuth = p.xForAzimuth(Math.PI / 4);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void altitudeForYWorksForMidPixel() {
		final var expectedAltitude = 0;
		final var actualAltitude = p.altitudeForY(25.0);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void altitudeForYWorksForLowPixel() {
		final var expectedAltitude = Azimuth.canonicalize(Math.PI / 8);
		final var actualAltitude = p.altitudeForY(0);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void altitudeForYWorksForUppPixel() {
		final var expectedAzimuth = -Math.PI / 8;
		final var actualAzimuth = p.altitudeForY(50);
		assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
	}

	@Test
	void yForAltitudeWorksForMidAzimuth() {
		final var expectedAltitude = 25.0;
		final var actualAltitude = p.yForAltitude(0);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void yForAltitudeWorksForLowPixel() {
		final var expectedAltitude = 0.0;
		final var actualAltitude = p.yForAltitude(Math.PI / 8);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}

	@Test
	void yForAltitudeWorksForUppPixel() {
		final var expectedAltitude = 50.0;
		final var actualAltitude = p.yForAltitude(-Math.PI / 8);
		assertEquals(expectedAltitude, actualAltitude, 1e-10);
	}
}
