package ch.epfl.alpano.summit;

import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.alpano.GeoPoint;

class SummitTest {

	@Test
	void constructorFailsWithNullName() {
		final var p = new GeoPoint(0, 0);
		assertThrows(NullPointerException.class, () -> new Summit(null, p, 1));
	}

	@Test
	void constructorFailsWithNullPosition() {
		assertThrows(NullPointerException.class, () -> new Summit("sommet", null, 1));
	}

	@Test
	void nameReturnsName() {
		String n = "sommet";
		Summit s = new Summit(n, new GeoPoint(0, 0), 1);
		assertEquals(n, s.name());
	}

	@Test
	void positionReturnsPosition() {
		GeoPoint p = new GeoPoint(toRadians(3), toRadians(5));
		Summit s = new Summit("sommet", p, 1);
		assertEquals(p, s.position());
	}

	@Test
	void elevationReturnsElevation() {
		int e = 1234;
		Summit s = new Summit("sommet", new GeoPoint(0, 0), e);
		assertEquals(e, s.elevation());
	}
}
