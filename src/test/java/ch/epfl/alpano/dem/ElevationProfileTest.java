package ch.epfl.alpano.dem;

import static java.lang.Math.PI;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

final class ElevationProfileTest {

	@Test
	void constructorFailsWhenElevationModelIsNull() {
		final var p = new GeoPoint(0, 0);
		assertThrows(NullPointerException.class, () -> new ElevationProfile(null, p, 0, 100));
	}

	@Test
	void constructorFailsWhenOriginIsNull() {
		final var dem = newConstantSlopeDEM();
		assertThrows(NullPointerException.class, () -> new ElevationProfile(dem, null, 0, 100));
	}

	@Test
	void constructorFailsWhenAzimuthIsNotCanonical() {
		final var dem = newConstantSlopeDEM();
		final var p = new GeoPoint(0, 0);
		assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(dem, p, 6.3, 100));
	}

	@Test
	void constructorFailsWhenLengthIsZero() {
		final var dem = newConstantSlopeDEM();
		final var p = new GeoPoint(0, 0);
		assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(dem, p, 0, 0));
	}

	@Test
	void elevationAtFailsWhenXIsTooBig() {
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0, 0), 0, 100);
		assertThrows(IllegalArgumentException.class, () -> p.elevationAt(101));
	}

	@Test
	void elevationAtWorksOnConstantSlopeDEMGoingNorth() {
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0, 0), 0, 100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 100d * i;
			assertEquals(x, p.elevationAt(x), 1e-5);
		}
	}

	@Test
	void elevationAtWorksOnConstantSlopeDEMGoingSouth() {
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0, 0), PI, 100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 100d * i;
			assertEquals(-x, p.elevationAt(x), 1e-5);
		}
	}

	@Test
	void elevationAtWorksOnConstantSlopeDEMGoingEast() {
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0, 0), PI / 2d, 100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 100d * i;
			assertEquals(x, p.elevationAt(x), 1e-5);
		}
	}

	@Test
	void elevationAtWorksOnConstantSlopeDEMGoingWest() {
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0, 0), 3d * PI / 2d, 100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 100d * i;
			assertEquals(-x, p.elevationAt(x), 1e-5);
		}
	}

	@Test
	void positionAtFailsWhenXIsTooBig() {
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0, 0), 0, 100);
		assertThrows(IllegalArgumentException.class, () -> p.positionAt(101));
	}

	@Test
	void positionAtProducesConstantLongitudeWhenGoingNorth() {
		final var lon = toRadians(3);
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(lon, toRadians(40)), 0, 100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 500d * i;
			assertEquals(lon, p.positionAt(x).longitude(), 1e-5);
		}
	}

	@Test
	void positionAtProducesConstantLongitudeWhenGoingSouth() {
		final var lon = toRadians(3);
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(lon, toRadians(40)), PI, 100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 500d * i;
			assertEquals(lon, p.positionAt(x).longitude(), 1e-5);
		}
	}

	@Test
	void positionAtProducesConstantLatitudeWhenGoingEast() {
		final var lat = toRadians(40);
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(toRadians(3), lat), PI / 2d, 100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 500d * i;
			assertEquals(lat, p.positionAt(x).latitude(), 1e-4);
		}
	}

	@Test
	void positionAtProducesConstantLatitudeWhenGoingWest() {
		final var lat = toRadians(40);
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(toRadians(3), lat), 3d * PI / 2d,
				100_000);
		for (var i = 0; i < 100; ++i) {
			final var x = 500d * i;
			assertEquals(lat, p.positionAt(x).latitude(), 1e-4);
		}
	}

	@Test
	void slopeAtFailsWhenXIsNegative() {
		final var p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0, 0), 0, 100);
		assertThrows(IllegalArgumentException.class, () -> p.positionAt(-1));
	}

	private static ContinuousElevationModel newConstantSlopeDEM() {
		final var extent = new Interval2D(
				new Interval1D(-10_000, 10_000),
				new Interval1D(-10_000, 10_000));
		return new ContinuousElevationModel(new ConstantSlopeDEM(extent));
	}
}
