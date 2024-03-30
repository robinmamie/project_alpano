package ch.epfl.alpano;

import static ch.epfl.test.ObjectTest.hashCodeIsCompatibleWithEquals;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class Interval1DTest {

	private static Interval1D i_0_9() {
		return new Interval1D(0, 9);
	}

	private static Interval1D i_0_2() {
		return new Interval1D(0, 2);
	}

	private static Interval1D i_3_5() {
		return new Interval1D(3, 5);
	}

	private static Interval1D i_4_6() {
		return new Interval1D(4, 6);
	}

	private static Interval1D i_6_9() {
		return new Interval1D(6, 9);
	}

	@Test
	void constructorFailsForInvalidBounds() {
		assertThrows(IllegalArgumentException.class, () -> new Interval1D(1, 0));
	}

	@Test
	void constructorWorksForSingletonInterval() {
		final var o = new Interval1D(10, 10);
		assertNotNull(o);
	}

	@Test
	void containsIsTrueOnlyForTheIntervalsElements() {
		final var sqrtIt = (int) ceil(sqrt(RANDOM_ITERATIONS));
		final var rng = newRandom();
		for (var i = 0; i < sqrtIt; ++i) {
			final var a = rng.nextInt(200) - 100;
			final var b = a + rng.nextInt(50);
			final var interval = new Interval1D(a, b);
			for (var j = 0; j < sqrtIt; ++j) {
				final var v = rng.nextInt(200) - 100;
				assertEquals(a <= v && v <= b, interval.contains(v));
			}
		}
	}

	@Test
	void containsWorksAtTheLimit() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var max = rng.nextInt(2000);
			final var a = rng.nextInt(max) - 1000;
			final var b = max - 1000;
			final var interval = new Interval1D(a, b);
			assertFalse(interval.contains(a - 1));
			assertTrue(interval.contains(a));
			assertTrue(interval.contains(b));
			assertFalse(interval.contains(b + 1));
		}
	}

	@Test
	void sizeWorksOnKnownIntervals() {
		assertEquals(10, i_0_9().size());
		assertEquals(3, i_0_2().size());
		assertEquals(3, i_3_5().size());
		assertEquals(3, i_4_6().size());
		assertEquals(4, i_6_9().size());
	}

	@Test
	void sizeOfIntersectionWorksOnNonIntersectingIntervals() {
		assertEquals(0, i_0_2().sizeOfIntersectionWith(i_3_5()));
		assertEquals(0, i_0_2().sizeOfIntersectionWith(i_4_6()));
		assertEquals(0, i_0_2().sizeOfIntersectionWith(i_6_9()));
	}

	@Test
	void sizeOfIntersectionWorksOnIntersectingIntervals() {
		assertEquals(3, i_0_2().sizeOfIntersectionWith(i_0_9()));
		assertEquals(3, i_0_9().sizeOfIntersectionWith(i_0_2()));
		assertEquals(1, i_4_6().sizeOfIntersectionWith(i_6_9()));
	}

	@Test
	void boundingUnionWorksOnKnownIntervals() {
		assertEquals(0, i_0_2().boundingUnion(i_6_9()).includedFrom());
		assertEquals(9, i_0_2().boundingUnion(i_6_9()).includedTo());
		assertEquals(0, i_6_9().boundingUnion(i_0_2()).includedFrom());
		assertEquals(9, i_6_9().boundingUnion(i_0_2()).includedTo());
		assertEquals(0, i_0_9().boundingUnion(i_0_9()).includedFrom());
		assertEquals(9, i_0_9().boundingUnion(i_0_9()).includedTo());
	}

	@Test
	void isUnionableWithWorksOnKnownIntervals() {
		// Intersecting intervals
		assertTrue(i_0_9().isUnionableWith(i_0_9()));
		assertTrue(i_0_9().isUnionableWith(i_3_5()));
		assertTrue(i_3_5().isUnionableWith(i_3_5()));
		assertTrue(i_3_5().isUnionableWith(i_0_9()));
		assertTrue(i_3_5().isUnionableWith(i_4_6()));
		assertTrue(i_4_6().isUnionableWith(i_4_6()));
		assertTrue(i_4_6().isUnionableWith(i_3_5()));

		// Contiguous intervals
		assertTrue(i_3_5().isUnionableWith(i_6_9()));
		assertTrue(i_6_9().isUnionableWith(i_3_5()));
	}

	@Test
	void unionFailsOnNonUnionableIntervals() {
		final var i1 = i_0_2();
		final var i2 = i_6_9();
		assertThrows(IllegalArgumentException.class, () -> i1.union(i2));
	}

	@Test
	void unionWorksWhenOneIntervalContainsTheOther() {
		assertEquals(i_0_9(), i_0_9().union(i_3_5()));
		assertEquals(i_0_9(), i_3_5().union(i_0_9()));
	}

	@Test
	void unionWorksWithASingleInterval() {
		assertEquals(i_3_5(), i_3_5().union(i_3_5()).union(i_3_5()));
	}

	@Test
	void unionWorksWhenOneIntervalIsContiguousWithTheOther() {
		final var i = i_0_2().union(i_6_9().union(i_3_5()));
		assertEquals(i_0_9(), i);
	}

	@Test
	void equalsIsStructural() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var a = rng.nextInt();
			final var b = rng.nextInt();
			final var int1 = new Interval1D(min(a, b), max(a, b));
			final var int2 = new Interval1D(min(a, b), max(a, b));
			final var int3 = new Interval1D(min(a, b) + 1, max(a, b) + 1);
			assertEquals(int1, int2);
			assertNotEquals(int1, int3);
		}
	}

	@Test
	void hashCodeAndEqualsAreCompatible() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var a = rng.nextInt();
			final var b = rng.nextInt();
			final var c = rng.nextInt();
			final var d = rng.nextInt();
			final var int1 = new Interval1D(min(a, b), max(a, b));
			final var int2 = new Interval1D(min(c, d), max(c, d));
			final var int3 = new Interval1D(min(c, d), max(c, d));
			assertTrue(hashCodeIsCompatibleWithEquals(int1, int2));
			assertTrue(hashCodeIsCompatibleWithEquals(int2, int3));
		}
	}

	@Test
	void constructorThrowsErrorIfIncorrectArgumentsGiven() {
		assertThrows(IllegalArgumentException.class, () -> new Interval1D(1, 0));
	}

	private void testContains(final int value) {
		final var test = new Interval1D(0, 2);
		assertTrue(test.contains(value));
	}

	@Test
	void containsWorksOnMiddleValue() {
		testContains(1);
	}

	@Test
	void containsWorksOnLowerBound() {
		testContains(0);
	}

	@Test
	void containsWorksOnUpperBound() {
		testContains(2);
	}

	@Test
	void containsFailsOnOtherValue() {
		final var test = new Interval1D(0, 2);
		assertFalse(test.contains(3));
	}

	@Test
	void sizeWorksOnRandomInterval() {
		final var test = new Interval1D(0, 2);
		final var expectedSize = 3;
		final var actualSize = test.size();
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void sizeWorksOnSmallestInterval() {
		final var test = new Interval1D(0, 0);
		final var expectedSize = 1;
		final var actualSize = test.size();
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void sizeOfIntersectionWithWorksWithRandomIntervals() {
		final var a = new Interval1D(0, 2);
		final var b = new Interval1D(1, 3);
		final var expectedSize = 2;
		var actualSize = a.sizeOfIntersectionWith(b);
		assertEquals(expectedSize, actualSize);
		actualSize = b.sizeOfIntersectionWith(a);
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void sizeOfIntersectionWithWorksWithDisjointIntervals() {
		final var a = new Interval1D(0, 2);
		final var b = new Interval1D(3, 5);
		final var expectedSize = 0;
		var actualSize = a.sizeOfIntersectionWith(b);
		assertEquals(expectedSize, actualSize);
		actualSize = b.sizeOfIntersectionWith(a);
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void sizeOfIntersectionWithWorksWithInnerIntervals() {
		final var a = new Interval1D(0, 5);
		final var b = new Interval1D(2, 4);
		final var expectedSize = 3;
		var actualSize = a.sizeOfIntersectionWith(b);
		assertEquals(expectedSize, actualSize);
		actualSize = b.sizeOfIntersectionWith(a);
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void unionWorksWithUnionableIntervals() {
		final var a = new Interval1D(0, 5);
		final var b = new Interval1D(2, 6);
		final var expectedInterval = new Interval1D(0, 6);
		final var actualInterval = a.union(b);
		assertEquals(expectedInterval, actualInterval);
	}

	@Test
	void unionFailsWithNonUnionableIntervals() {
		final var a = new Interval1D(0, 5);
		final var b = new Interval1D(7, 7);

		assertThrows(IllegalArgumentException.class, () -> a.union(b));
	}

	@Test
	void unionWorksWithUnionableInnerIntervals() {
		final var a = new Interval1D(0, 5);
		final var b = new Interval1D(2, 3);
		final var expectedInterval = a;
		final var actualInterval = a.union(b);
		assertEquals(expectedInterval, actualInterval);
	}

	@Test
	void toStringWorksWithRandomInterval() {
		final var test = new Interval1D(4, 12);
		final var expectedString = "[4..12]";
		final var actualString = test.toString();
		assertEquals(expectedString, actualString);
	}

}
