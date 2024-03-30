package ch.epfl.alpano.summit;

import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.alpano.GeoPoint;

final class SummitTest {

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
		final var n = "sommet";
		final var s = new Summit(n, new GeoPoint(0, 0), 1);
		assertEquals(n, s.name());
		assertEquals(0, s.priority());
	}

	@Test
	void positionReturnsPosition() {
		final var p = new GeoPoint(toRadians(3), toRadians(5));
		final var s = new Summit("sommet", p, 1);
		assertEquals(p, s.position());
		assertEquals(0, s.priority());
	}

	@Test
	void elevationReturnsElevation() {
		final var e = 1234;
		final var s = new Summit("sommet", new GeoPoint(0, 0), e);
		assertEquals(e, s.elevation());
		assertEquals(0, s.priority());
	}
}
