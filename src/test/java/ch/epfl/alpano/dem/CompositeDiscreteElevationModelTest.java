package ch.epfl.alpano.dem;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

final class CompositeDiscreteElevationModelTest {

	private final static Interval2D ext1 = new Interval2D(
			new Interval1D(-100_000, 100_000),
			new Interval1D(0, 100_000));
	private final static Interval2D ext2 = new Interval2D(
			new Interval1D(-100_000, 100_000),
			new Interval1D(100_001, 200_000));
	private final static Interval2D ext12 = new Interval2D(
			new Interval1D(-100_000, 100_000),
			new Interval1D(0, 200_000));
	private final static Interval2D ext3 = new Interval2D(
			new Interval1D(0, 99_999),
			new Interval1D(0, 100_001));

	@Test
	void samplesPerRadiansHasCorrectValue() {
		assertEquals(206264.80624709636, DiscreteElevationModel.SAMPLES_PER_RADIAN, 1e-8);
	}

	@Test
	void sampleIndexWorksOnRandomValues() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var arcSeconds = rng.nextInt(2_000_000) - 1_000_000;
			final var angle = toRadians(arcSeconds / 3_600d);
			assertEquals(arcSeconds, DiscreteElevationModel.sampleIndex(angle), 1e-5);
		}
	}

	@Test
	void unionFailsIfExtentsNotUnionable() {
		final var dem1 = new ConstantElevationDEM__Prof(ext1, 0);
		final var dem2 = new ConstantElevationDEM__Prof(ext3, 0);
		assertThrows(IllegalArgumentException.class, () -> dem1.union(dem2));
	}

	@Test
	void extentOfUnionIsUnionOfExtent() {
		final var dem1 = new ConstantElevationDEM__Prof(ext1, 0);
		final var dem2 = new ConstantElevationDEM__Prof(ext2, 0);
		final var dem12 = dem1.union(dem2);
		assertEquals(ext12, dem12.extent());
	}

	@Test
	void elevationSampleFailsWhenOutsideOfExtent() {
		final var dem1 = new ConstantElevationDEM__Prof(ext1, 0);
		final var dem2 = new ConstantElevationDEM__Prof(ext2, 0);
		final var dem12 = dem1.union(dem2);
		assertThrows(IllegalArgumentException.class, () -> dem12.elevationSample(0, 200_001));
	}

	@Test
	void elevationSampleWorksOnBothSubDEMs() {
		final var dem1 = new ConstantElevationDEM__Prof(ext1, 1);
		final var dem2 = new ConstantElevationDEM__Prof(ext2, 2);
		final var dem12 = dem1.union(dem2);
		assertEquals(1, dem12.elevationSample(-100_000, 0), 0);
		assertEquals(1, dem12.elevationSample(100_000, 0), 0);
		assertEquals(1, dem12.elevationSample(100_000, 100_000), 0);
		assertEquals(1, dem12.elevationSample(-100_000, 100_000), 0);
		assertEquals(2, dem12.elevationSample(-100_000, 100_001), 0);
		assertEquals(2, dem12.elevationSample(100_000, 100_001), 0);
		assertEquals(2, dem12.elevationSample(100_000, 200_000), 0);
		assertEquals(2, dem12.elevationSample(-100_000, 200_000), 0);
	}
}

final class ConstantElevationDEM__Prof implements DiscreteElevationModel {
	private final Interval2D extent;
	private final double elevation;
	boolean isClosed = false;

	public ConstantElevationDEM__Prof(final Interval2D extent, final double elevation) {
		this.extent = extent;
		this.elevation = elevation;
	}

	@Override
	public Interval2D extent() {
		return extent;
	}

	@Override
	public double elevationSample(final int x, final int y) {
		return elevation;
	}
}