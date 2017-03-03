package ch.epfl.alpano.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

/**
 * Class that test if Interval2D behave as expected.
 *
 * @author Charline Montial
 * @author Yves Zumbach
 */

public class Interval2DTest {

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
        assertEquals(new Interval1D(1, 1),
                new Interval2D(new Interval1D(1, 1), new Interval1D(1, 2))
                        .iX());
    }

    @Test
    public void testIY() {
        assertEquals(new Interval1D(1, 2),
                new Interval2D(new Interval1D(1, 1), new Interval1D(1, 2))
                        .iY());
    }

    @Test
    public void testContains() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1))
                        .contains(1, 1));
    }

    @Test
    public void testContains2() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .contains(1, 1));
    }

    @Test
    public void testContains3() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .contains(1, 2));
    }

    @Test
    public void testContains4() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .contains(2, 1));
    }

    @Test
    public void testContains5() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .contains(2, 2));
    }

    @Test
    public void testContainsWithUncontainedValues() {
        assertEquals(false,
                new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1))
                        .contains(1, 2));
    }

    @Test
    public void testContainsWithUncontainedValues2() {
        assertEquals(false,
                new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1))
                        .contains(2, 1));
    }

    @Test
    public void testSize() {
        assertEquals(1,
                new Interval2D(new Interval1D(1, 1), new Interval1D(1, 1))
                        .size());
    }

    @Test
    public void testSize2() {
        assertEquals(4,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .size());
    }

    @Test
    public void testSize3() {
        assertEquals(8,
                new Interval2D(new Interval1D(-1, 2), new Interval1D(1, 2))
                        .size());
    }

    @Test
    public void testSize4() {
        assertEquals(4,
                new Interval2D(new Interval1D(-2, -1), new Interval1D(-2, -1))
                        .size());
    }

    @Test
    public void testSizeOfIntersectWith() {
        assertEquals(1,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .sizeOfIntersectionWith(new Interval2D(
                                new Interval1D(0, 1), new Interval1D(0, 1))));
    }

    @Test
    public void testSizeOfIntersectWith2() {
        assertEquals(4,
                new Interval2D(new Interval1D(1, 3), new Interval1D(1, 3))
                        .sizeOfIntersectionWith(new Interval2D(
                                new Interval1D(2, 4), new Interval1D(2, 4))));
    }

    @Test
    public void testSizeOfIntersectWith3() {
        assertEquals(18,
                new Interval2D(new Interval1D(0, 6), new Interval1D(0, 9))
                        .sizeOfIntersectionWith(new Interval2D(
                                new Interval1D(4, 8), new Interval1D(-2, 5))));
    }

    @Test
    public void testSizeOfIntersectWith4() {
        assertEquals(18,
                new Interval2D(new Interval1D(-6, 0), new Interval1D(-9, 0))
                        .sizeOfIntersectionWith(
                                new Interval2D(new Interval1D(-8, -4),
                                        new Interval1D(-5, 2))));
    }

    @Test
    public void testSizeOfIntersectWithDisjointIntervals() {
        assertEquals(0,
                new Interval2D(new Interval1D(0, 1), new Interval1D(0, 1))
                        .sizeOfIntersectionWith(new Interval2D(
                                new Interval1D(2, 3), new Interval1D(2, 3))));
    }

    @Test
    public void testBoundingUnion() {
        assertEquals(new Interval2D(new Interval1D(1, 3), new Interval1D(1, 3)),
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .boundingUnion(new Interval2D(new Interval1D(2, 3),
                                new Interval1D(2, 3))));
    }

    @Test
    public void testBoundingUnionWithDisjointIntervals() {
        assertEquals(new Interval2D(new Interval1D(1, 4), new Interval1D(1, 4)),
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .boundingUnion(new Interval2D(new Interval1D(3, 4),
                                new Interval1D(3, 4))));
    }

    @Test
    public void testBoundingUnionWithNegativeValues() {
        assertEquals(
                new Interval2D(new Interval1D(-3, -1), new Interval1D(-3, -1)),
                new Interval2D(new Interval1D(-2, -1), new Interval1D(-2, -1))
                        .boundingUnion(new Interval2D(new Interval1D(-3, -2),
                                new Interval1D(-3, -2))));
    }

    @Test
    public void testBoundingUnionWithDisjointIntervalsWithNegativeValues() {
        assertEquals(
                new Interval2D(new Interval1D(-4, -1), new Interval1D(-4, -1)),
                new Interval2D(new Interval1D(-2, -1), new Interval1D(-2, -1))
                        .boundingUnion(new Interval2D(new Interval1D(-4, -3),
                                new Interval1D(-4, -3))));
    }

    @Test
    public void testIsUnionable() {
        assertEquals(false,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .isUnionableWith(new Interval2D(new Interval1D(2, 4),
                                new Interval1D(2, 4))));
    }

    @Test
    public void testIsUnionable2() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .isUnionableWith(new Interval2D(new Interval1D(1, 2),
                                new Interval1D(2, 4))));
    }

    @Test
    public void testIsUnionable3() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .isUnionableWith(new Interval2D(new Interval1D(-3, 2),
                                new Interval1D(1, 2))));
    }

    @Test
    public void testIsUnionable4() {
        assertEquals(true,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .isUnionableWith(new Interval2D(new Interval1D(0, 3),
                                new Interval1D(0, 3))));
    }

    @Test
    public void testIsUnionableWithDisjointIntervals() {
        assertEquals(false,
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .isUnionableWith(new Interval2D(new Interval1D(3, 4),
                                new Interval1D(3, 4))));
    }

    @Test
    public void testIsUnionableWithIntervalsThatDontAddUpToARectangle() {
        assertEquals(false,
                new Interval2D(new Interval1D(1, 3), new Interval1D(1, 3))
                        .isUnionableWith(new Interval2D(new Interval1D(2, 4),
                                new Interval1D(2, 4))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnion() {
        new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).union(
                new Interval2D(new Interval1D(2, 4), new Interval1D(2, 4)));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testUnionWithDisjunctIntervals() {
        new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).union(
                new Interval2D(new Interval1D(3, 4), new Interval1D(3, 4)));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testUnionWithDisjunctIntervals2() {
        new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2)).union(
                new Interval2D(new Interval1D(1, 4), new Interval1D(3, 4)));
    }

    @Test
    public void testToString() {
        assertEquals("[1..2]×[1..2]",
                new Interval2D(new Interval1D(1, 2), new Interval1D(1, 2))
                        .toString());
    }
}
