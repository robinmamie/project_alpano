package ch.epfl.alpano;

import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.Test;

class PanoramaTest {

	private static PanoramaParameters PARAMS() {
		return new PanoramaParameters(
				new GeoPoint(toRadians(46), toRadians(6)),
				1000,
				toRadians(180),
				toRadians(60),
				100_000,
				9,
				7);
	}

	@Test
	void builderFailsWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new Panorama.Builder(null));
	}

	@Test
	void builderConstructorCorrectlyInitializesSamples() {
		Panorama p = new Panorama.Builder(PARAMS()).build();
		assertEquals(Float.POSITIVE_INFINITY, p.distanceAt(0, 0), 0);
		assertEquals(0, p.longitudeAt(0, 0), 0);
		assertEquals(0, p.latitudeAt(0, 0), 0);
		assertEquals(0, p.elevationAt(0, 0), 0);
		assertEquals(0, p.slopeAt(0, 0), 0);
	}

	@Test
	void setDistanceAtFailsWithInvalidIndex() {
		Panorama.Builder b = new Panorama.Builder(PARAMS());
		assertThrows(IndexOutOfBoundsException.class, () -> b.setDistanceAt(10, 0, 1));
	}

	@Test
	void setLongitudeAtReturnsThis() {
		Panorama.Builder b = new Panorama.Builder(PARAMS());
		assertSame(b, b.setLongitudeAt(0, 0, 1));
	}

	@Test
	void setSlopeAtFailsAfterBuild() {
		Panorama.Builder b = new Panorama.Builder(PARAMS());
		b.build();
		assertThrows(IllegalStateException.class, () -> b.setSlopeAt(0, 0, 0));
	}

	@Test
	void buildFailsAfterBuild() {
		Panorama.Builder b = new Panorama.Builder(PARAMS());
		b.build();
		assertThrows(IllegalStateException.class, b::build);
	}

	@Test
	void parametersReturnsParameters() {
		PanoramaParameters ps = PARAMS();
		Panorama p = new Panorama.Builder(ps).build();
		assertSame(ps, p.parameters());
	}

	@Test
	void builderSettersWork() {
		PanoramaParameters ps = PARAMS();
		float[] values = new float[ps.width() * ps.height()];
		Random rng = newRandom();
		for (int i = 0; i < values.length; ++i)
			values[i] = rng.nextFloat() + 0.5f;

		Panorama.Builder b = new Panorama.Builder(ps);
		for (int x = 0; x < ps.width(); ++x) {
			for (int y = 0; y < ps.height(); ++y) {
				float v = values[y + x * ps.height()];
				b.setDistanceAt(x, y, v)
						.setElevationAt(x, y, v)
						.setLatitudeAt(x, y, v)
						.setLongitudeAt(x, y, v)
						.setSlopeAt(x, y, v);
			}
		}

		Panorama p = b.build();
		for (int x = 0; x < ps.width(); ++x) {
			for (int y = 0; y < ps.height(); ++y) {
				float v = values[y + x * ps.height()];
				assertEquals(v, p.distanceAt(x, y), 0);
				assertEquals(v, p.elevationAt(x, y), 0);
				assertEquals(v, p.latitudeAt(x, y), 0);
				assertEquals(v, p.longitudeAt(x, y), 0);
				assertEquals(v, p.slopeAt(x, y), 0);
			}
		}
	}
}
