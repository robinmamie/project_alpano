package ch.epfl.alpano.dem;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

final class ContinuousElevationModelTest {

	private final static Interval2D EXT_100_100 = new Interval2D(
			new Interval1D(0, 100),
			new Interval1D(0, 100));

	private final static Interval2D EXT_13_13 = new Interval2D(
			new Interval1D(0, 13),
			new Interval1D(0, 13));

	@Test
	void constructorFailsWithNullDEM() {
		assertThrows(NullPointerException.class, () -> new ContinuousElevationModel(null));
	}

	@Test
	void elevationAtReturns0OutsideOfExtent() {
		final var dDEM = new ConstantElevationDEM__Prof(EXT_100_100, 1000);
		final var cDEM = new ContinuousElevationModel(dDEM);
		assertEquals(0, cDEM.elevationAt(pointForSampleIndex(101, 0)), 0);
	}

	@Test
	void elevationAtReturnsCorrectElevationInsideExtent() {
		final var elevation = 1000;
		final var dDEM = new ConstantElevationDEM__Prof(EXT_100_100, elevation);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = rng.nextDouble() * 100d;
			final var y = rng.nextDouble() * 100d;
			assertEquals(elevation, cDEM.elevationAt(pointForSampleIndex(x, y)), 1e-10);
		}
	}

	@Test
	void elevationAtInterpolatesJustOutsideExtent() {
		final var dDEM = new ConstantElevationDEM__Prof(EXT_100_100, 1000);
		final var cDEM = new ContinuousElevationModel(dDEM);
		assertEquals(500, cDEM.elevationAt(pointForSampleIndex(100.5, 10)), 1e-10);
	}

	@Test
	void elevationAtReturnsCorrectInterpolatedElevation() {
		final var dDEM = new ConstantSlopeDEM(EXT_100_100);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var rng = new Random();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = rng.nextDouble() * 100;
			final var y = rng.nextDouble() * 100;
			assertEquals((x + y) * ConstantSlopeDEM.INTER_SAMPLE_DISTANCE, cDEM.elevationAt(pointForSampleIndex(x, y)),
					1e-6);
		}
	}

	@Test
	void elevationAtStaysWithinBoundsOnRandomTerrain() {
		final var maxElevation = 1000;
		final var dDEM = new RandomElevationDEM(EXT_13_13, maxElevation);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = rng.nextDouble() * dDEM.extent().iX().size();
			final var y = rng.nextDouble() * dDEM.extent().iY().size();
			final var e = cDEM.elevationAt(pointForSampleIndex(x, y));
			assertTrue(0 <= e && e <= maxElevation);
		}
	}

	@Test
	void slopeAtReturnsCorrectInterpolatedSlope() {
		final var dDEM = new ConstantSlopeDEM(EXT_100_100);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var rng = new Random();
		final var expectedSlope = Math.acos(1 / Math.sqrt(3));
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = 5 + rng.nextDouble() * 90;
			final var y = 5 + rng.nextDouble() * 90;
			assertEquals(expectedSlope, cDEM.slopeAt(pointForSampleIndex(x, y)), 1e-4);
		}
	}

	@Test
	void slopeAtStaysWithinBoundsOnRandomTerrain() {
		final var maxElevation = 1000;
		final var dDEM = new RandomElevationDEM(EXT_13_13, maxElevation);
		final var cDEM = new ContinuousElevationModel(dDEM);
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = rng.nextDouble() * dDEM.extent().iX().size();
			final var y = rng.nextDouble() * dDEM.extent().iY().size();
			final var e = toDegrees(cDEM.slopeAt(pointForSampleIndex(x, y)));
			assertTrue(0 <= e && e < 90);
		}
	}

	private static GeoPoint pointForSampleIndex(final double x, final double y) {
		return new GeoPoint(toRadians(x / 3600d), toRadians(y / 3600d));
	}
}

final class RandomElevationDEM implements DiscreteElevationModel {
	private final Interval2D extent;
	private final double[][] elevations;

	public RandomElevationDEM(final Interval2D extent, final int maxElevation) {
		this.extent = extent;
		this.elevations = randomElevations(extent.iX().size(), extent.iY().size(), maxElevation);
	}

	private static double[][] randomElevations(final int width, final int height, final int maxElevation) {
		final var rng = newRandom();
		final var es = new double[width][height];
		for (var x = 0; x < width; ++x) {
			for (var y = 0; y < height; ++y) {
				es[x][y] = rng.nextInt(maxElevation + 1);
			}
		}
		return es;
	}

	@Override
	public Interval2D extent() {
		return extent;
	}

	@Override
	public double elevationSample(final int x, final int y) {
		return elevations[x][y];
	}
}

record ConstantSlopeDEM(Interval2D extent) implements DiscreteElevationModel {

	public final static double INTER_SAMPLE_DISTANCE = 2d * Math.PI * 6_371_000d / (3600d * 360d);

	@Override
	public double elevationSample(final int x, final int y) {
		return (x + y) * INTER_SAMPLE_DISTANCE;
	}
}