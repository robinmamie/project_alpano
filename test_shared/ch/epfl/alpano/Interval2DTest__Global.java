package ch.epfl.alpano;

import static ch.epfl.test.ObjectTest.hashCodeIsCompatibleWithEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.epfl.test.ObjectTest;

public class Interval2DTest__Global {
	Interval2D i1 = new Interval2D(new Interval1D(1, 5), new Interval1D(5, 7));
	Interval2D i2 = new Interval2D(new Interval1D(0, 0), new Interval1D(1, 3));
	Interval2D i3 = new Interval2D(new Interval1D(3, 7), new Interval1D(1, 1));
	Interval2D i4 = new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1));

	Interval2D i5 = new Interval2D(new Interval1D(2, 7), new Interval1D(6, 7));
	Interval2D i6 = new Interval2D(new Interval1D(3, 3), new Interval1D(3, 3));

	Interval2D i7 = new Interval2D(new Interval1D(0, 3), new Interval1D(1, 3));
	Interval2D i8 = new Interval2D(new Interval1D(0, 3), new Interval1D(1, 4));

	Interval2D i9 = new Interval2D(new Interval1D(0, 4), new Interval1D(1, 3));
	Interval2D i10 = new Interval2D(new Interval1D(0, 3), new Interval1D(1, 4));

	Interval2D[] i2dArray = {
			// case1
			new Interval2D(new Interval1D(4, 4), new Interval1D(4, 4)),
			new Interval2D(new Interval1D(4, 4), new Interval1D(4, 4)),
			// case2
			new Interval2D(new Interval1D(0, 0), new Interval1D(0, 0)),
			new Interval2D(new Interval1D(4, 4), new Interval1D(4, 4)),
			// case3
			new Interval2D(new Interval1D(0, 10), new Interval1D(0, 6)),
			new Interval2D(new Interval1D(4, 8), new Interval1D(4, 8)),
			// case4
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(2, 6), new Interval1D(-3, 1)),
			// case5
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(0, 2), new Interval1D(0, 2)),
			// case6
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(4, 8), new Interval1D(4, 8)),
			// case7
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(0, 4), new Interval1D(2, 8)),
			// case8
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(4, 10), new Interval1D(0, 4)),
			// case9
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(4, 8), new Interval1D(0, 4)),
			// case10
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(3, 7), new Interval1D(-1, 4)),
			// case11
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(4, 8), new Interval1D(1, 3)),
			// case12
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(7, 10), new Interval1D(0, 4)),
			// case12.2 same as 12 but in the y axis
			new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4)),
			new Interval2D(new Interval1D(0, 4), new Interval1D(7, 10)) };

	boolean[] isUnionableAwnsers = { true, false, false, false, true, false, true, true, true, false, false, false,
			false };

	@Test(expected = NullPointerException.class)
	public void constructorStopsWorkingWhenWithNullPointerExcpetion() {
		new Interval2D(null, new Interval1D(5, 10));
		new Interval2D(new Interval1D(1, 3), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorFailsWithIllegalArgument() {
		new Interval1D(5, 4);
	}

	@Test
	public void iXReturnsTheRightValue() {
		assertEquals(new Interval1D(1, 5), i1.iX());
		assertEquals(new Interval1D(0, 0), i2.iX());
		assertEquals(new Interval1D(3, 7), i3.iX());
	}

	@Test
	public void iYReturnsTheRightValue() {
		assertEquals(new Interval1D(5, 7), i1.iY());
		assertEquals(new Interval1D(1, 3), i2.iY());
		assertEquals(new Interval1D(1, 1), i3.iY());
		assertEquals(new Interval1D(1, 1), i4.iY());
	}

	@Test
	public void containsTest() {
		assertTrue(i1.contains(3, 6));
		assertFalse(i2.contains(1, 2));
		assertTrue(i2.contains(0, 2));
		assertFalse(i2.contains(0, 4));
		assertTrue(i4.contains(1, 1));
	}

	@Test
	public void sizeTest() {
		assertEquals(3, i2.size());
		assertEquals(15, i1.size());
		assertEquals(1, i4.size());
	}

	@Test
	public void sizeOfIntersectionWithTest() {
		assertEquals(0, i1.sizeOfIntersectionWith(i4));
		assertEquals(0, i1.sizeOfIntersectionWith(i2));
		assertEquals(8, i1.sizeOfIntersectionWith(i5));
		assertEquals(i1.sizeOfIntersectionWith(i4), i4.sizeOfIntersectionWith(i1));
		assertEquals(8, i5.sizeOfIntersectionWith(i1));

	}

	@Test
	public void boundingUnionTest() {
		assertEquals(new Interval2D(new Interval1D(1, 3), new Interval1D(1, 3)), i4.boundingUnion(i6));
		assertEquals(new Interval2D(new Interval1D(1, 7), new Interval1D(5, 7)), i1.boundingUnion(i5));
		assertEquals(i4, i4.boundingUnion(i4));
	}

	@Test
	public void isUnionableWithTestBaclé() {
		Interval2D i11 = new Interval2D(new Interval1D(0, 4), new Interval1D(0, 4));
		Interval2D i12 = new Interval2D(new Interval1D(0, 3), new Interval1D(0, 3));

		assertFalse(i4.isUnionableWith(i6));
		assertTrue(i7.isUnionableWith(i8));
		assertFalse(i9.isUnionableWith(i10));
		assertTrue(i11.isUnionableWith(i12));
		assertTrue(i12.isUnionableWith(i11));
	}

	@Test
	public void isUnionableWithTestComplete() {
		for (int i = 0; i < i2dArray.length; i += 2) {
			assertEquals(isUnionableAwnsers[i / 2], i2dArray[i].isUnionableWith(i2dArray[i + 1]));
			assertEquals(isUnionableAwnsers[i / 2], i2dArray[i + 1].isUnionableWith(i2dArray[i]));
		}
	}

	@Test
	public void hashCodeIsCompatibleWithEquals1() {
		ObjectTest.hashCodeIsCompatibleWithEquals(i2dArray[0], i2dArray[1]);
	}

	@Test
	public void unionTestCase1() {
		assertEquals(i2dArray[0].union(i2dArray[1]), i2dArray[0]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unionTestCase2() {
		i2dArray[2].union(i2dArray[3]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unionTestCase3() {
		i2dArray[4].union(i2dArray[5]);

	}

	@Test(expected = IllegalArgumentException.class)
	public void unionTestCase4() {
		i2dArray[6].union(i2dArray[7]);
	}

	@Test
	public void unionTestCase5() {
		assertEquals(i2dArray[8], i2dArray[8].union(i2dArray[9]));
	}

	@Test(expected = IllegalArgumentException.class)
	public void unionTestCase6() {
		i2dArray[10].union(i2dArray[11]);
	}

	@Test
	public void unionTestCase7() {
		assertEquals(new Interval2D(new Interval1D(0, 4), new Interval1D(0, 8)), i2dArray[12].union(i2dArray[13]));
	}

	@Test
	public void unionTestCase8() {
		assertEquals(new Interval2D(new Interval1D(0, 10), new Interval1D(0, 4)), i2dArray[14].union(i2dArray[15]));
		assertEquals(new Interval2D(new Interval1D(0, 10), new Interval1D(0, 4)), i2dArray[15].union(i2dArray[14]));
	}

	@Test
	public void unionTestCase9() {
		assertEquals(new Interval2D(new Interval1D(0, 8), new Interval1D(0, 4)), i2dArray[16].union(i2dArray[17]));
	}

	@Test(expected = IllegalArgumentException.class)
	public void unionTestCase10() {
		i2dArray[18].union(i2dArray[19]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unionTestCase11() {
		i2dArray[20].union(i2dArray[21]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unionTestCase12() {
		i2dArray[22].union(i2dArray[23]);

	}

	@Test
	public void testConstructeur() {
		Interval1D i1 = new Interval1D(0, 3);
		Interval1D i2 = new Interval1D(2, 4);
		new Interval2D(i1, i2);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructeurException() {
		Interval1D i1 = null;
		Interval1D i2 = new Interval1D(0, 4);
		new Interval2D(i1, i2);
	}

	@Test
	public void testConstructeur2() {
		Interval1D i1 = new Interval1D(0, 0);
		Interval1D i2 = new Interval1D(2, 4);
		new Interval2D(i1, i2);
	}

	@Test
	public void testConstructeur3() {
		Interval1D i1 = new Interval1D(0, 3);
		Interval1D i2 = new Interval1D(2, 4);
		new Interval2D(i2, i1);
	}

	@Test
	public void testConstructeur4() {
		Interval1D i1 = new Interval1D(0, 3);
		new Interval2D(i1, i1);
	}

	@Test
	public void testIX1() {
		Interval1D i1 = new Interval1D(0, 3);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);
		Interval1D i4 = new Interval1D(5, 10);
		Interval1D i5 = new Interval1D(8, 14);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i4, i3);
		Interval2D I3 = new Interval2D(i1, i5);

		assertEquals(i1, I1.iX());
		assertEquals(i4, I2.iX());
		assertEquals(i1, I3.iX());
	}

	@Test
	public void testIY1() {
		Interval1D i1 = new Interval1D(0, 3);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i3, i2);
		Interval2D I3 = new Interval2D(i3, i1);

		assertEquals(i2, I1.iY());
		assertEquals(i2, I2.iY());
		assertEquals(i1, I3.iY());
	}

	@Test
	public void testContains1() {
		Interval1D i1 = new Interval1D(0, 3);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i3, i2);
		Interval2D I3 = new Interval2D(i3, i1);

		assertTrue(I1.contains(0, 7));
		assertFalse(I1.contains(4, 5));
		assertTrue(I2.contains(10, 5));
		assertFalse(I2.contains(1, 10));
		assertTrue(I3.contains(10, 2));
		assertFalse(I3.contains(1, 2));

	}

	@Test
	public void testSize1() {
		Interval1D i1 = new Interval1D(0, 0);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i3, i2);
		Interval2D I3 = new Interval2D(i3, i1);

		assertEquals(4, I1.size());
		assertEquals(44, I2.size());
		assertEquals(11, I3.size());
	}

	@Test
	public void testSizeOfIntersectionWith() {
		Interval1D i1 = new Interval1D(0, 0);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);
		Interval1D i4 = new Interval1D(5, 8);

		Interval1D i5 = new Interval1D(0, 5);
		Interval1D i6 = new Interval1D(2, 4);
		Interval1D i7 = new Interval1D(0, 5);
		Interval1D i8 = new Interval1D(2, 5);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i3, i2);
		Interval2D I3 = new Interval2D(i3, i1);
		Interval2D I4 = new Interval2D(i3, i4);

		Interval2D I5 = new Interval2D(i5, i6);
		Interval2D I6 = new Interval2D(i7, i8);

		assertEquals(18, I5.sizeOfIntersectionWith(I6));

		assertEquals(0, I1.sizeOfIntersectionWith(I2));
		assertEquals(0, I2.sizeOfIntersectionWith(I3));
		assertEquals(33, I2.sizeOfIntersectionWith(I4));
	}

	@Test
	public void testBoundingUnion1() {
		Interval1D i1 = new Interval1D(0, 0);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);
		Interval1D i4 = new Interval1D(5, 8);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i3, i2);
		new Interval2D(i3, i1);
		Interval2D I4 = new Interval2D(i3, i4);

		assertEquals(new Interval2D(new Interval1D(2, 12), new Interval1D(4, 8)), I2.boundingUnion(I4));
		assertEquals(new Interval2D(new Interval1D(0, 12), new Interval1D(4, 7)), I1.boundingUnion(I2));
	}

	@Test
	public void testIsUnionableWith() {
		Interval1D i1 = new Interval1D(0, 0);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);
		Interval1D i4 = new Interval1D(5, 8);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i3, i2);
		Interval2D I3 = new Interval2D(i3, i1);
		Interval2D I4 = new Interval2D(i3, i4);

		assertFalse(I1.isUnionableWith(I2));
		assertFalse(I2.isUnionableWith(I3));
		assertTrue(I2.isUnionableWith(I4));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnionException() {
		Interval1D i1 = new Interval1D(0, 0);
		Interval1D i2 = new Interval1D(4, 7);
		Interval1D i3 = new Interval1D(2, 12);
		new Interval1D(5, 8);

		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i3, i2);
		Interval2D I3 = new Interval2D(i3, i1);

		I1.union(I2);
		I2.union(I3);
	}

	@Test
	public void testUnion1() {
		Interval1D i1 = new Interval1D(0, 5);
		Interval1D i2 = new Interval1D(2, 4);
		Interval2D I1 = new Interval2D(i1, i2);

		Interval1D i3 = new Interval1D(0, 5);
		Interval1D i4 = new Interval1D(2, 5);
		Interval2D I2 = new Interval2D(i3, i4);

		assertEquals(new Interval2D(new Interval1D(0, 5), new Interval1D(2, 5)), I1.union(I2));
	}

	@Test
	public void testUnion2() {
		Interval1D i1 = new Interval1D(2, 5);
		Interval1D i2 = new Interval1D(2, 4);
		Interval2D I1 = new Interval2D(i1, i2);

		Interval1D i3 = new Interval1D(5, 10);
		Interval1D i4 = new Interval1D(2, 4);
		Interval2D I2 = new Interval2D(i3, i4);

		assertEquals(new Interval2D(new Interval1D(2, 10), new Interval1D(2, 4)), I1.union(I2));
	}

	@Test
	public void testUnion3() {
		Interval1D i1 = new Interval1D(5, 10);
		Interval1D i2 = new Interval1D(2, 4);
		Interval2D I1 = new Interval2D(i1, i2);

		Interval1D i3 = new Interval1D(5, 10);
		Interval1D i4 = new Interval1D(2, 4);
		Interval2D I2 = new Interval2D(i3, i4);

		assertEquals(new Interval2D(new Interval1D(5, 10), new Interval1D(2, 4)), I1.union(I2));
	}

	@Test
	public void testEqualsOK() {
		Interval1D i1 = new Interval1D(0, 5);
		Interval1D i2 = new Interval1D(2, 4);
		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i1, i2);
		assertTrue(I1.equals(I2));
	}

	@Test
	public void testEqualsWrong() {
		Interval1D i1 = new Interval1D(0, 5);
		Interval1D i2 = new Interval1D(2, 4);
		Interval2D I1 = new Interval2D(i1, i2);
		Interval2D I2 = new Interval2D(i1, new Interval1D(3, 5));
		assertFalse(I1.equals(I2));
	}

	@Test
	public void testHashCode() {
		Interval1D i = new Interval1D(0, 3);
		Interval1D i2 = new Interval1D(5, 8);
		Interval2D I1 = new Interval2D(i, i2);
		Interval2D I2 = new Interval2D(i, new Interval1D(4, 7));
		assertTrue(hashCodeIsCompatibleWithEquals(I1, I2));
	}

	@Test
	public void testToString1() {
		Interval1D i1 = new Interval1D(0, 3);
		Interval1D i2 = new Interval1D(5, 8);
		Interval2D I1 = new Interval2D(i1, i2);
		System.out.println(I1.toString());
		assertTrue(I1.toString().equals("[0..3]x[5..8]")
		        || I1.toString().equals("[0..3]×[5..8]"));
	}

	@Test
	public void canConstructObject() {
		new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1));
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void throwsExceptionWhenConstructedWithNullIX() {
		new Interval2D(null, new Interval1D(1, 1));
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void throwsExceptionWhenConstructedWithNullIY() {
		new Interval2D(new Interval1D(1, 1), null);
	}

	@Test
	public void testIX() {
		assertEquals(new Interval1D(1, 1), new Interval2D(new Interval1D(1, 1), new Interval1D(1, 2)).iX());
	}

	@Test
	public void testIY() {
		assertEquals(new Interval1D(1, 2), new Interval2D(new Interval1D(1, 1), new Interval1D(1, 2)).iY());
	}

	@Test
	public void testContains() {
		assertEquals(true, new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1)).contains(1, 1));
	}

	@Test
	public void testContains2() {
		assertEquals(true, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).contains(1, 1));
	}

	@Test
	public void testContains3() {
		assertEquals(true, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).contains(1, 2));
	}

	@Test
	public void testContains4() {
		assertEquals(true, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).contains(2, 1));
	}

	@Test
	public void testContains5() {
		assertEquals(true, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).contains(2, 2));
	}

	@Test
	public void testContainsWithUncontainedValues() {
		assertEquals(false, new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1)).contains(1, 2));
	}

	@Test
	public void testContainsWithUncontainedValues2() {
		assertEquals(false, new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1)).contains(2, 1));
	}

	@Test
	public void testSize() {
		assertEquals(1, new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1)).size());
	}

	@Test
	public void testSize2() {
		assertEquals(4, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).size());
	}

	@Test
	public void testSize3() {
		assertEquals(8, new Interval2D(new Interval1D(-1, 2), new Interval1D(1, 2)).size());
	}

	@Test
	public void testSize4() {
		assertEquals(4, new Interval2D(new Interval1D(-2, -1), new Interval1D(-2, -1)).size());
	}

	@Test
	public void testSizeOfIntersectWith() {
		assertEquals(1, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.sizeOfIntersectionWith(new Interval2D(new Interval1D(0, 1), new Interval1D(0, 1))));
	}

	@Test
	public void testSizeOfIntersectWith2() {
		assertEquals(4, new Interval2D(new Interval1D(1, 3), new Interval1D(1, 3))
				.sizeOfIntersectionWith(new Interval2D(new Interval1D(2, 4), new Interval1D(2, 4))));
	}

	@Test
	public void testSizeOfIntersectWith3() {
		assertEquals(18, new Interval2D(new Interval1D(0, 6), new Interval1D(0, 9))
				.sizeOfIntersectionWith(new Interval2D(new Interval1D(4, 8), new Interval1D(-2, 5))));
	}

	@Test
	public void testSizeOfIntersectWith4() {
		assertEquals(18, new Interval2D(new Interval1D(-6, 0), new Interval1D(-9, 0))
				.sizeOfIntersectionWith(new Interval2D(new Interval1D(-8, -4), new Interval1D(-5, 2))));
	}

	@Test
	public void testSizeOfIntersectWithDisjointIntervals() {
		assertEquals(0, new Interval2D(new Interval1D(0, 1), new Interval1D(0, 1))
				.sizeOfIntersectionWith(new Interval2D(new Interval1D(2, 3), new Interval1D(2, 3))));
	}

	@Test
	public void testBoundingUnion() {
		assertEquals(new Interval2D(new Interval1D(1, 3), new Interval1D(1, 3)),
				new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
						.boundingUnion(new Interval2D(new Interval1D(2, 3), new Interval1D(2, 3))));
	}

	@Test
	public void testBoundingUnionWithDisjointIntervals() {
		assertEquals(new Interval2D(new Interval1D(1, 4), new Interval1D(1, 4)),
				new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
						.boundingUnion(new Interval2D(new Interval1D(3, 4), new Interval1D(3, 4))));
	}

	@Test
	public void testBoundingUnionWithNegativeValues() {
		assertEquals(new Interval2D(new Interval1D(-3, -1), new Interval1D(-3, -1)),
				new Interval2D(new Interval1D(-2, -1), new Interval1D(-2, -1))
						.boundingUnion(new Interval2D(new Interval1D(-3, -2), new Interval1D(-3, -2))));
	}

	@Test
	public void testBoundingUnionWithDisjointIntervalsWithNegativeValues() {
		assertEquals(new Interval2D(new Interval1D(-4, -1), new Interval1D(-4, -1)),
				new Interval2D(new Interval1D(-2, -1), new Interval1D(-2, -1))
						.boundingUnion(new Interval2D(new Interval1D(-4, -3), new Interval1D(-4, -3))));
	}

	@Test
	public void testIsUnionable() {
		assertEquals(false, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.isUnionableWith(new Interval2D(new Interval1D(2, 4), new Interval1D(2, 4))));
	}

	@Test
	public void testIsUnionable2() {
		assertEquals(true, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.isUnionableWith(new Interval2D(new Interval1D(1, 2), new Interval1D(2, 4))));
	}

	@Test
	public void testIsUnionable3() {
		assertEquals(true, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.isUnionableWith(new Interval2D(new Interval1D(-3, 2), new Interval1D(1, 2))));
	}

	@Test
	public void testIsUnionable4() {
		assertEquals(true, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.isUnionableWith(new Interval2D(new Interval1D(0, 3), new Interval1D(0, 3))));
	}

	@Test
	public void testIsUnionableWithDisjointIntervals() {
		assertEquals(false, new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.isUnionableWith(new Interval2D(new Interval1D(3, 4), new Interval1D(3, 4))));
	}

	@Test
	public void testIsUnionableWithIntervalsThatDontAddUpToARectangle() {
		assertEquals(false, new Interval2D(new Interval1D(1, 3), new Interval1D(1, 3))
				.isUnionableWith(new Interval2D(new Interval1D(2, 4), new Interval1D(2, 4))));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnion() {
		new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.union(new Interval2D(new Interval1D(2, 4), new Interval1D(2, 4)));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testUnionWithDisjunctIntervals() {
		new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.union(new Interval2D(new Interval1D(3, 4), new Interval1D(3, 4)));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testUnionWithDisjunctIntervals2() {
		new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
				.union(new Interval2D(new Interval1D(1, 4), new Interval1D(3, 4)));
	}

	@Test
	public void testToString() {
		assertEquals("[1..2]×[1..2]", new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).toString());
	}

}
