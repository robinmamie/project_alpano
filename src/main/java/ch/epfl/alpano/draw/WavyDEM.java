package ch.epfl.alpano.draw;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import ch.epfl.alpano.Interval2D;
import ch.epfl.alpano.dem.DiscreteElevationModel;

/**
 * Définit un MNT test.
 *
 * @author Robin Mamié
 */
public final class WavyDEM implements DiscreteElevationModel {

	private static final double PERIOD = 100d;
	private static final double HEIGHT = 1000d;
	private final Interval2D extent;

	public WavyDEM(final Interval2D extent) {
		this.extent = extent;
	}

	@Override
	public Interval2D extent() {
		return extent;
	}

	@Override
	public double elevationSample(final int x, final int y) {
		final var x1 = PI * 2d * x / PERIOD;
		final var y1 = PI * 2d * y / PERIOD;
		return (1 + sin(x1) * cos(y1)) / 2d * HEIGHT;
	}
}
