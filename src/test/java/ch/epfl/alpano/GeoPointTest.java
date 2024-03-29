package ch.epfl.alpano;

import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoPointTest {

	private static GeoPoint CORNAVIN = new GeoPoint(toRadians(6.14308), toRadians(46.21023));
	private static GeoPoint M1_EPFL = new GeoPoint(toRadians(6.56599), toRadians(46.52224));
	private static GeoPoint FEDERAL_PALACE = new GeoPoint(toRadians(7.44428), toRadians(46.94652));
	private static GeoPoint SAENTIS = new GeoPoint(toRadians(9.34324), toRadians(47.24942));
	private static GeoPoint MONTE_TAMARO = new GeoPoint(toRadians(8.86598), toRadians(46.10386));

	@Test
	public void distanceToWorksOnKnownPoints() {
		assertEquals(226_000, M1_EPFL.distanceTo(SAENTIS), 10);
		assertEquals(81_890, M1_EPFL.distanceTo(FEDERAL_PALACE), 10);
		assertEquals(143_560, FEDERAL_PALACE.distanceTo(MONTE_TAMARO), 10);
		assertEquals(269_870, SAENTIS.distanceTo(CORNAVIN), 10);
	}

	@Test
	public void azimuthToWorksOnKnownPoints() {
		assertEquals(68.03, toDegrees(M1_EPFL.azimuthTo(SAENTIS)), 0.01);
		assertEquals(54.50, toDegrees(M1_EPFL.azimuthTo(FEDERAL_PALACE)), 0.01);
		assertEquals(130.23, toDegrees(FEDERAL_PALACE.azimuthTo(MONTE_TAMARO)), 0.01);
		assertEquals(245.82, toDegrees(SAENTIS.azimuthTo(CORNAVIN)), 0.01);
	}

	private GeoPoint rolex = new GeoPoint(Math.toRadians(6.56727), Math.toRadians(46.51779));
	private GeoPoint eiger = new GeoPoint(Math.toRadians(8.00529), Math.toRadians(46.57759));
	private GeoPoint lausanne = new GeoPoint(Math.toRadians(6.631), Math.toRadians(46.521));
	private GeoPoint moscow = new GeoPoint(Math.toRadians(37.623), Math.toRadians(55.753));

	@Test
	public void distanceWorksBetweenRolexEiger() {
		double expectedDistance = 110.49;
		double actualDistance = rolex.distanceTo(eiger) / 1000;
		assertEquals(expectedDistance, actualDistance, 10e-1);
	}

	@Test
	public void azimuthWorksBetweenRolexEiger() {
		double expectedAzimuth = 86.66;
		double actualAzimuth = Math.toDegrees(rolex.azimuthTo(eiger));
		assertEquals(expectedAzimuth, actualAzimuth, 10e-1);
	}

	@Test
	public void distanceWorksBetweenLausanneMoscow() {
		double expectedDistance = 2370;
		double actualDistance = lausanne.distanceTo(moscow) / 1000;
		assertEquals(expectedDistance, actualDistance, 10e0);
	}

	@Test
	public void azimuthWorksBetweenLausanneMoscow() {
		double expectedAzimuth = 52.95;
		double actualAzimuth = Math.toDegrees(lausanne.azimuthTo(moscow));
		assertEquals(expectedAzimuth, actualAzimuth, 10e-2);
	}

	@Test
	public void azimuthIsCanonical() {
		GeoPoint zero = new GeoPoint(0, 0);
		for (int i = 0; i < 360; ++i) {
			double expectedAngle = i;
			double actualAngle = Math.toDegrees(zero
					.azimuthTo(new GeoPoint(10e-4 * Math.sin(Math.toRadians(i)), 10e-4 * Math.cos(Math.toRadians(i)))));
			assertEquals(expectedAngle, actualAngle, 10e-5);
		}
	}

	@Test
	public void toStringOutputsCorrectValue() {
		double longitude = -5.6578;
		double latitude = 12.3457;
		GeoPoint test = new GeoPoint(Math.toRadians(longitude), Math.toRadians(latitude));
		String expectedString = "(" + longitude + "," + latitude + ")";
		String actualString = test.toString();

		assertEquals(expectedString, actualString);
	}

	@Test
	public void distanceToSamePointIsZero() {
		double expectedDistance = 0;
		double actualDistance = lausanne.distanceTo(lausanne);
		assertEquals(expectedDistance, actualDistance, 1e-10);
	}

}
