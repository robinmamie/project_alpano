package ch.epfl.alpano;

import static ch.epfl.alpano.Azimuth.canonicalize;
import static ch.epfl.alpano.Azimuth.fromMath;
import static ch.epfl.alpano.Azimuth.isCanonical;
import static ch.epfl.alpano.Azimuth.toMath;
import static ch.epfl.alpano.Azimuth.toOctantString;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.PI;
import static java.lang.Math.floorMod;
import static java.lang.Math.nextDown;
import static java.lang.Math.round;
import static java.lang.Math.scalb;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;

final class AzimuthTest {

	@Test
	void isCanonicalIsTrueFor0() {
		assertTrue(isCanonical(0));
	}

	@Test
	void isCanonicalIsFalseFor0Pred() {
		assertFalse(isCanonical(nextDown(0)));
	}

	@Test
	void isCanonicalIsTrueFor2PiPred() {
		assertTrue(isCanonical(nextDown(scalb(PI, 1))));
	}

	@Test
	void isCanonicalIsFalseFor2Pi() {
		assertFalse(isCanonical(scalb(PI, 1)));
	}

	@Test
	void isCanonicalIsTrueForRandomCanonicalAzimuths() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			assertTrue(isCanonical(rng.nextDouble() * scalb(PI, 1)));
		}
	}

	@Test
	void canonicalizeCorrectlyCanonicalizesRoundedRandomAngles() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var aDeg = rng.nextInt(10_000) - 5_000;
			final var aRad = toRadians(aDeg);
			final var canonicalARad = canonicalize(aRad);
			assertTrue(0 <= canonicalARad && canonicalARad < scalb(PI, 1));
			var canonicalADeg = (int) round(toDegrees(canonicalARad));
			if (canonicalADeg == 360) {
				canonicalADeg = 0;
			}
			assertEquals(floorMod(aDeg, 360), canonicalADeg);
		}
	}

	@Test
	void toMathCorrectlyHandles0() {
		assertEquals(0d, toMath(0d), 0d);
	}

	@Test
	void fromMathCorrectlyHandles0() {
		assertEquals(0d, fromMath(0d), 0d);
	}

	@Test
	void toMathWorksForKnownValues() {
		final var vs = new int[] {
				0, 0,
				90, 270,
				180, 180,
				270, 90
		};
		for (var i = 0; i < vs.length; i += 2) {
			final var a = toMath(toRadians(vs[i]));
			assertEquals(toRadians(vs[i + 1]), a, 1e-10);
		}
	}

	@Test
	void fromMathWorksForKnownValues() {
		final var vs = new int[] {
				0, 0,
				90, 270,
				180, 180,
				270, 90
		};
		for (var i = 0; i < vs.length; i += 2) {
			final var a = fromMath(toRadians(vs[i]));
			assertEquals(toRadians(vs[i + 1]), a, 1e-10);
		}
	}

	@Test
	void toMathAndFromMathAreInverseForRandomValues() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var a = rng.nextDouble() * scalb(PI, 1);
			final var a2 = fromMath(toMath(a));
			assertEquals(a, a2, 1e-10);

			final var a3 = toMath(fromMath(a));
			assertEquals(a, a3, 1e-10);
		}
	}

	@Test
	void toMathThrowsFor2Pi() {
		final var scalb = scalb(PI, 1);
		assertThrows(IllegalArgumentException.class, () -> toMath(scalb));
	}

	@Test
	void fromMathThrowsFor2Pi() {
		final var scalb = scalb(PI, 1);
		assertThrows(IllegalArgumentException.class, () -> fromMath(scalb));
	}

	@Test
	void toOctantStringThrowsForNonCanonicalAzimuth() {
		assertThrows(IllegalArgumentException.class, () -> toOctantString(-1, null, null, null, null));
	}

	@Test
	void toOctantStringCorrectlyCyclesThroughValues() {
		final var n = "north";
		final var e = "east";
		final var s = "south";
		final var w = "west";
		final var expected = new ArrayList<>();
		expected.addAll(Collections.nCopies(45, n));
		expected.addAll(Collections.nCopies(45, n + e));
		expected.addAll(Collections.nCopies(45, e));
		expected.addAll(Collections.nCopies(45, s + e));
		expected.addAll(Collections.nCopies(45, s));
		expected.addAll(Collections.nCopies(45, s + w));
		expected.addAll(Collections.nCopies(45, w));
		expected.addAll(Collections.nCopies(45, n + w));

		for (var aDeg = 0; aDeg < 360; ++aDeg) {
			final var aRad = toRadians(floorMod(aDeg - 22, 360));
			final var os = toOctantString(aRad, n, e, s, w);
			assertEquals(expected.get(aDeg), os);
		}
	}
}
