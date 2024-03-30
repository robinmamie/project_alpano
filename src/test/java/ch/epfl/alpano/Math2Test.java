package ch.epfl.alpano;

import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Math2.bilerp;
import static ch.epfl.alpano.Math2.firstIntervalContainingRoot;
import static ch.epfl.alpano.Math2.floorMod;
import static ch.epfl.alpano.Math2.haversin;
import static ch.epfl.alpano.Math2.improveRoot;
import static ch.epfl.alpano.Math2.lerp;
import static ch.epfl.alpano.Math2.sq;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import org.junit.jupiter.api.Test;

final class Math2Test {

	@Test
	void sqSquaresRandomValues() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x = rng.nextDouble() * 1_000d - 500d;
			assertEquals(x * x, sq(x), 1e-10);
		}
	}

	@Test
	void floorModWorksOnRandomValues() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var n = rng.nextDouble() * 1_000d - 500d;
			var d = 0d;
			while (d == 0) {
				d = rng.nextDouble() * 1_000d - 500d;
			}
			final var q = (int) floor(n / d);
			final var r = floorMod(n, d);
			assertEquals(n, q * d + r, 1e-10);
		}
	}

	@Test
	void haversinWorksOnRandomAngles() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var a = nextAngle(rng);
			final var h = (1d - cos(a)) / 2d;
			assertEquals(h, haversin(a), 1e-10);
		}
	}

	@Test
	void angularDistanceWorksOnKnownAngles() {
		final var data = new double[] {
				0, 45, 45,
				45, 0, -45,
				0, 179, 179,
				0, 181, -179,
				181, 359, 178,
				181, 2, -179
		};
		for (var i = 0; i < data.length; i += 3) {
			final var a1 = toRadians(data[i]);
			final var a2 = toRadians(data[i + 1]);
			final var expectedD = toRadians(data[i + 2]);
			assertEquals(expectedD, angularDistance(a1, a2), 1e-10);
		}
	}

	@Test
	void angularDistanceIsInExpectedRange() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var a1 = nextAngle(rng);
			final var a2 = nextAngle(rng);
			final var d = angularDistance(a1, a2);
			assertTrue(-PI <= d && d < PI);
		}
	}

	@Test
	void angularDistanceIsSymmetric() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var a1 = nextAngle(rng);
			final var a2 = nextAngle(rng);
			assertEquals(0, angularDistance(a1, a2) + angularDistance(a2, a1), 1e-10);
		}
	}

	@Test
	void lerpIsFirstValueAtStart() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var v1 = (rng.nextDouble() - 0.5) * 1000d;
			final var v2 = (rng.nextDouble() - 0.5) * 1000d;
			assertEquals(v1, lerp(v1, v2, 0), 1e-10);
		}
	}

	@Test
	void lerpIsAverageValueAtMiddle() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var v1 = (rng.nextDouble() - 0.5) * 1000d;
			final var v2 = (rng.nextDouble() - 0.5) * 1000d;
			assertEquals((v1 + v2) / 2d, lerp(v1, v2, 0.5), 1e-10);
		}
	}

	@Test
	void lerpIsSecondValueAtEnd() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var v1 = (rng.nextDouble() - 0.5) * 1000d;
			final var v2 = (rng.nextDouble() - 0.5) * 1000d;
			assertEquals(v2, lerp(v1, v2, 1), 1e-10);
		}
	}

	@Test
	void lerpIsInExpectedRange() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var v1 = (rng.nextDouble() - 0.5) * 1000d;
			final var v2 = (rng.nextDouble() - 0.5) * 1000d;
			final var p = rng.nextDouble();
			final var v = lerp(v1, v2, p);
			assertTrue(min(v1, v2) <= v && v <= max(v1, v2));
		}
	}

	@Test
	void bilerpIsInExpectedRange() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var v1 = (rng.nextDouble() - 0.5) * 1000d;
			final var v2 = (rng.nextDouble() - 0.5) * 1000d;
			final var v3 = (rng.nextDouble() - 0.5) * 1000d;
			final var v4 = (rng.nextDouble() - 0.5) * 1000d;
			final var x = rng.nextDouble();
			final var y = rng.nextDouble();
			final var v = bilerp(v1, v2, v3, v4, x, y);
			assertTrue(min(min(v1, v2), min(v3, v4)) <= v && v <= max(max(v1, v2), max(v3, v4)));
		}
	}

	@Test
	void bilerpIsCorrectInCorners() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var v0 = rng.nextDouble();
			final var v1 = rng.nextDouble();
			final var v2 = rng.nextDouble();
			final var v3 = rng.nextDouble();
			assertEquals(v0, bilerp(v0, v1, v2, v3, 0, 0), 1e-10);
			assertEquals(v1, bilerp(v1, v1, v2, v3, 1, 0), 1e-10);
			assertEquals(v2, bilerp(v2, v1, v2, v3, 0, 1), 1e-10);
			assertEquals(v3, bilerp(v3, v1, v2, v3, 1, 1), 1e-10);
		}
	}

	@Test
	void bilerpLerpsAlongSides() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var v0 = rng.nextDouble();
			final var v1 = rng.nextDouble();
			final var v2 = rng.nextDouble();
			final var v3 = rng.nextDouble();
			assertEquals((v0 + v1) / 2d, bilerp(v0, v1, v2, v3, 0.5, 0), 1e-10);
			assertEquals((v0 + v2) / 2d, bilerp(v0, v1, v2, v3, 0, 0.5), 1e-10);
			assertEquals((v2 + v3) / 2d, bilerp(v0, v1, v2, v3, 0.5, 1), 1e-10);
			assertEquals((v1 + v3) / 2d, bilerp(v0, v1, v2, v3, 1, 0.5), 1e-10);
		}
	}

	@Test
	void firstIntervalContainingRootWorksOnSin() {
		final var i1 = firstIntervalContainingRoot(new Sin(), -1d, 1d, 0.1 + 1e-11);
		assertEquals(-0.1, i1, 1e-10);

		final var i2 = firstIntervalContainingRoot(new Sin(), 1, 4, 1);
		assertEquals(3, i2, 0);
	}

	@Test
	void improveRootFailsWhenIntervalDoesNotContainRoot() {
		final var sin = new Sin();
		assertThrows(IllegalArgumentException.class, () -> improveRoot(sin, 1, 2, 1e-10));
	}

	@Test
	void improveRootWorksOnSin() {
		final var pi = improveRoot(new Sin(), 3.1, 3.2, 1e-10);
		assertEquals(PI, pi, 1e-10);

		final var mPi = improveRoot(new Sin(), -4, -3.1, 1e-10);
		assertEquals(-PI, mPi, 1e-10);
	}

	private static double nextAngle(final Random rng) {
		return rng.nextDouble() * 2d * PI;
	}
}

final class Sin implements DoubleUnaryOperator {
	@Override
	public double applyAsDouble(final double x) {
		return sin(x);
	}
}
