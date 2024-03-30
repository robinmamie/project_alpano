package ch.epfl.alpano;

import static ch.epfl.test.ObjectTest.hashCodeIsCompatibleWithEquals;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

final class Interval2DTest {

	private static final int RANDOM_ITERATIONS = 0;

	private static Interval2D newInterval2D(final int x1, final int x2, final int y1, final int y2) {
		return new Interval2D(new Interval1D(x1, x2), new Interval1D(y1, y2));
	}

	private static Interval2D i_0_10_0_10() {
		return newInterval2D(0, 10, 0, 10);
	}

	private static Interval2D i_0_9_0_11() {
		return newInterval2D(0, 9, 0, 11);
	}

	private static Interval2D i_0_10_11_20() {
		return newInterval2D(0, 10, 11, 20);
	}

	private static Interval2D i_0_10_0_20() {
		return newInterval2D(0, 10, 0, 20);
	}

	private static Interval2D i_11_20_0_10() {
		return newInterval2D(11, 20, 0, 10);
	}

	private static Interval2D i_0_20_0_10() {
		return newInterval2D(0, 20, 0, 10);
	}

	private static Interval2D i_2_2_2_2() {
		return newInterval2D(2, 2, 2, 2);
	}

	@Test
	void constructorFailsOnInvalidInterval() {
		assertThrows(NullPointerException.class, () -> new Interval2D(null, null));
	}

	@Test
	void containsWorksOnKnownIntervals() {
		final var i = i_2_2_2_2();
		for (var x = 1; x <= 3; ++x) {
			for (var y = 1; y <= 3; ++y) {
				assertEquals(x == 2 && y == 2, i.contains(x, y));
			}
		}
	}

	@Test
	void sizeWorksOnKnownIntervals() {
		assertEquals(1, i_2_2_2_2().size());
		assertEquals(21 * 11, i_0_20_0_10().size());
		assertEquals(10 * 11, i_11_20_0_10().size());
	}

	@Test
	void sizeOfIntersectionWorksOnNonIntersectingIntervals() {
		assertEquals(0, i_2_2_2_2().sizeOfIntersectionWith(i_11_20_0_10()));
		assertEquals(0, i_11_20_0_10().sizeOfIntersectionWith(i_2_2_2_2()));
	}

	@Test
	void sizeOfIntersectionWorksOnIntersectingIntervals() {
		assertEquals(1, i_2_2_2_2().sizeOfIntersectionWith(i_2_2_2_2()));
		assertEquals(21 * 11, i_0_20_0_10().sizeOfIntersectionWith(i_0_20_0_10()));
		assertEquals(1, i_2_2_2_2().sizeOfIntersectionWith(i_0_20_0_10()));
		assertEquals(1, i_0_20_0_10().sizeOfIntersectionWith(i_2_2_2_2()));
		assertEquals(10 * 11, i_0_10_0_10().sizeOfIntersectionWith(i_0_9_0_11()));
	}

	@Test
	void boudingUnionWorksOnKnownIntervals() {
		assertEquals(i_2_2_2_2(), i_2_2_2_2().boundingUnion(i_2_2_2_2()));

		final var i1 = i_0_10_0_10().boundingUnion(i_0_9_0_11());
		assertEquals(0, i1.iX().includedFrom());
		assertEquals(10, i1.iX().includedTo());
		assertEquals(0, i1.iY().includedFrom());
		assertEquals(11, i1.iY().includedTo());

		final var i2 = i_2_2_2_2().boundingUnion(i_11_20_0_10());
		assertEquals(2, i2.iX().includedFrom());
		assertEquals(20, i2.iX().includedTo());
		assertEquals(0, i2.iY().includedFrom());
		assertEquals(10, i2.iY().includedTo());
	}

	@Test
	void isUnionableWorksOnKnownUnionableIntervals() {
		assertTrue(i_0_10_0_10().isUnionableWith(i_0_10_0_10()));
		assertTrue(i_0_10_0_10().isUnionableWith(i_0_10_11_20()));
		assertTrue(i_0_10_11_20().isUnionableWith(i_0_10_0_10()));
		assertTrue(i_0_10_0_10().isUnionableWith(i_11_20_0_10()));
		assertTrue(i_11_20_0_10().isUnionableWith(i_0_10_0_10()));
		assertTrue(i_0_10_0_10().isUnionableWith(i_2_2_2_2()));
		assertTrue(i_2_2_2_2().isUnionableWith(i_0_10_0_10()));
	}

	@Test
	void isUnionableWorksOnKnownNonUnionableIntervals() {
		assertFalse(i_2_2_2_2().isUnionableWith(i_11_20_0_10()));
		assertFalse(i_11_20_0_10().isUnionableWith(i_2_2_2_2()));
		assertFalse(i_0_9_0_11().isUnionableWith(i_0_10_0_10()));
		assertFalse(i_0_10_0_10().isUnionableWith(i_0_9_0_11()));
	}

	@Test
	void isUnionableWithIsReflexive() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var interval = nextInterval(rng, 500, 1000);
			assertTrue(interval.isUnionableWith(interval));
		}
	}

	@Test
	void isUnionableWithIsSymmetric() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var i1 = nextInterval(rng, 5, 10);
			final var i2 = nextInterval(rng, 5, 10);
			assertTrue(!i1.isUnionableWith(i2) || i2.isUnionableWith(i1));
		}
	}

	@Test
	void unionFailsOnNonUnionableIntervals() {
		final var i1 = i_2_2_2_2();
		final var i2 = i_11_20_0_10();
		assertThrows(IllegalArgumentException.class, () -> i1.union(i2));
	}

	@Test
	void unionWorksOnASingleInterval() {
		assertEquals(i_0_10_0_10(), i_0_10_0_10().union(i_0_10_0_10().union(i_0_10_0_10())));
	}

	@Test
	void unionWorksOnKnownIntervals() {
		assertEquals(i_0_10_0_10(), i_0_10_0_10().union(i_2_2_2_2()));
		assertEquals(i_0_10_0_10(), i_2_2_2_2().union(i_0_10_0_10()));

		assertEquals(i_0_10_0_20(), i_0_10_0_10().union(i_0_10_11_20()));
		assertEquals(i_0_10_0_20(), i_0_10_11_20().union(i_0_10_0_10()));

		assertEquals(i_0_20_0_10(), i_0_10_0_10().union(i_11_20_0_10()));
		assertEquals(i_0_20_0_10(), i_11_20_0_10().union(i_0_10_0_10()));
	}

	@Test
	void unionIsCommutative() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var i1 = nextInterval(rng, 5, 10);
			final var i2 = nextInterval(rng, 5, 10);
			if (i1.isUnionableWith(i2)) {
				assertEquals(i1.union(i2), i2.union(i1));
			}
		}
	}

	@Test
	void equalsIsStructural() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var x1 = rng.nextInt(1000) - 500;
			final var x2 = x1 + rng.nextInt(1000);
			final var y1 = rng.nextInt(1000) - 500;
			final var y2 = y1 + rng.nextInt(1000);
			final var int1 = newInterval2D(x1, x2, y1, y2);
			final var int2 = newInterval2D(x1, x2, y1, y2);
			final var int3 = newInterval2D(x1, x2, y1, y2 + 1);
			assertEquals(int1, int2);
			assertEquals(int2, int1);
			assertNotEquals(int1, int3);
			assertNotEquals(int3, int1);
		}
	}

	@Test
	void hashCodeAndEqualsAreCompatible() {
		final var rng = newRandom();
		for (var i = 0; i < RANDOM_ITERATIONS; ++i) {
			final var a = rng.nextInt(1000) - 500;
			final var b = a + rng.nextInt(20);
			final var c = rng.nextInt(1000) - 500;
			final var d = c + rng.nextInt(20);
			final var i1 = newInterval2D(a, b, c, d);
			final var i1b = newInterval2D(a, b, c, d);
			final var i2 = newInterval2D(a, b, c, d + 1);
			assertTrue(hashCodeIsCompatibleWithEquals(i1, i1b));
			assertTrue(hashCodeIsCompatibleWithEquals(i1, i2));
		}
	}

	private Interval2D nextInterval(final Random rng, final int maxOffset, final int maxSize) {
		final var offsetX = rng.nextInt(maxOffset);
		final var offsetY = rng.nextInt(maxOffset);
		final var sizeX = rng.nextInt(maxSize);
		final var sizeY = rng.nextInt(maxSize);
		return newInterval2D(-offsetX, sizeX - offsetX, -offsetY, sizeY - offsetY);
	}

	@Test
	void constructorThrowsErrorIfIncorrectArgumentsGiven() {
		assertThrows(NullPointerException.class, () -> new Interval2D(null, null));
	}

	private void testContains(final int i, final int j) {
		final var a = new Interval1D(0, 2);
		final var b = new Interval1D(5, 8);
		final var c = new Interval2D(a, b);
		assertTrue(c.contains(i, j));
	}

	@Test
	void containsWorksOnMiddleValue() {
		testContains(1, 7);
	}

	@Test
	void containsWorksOnLowerBound() {
		testContains(0, 5);
	}

	@Test
	void containsWorksOnUpperBound() {
		testContains(2, 8);
	}

	@Test
	void containsFailsOnOtherValue() {
		final var a = new Interval1D(0, 2);
		final var b = new Interval1D(5, 8);
		final var c = new Interval2D(a, b);
		assertFalse(c.contains(10, 6));
	}

	@Test
	void sizeWorksOnRandomInterval() {
		final var a = new Interval1D(0, 2);
		final var b = new Interval1D(5, 8);
		final var c = new Interval2D(a, b);
		final var expectedSize = 12;
		final var actualSize = c.size();
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void sizeOfIntersectionWithWorksWithRandomIntervals() {
		final var a1 = new Interval1D(0, 2);
		final var b1 = new Interval1D(5, 8);
		final var c1 = new Interval2D(a1, b1);
		final var a2 = new Interval1D(2, 4);
		final var b2 = new Interval1D(3, 8);
		final var c2 = new Interval2D(a2, b2);
		final var expectedSize = 4;
		var actualSize = c1.sizeOfIntersectionWith(c2);
		assertEquals(expectedSize, actualSize);
		actualSize = c2.sizeOfIntersectionWith(c1);
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void sizeOfIntersectionWithWorksWithDisjointIntervals() {
		final var a1 = new Interval1D(0, 2);
		final var b1 = new Interval1D(5, 8);
		final var c1 = new Interval2D(a1, b1);
		final var a2 = new Interval1D(3, 4);
		final var b2 = new Interval1D(3, 8);
		final var c2 = new Interval2D(a2, b2);
		final var expectedSize = 0;
		var actualSize = c1.sizeOfIntersectionWith(c2);
		assertEquals(expectedSize, actualSize);
		actualSize = c2.sizeOfIntersectionWith(c1);
		assertEquals(expectedSize, actualSize);
	}

	@Test
	void unionWorksWithUnionableIntervals() {
		final var a1 = new Interval1D(0, 2);
		final var b1 = new Interval1D(5, 8);
		final var c1 = new Interval2D(a1, b1);
		final var a2 = new Interval1D(0, 4);
		final var b2 = new Interval1D(3, 8);
		final var c2 = new Interval2D(a2, b2);

		final var e1 = new Interval1D(0, 4);
		final var e2 = new Interval1D(3, 8);
		final var expectedInterval = new Interval2D(e1, e2);
		final var actualInterval = c1.union(c2);
		assertEquals(expectedInterval, actualInterval);
	}

	@Test
	void unionFailsWithNonUnionableIntervals() {
		final var a1 = new Interval1D(0, 2);
		final var b1 = new Interval1D(5, 8);
		final var c1 = new Interval2D(a1, b1);

		final var a2 = new Interval1D(4, 4);
		final var b2 = new Interval1D(3, 8);
		final var c2 = new Interval2D(a2, b2);

		assertThrows(IllegalArgumentException.class, () -> c1.union(c2));
	}

	@Test
	void toStringWorksWithRandomInterval() {
		final var a1 = new Interval1D(-1, 14);
		final var b1 = new Interval1D(0, 22);
		final var c1 = new Interval2D(a1, b1);

		final var expectedString = "[-1..14]×[0..22]";
		final var actualString = c1.toString();
		assertEquals(expectedString, actualString);
	}

	@Test
	void equalsGivesConsistentResults() {
		final var i1 = i_0_10_0_10();
		final var i2 = i_0_10_0_20();
		final var i3 = i_0_20_0_10();
		final var i1D = new Interval1D(0, 10);
		assertEquals(i1, i1);
		assertNotEquals(i1, i2);
		assertNotEquals(i2, i1);
		assertNotEquals(i1, i3);
		assertNotEquals(i3, i1);
		assertNotEquals(i1, i1D);
		assertEquals(i1.hashCode(), i1.hashCode());
		assertNotEquals(i1.hashCode(), i2.hashCode());
		assertNotEquals(i2.hashCode(), i1.hashCode());
		assertNotEquals(i1.hashCode(), i3.hashCode());
		assertNotEquals(i3.hashCode(), i1.hashCode());
		assertNotEquals(i1.hashCode(), i1D.hashCode());
	}
}
