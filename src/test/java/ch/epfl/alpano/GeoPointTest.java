package ch.epfl.alpano;

import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class GeoPointTest {

	private static final GeoPoint CORNAVIN = new GeoPoint(toRadians(6.14308), toRadians(46.21023));
	private static final GeoPoint M1_EPFL = new GeoPoint(toRadians(6.56599), toRadians(46.52224));
	private static final GeoPoint FEDERAL_PALACE = new GeoPoint(toRadians(7.44428), toRadians(46.94652));
	private static final GeoPoint SAENTIS = new GeoPoint(toRadians(9.34324), toRadians(47.24942));
	private static final GeoPoint MONTE_TAMARO = new GeoPoint(toRadians(8.86598), toRadians(46.10386));

	@Test
	void distanceToWorksOnKnownPoints() {
		assertEquals(226_000, M1_EPFL.distanceTo(SAENTIS), 10);
		assertEquals(81_890, M1_EPFL.distanceTo(FEDERAL_PALACE), 10);
		assertEquals(143_560, FEDERAL_PALACE.distanceTo(MONTE_TAMARO), 10);
		assertEquals(269_870, SAENTIS.distanceTo(CORNAVIN), 10);
	}

	@Test
	void azimuthToWorksOnKnownPoints() {
		assertEquals(68.03, toDegrees(M1_EPFL.azimuthTo(SAENTIS)), 0.01);
		assertEquals(54.50, toDegrees(M1_EPFL.azimuthTo(FEDERAL_PALACE)), 0.01);
		assertEquals(130.23, toDegrees(FEDERAL_PALACE.azimuthTo(MONTE_TAMARO)), 0.01);
		assertEquals(245.82, toDegrees(SAENTIS.azimuthTo(CORNAVIN)), 0.01);
	}

	private static final GeoPoint ROLEX = new GeoPoint(Math.toRadians(6.56727), Math.toRadians(46.51779));
	private static final GeoPoint EIGER = new GeoPoint(Math.toRadians(8.00529), Math.toRadians(46.57759));
	private static final GeoPoint LAUSANNE = new GeoPoint(Math.toRadians(6.631), Math.toRadians(46.521));
	private static final GeoPoint MOSCOW = new GeoPoint(Math.toRadians(37.623), Math.toRadians(55.753));

	@Test
	void distanceWorksBetweenRolexEiger() {
		final var expectedDistance = 110.49;
		final var actualDistance = ROLEX.distanceTo(EIGER) / 1000;
		assertEquals(expectedDistance, actualDistance, 10e-1);
	}

	@Test
	void azimuthWorksBetweenRolexEiger() {
		final var expectedAzimuth = 86.66;
		final var actualAzimuth = Math.toDegrees(ROLEX.azimuthTo(EIGER));
		assertEquals(expectedAzimuth, actualAzimuth, 10e-1);
	}

	@Test
	void distanceWorksBetweenLausanneMoscow() {
		final var expectedDistance = 2370;
		final var actualDistance = LAUSANNE.distanceTo(MOSCOW) / 1000;
		assertEquals(expectedDistance, actualDistance, 10e0);
	}

	@Test
	void azimuthWorksBetweenLausanneMoscow() {
		final var expectedAzimuth = 52.95;
		final var actualAzimuth = Math.toDegrees(LAUSANNE.azimuthTo(MOSCOW));
		assertEquals(expectedAzimuth, actualAzimuth, 10e-2);
	}

	@Test
	void azimuthIsCanonical() {
		final var zero = new GeoPoint(0, 0);
		for (var i = 0; i < 360; ++i) {
			final var expectedAngle = i;
			final var actualAngle = Math.toDegrees(zero
					.azimuthTo(new GeoPoint(10e-4 * Math.sin(Math.toRadians(i)), 10e-4 * Math.cos(Math.toRadians(i)))));
			assertEquals(expectedAngle, actualAngle, 10e-5);
		}
	}

	@Test
	void toStringOutputsCorrectValue() {
		final var longitude = -5.6578;
		final var latitude = 12.3457;
		final var test = new GeoPoint(Math.toRadians(longitude), Math.toRadians(latitude));
		final var expectedString = "(" + longitude + "," + latitude + ")";
		final var actualString = test.toString();

		assertEquals(expectedString, actualString);
	}

	@Test
	void distanceToSamePointIsZero() {
		final var expectedDistance = 0;
		final var actualDistance = LAUSANNE.distanceTo(LAUSANNE);
		assertEquals(expectedDistance, actualDistance, 1e-10);
	}

}
