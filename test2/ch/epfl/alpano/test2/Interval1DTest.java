package ch.epfl.alpano.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.epfl.alpano.Interval1D;

/**
 * Tests Interval1D
 *
 * @author Charline Montial
 * @author Yves Zumbach
 */

public class Interval1DTest {

    @Test
    public void testCanConstructWithAllNegativeValues() {
        new Interval1D(-4, -2);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testConstructorThrowsExceptionWithUnvalidInterval() {
        new Interval1D(3, 2);
    }

    @Test
    public void testIncludedFrom() {
        assertEquals(2, new Interval1D(2, 3).includedFrom());
    }

    @Test
    public void testIncludedTo() {
        assertEquals(3, new Interval1D(2, 3).includedTo());
    }

    @Test
    public void testSize() {
        assertEquals(6, new Interval1D(2, 7).size());
    }

    @Test
    public void testSizeIfContainsOnlyOneElement() {
        assertEquals(1, new Interval1D(2, 2).size());
    }

    @Test
    public void testContain() {
        assertEquals(true, new Interval1D(2, 7).contains(3));
    }

    @Test
    public void testContainAtLowerBound() {
        assertEquals(true, new Interval1D(2, 7).contains(2));
    }

    @Test
    public void testContainAtUpperBound() {
        assertEquals(true, new Interval1D(2, 7).contains(7));
    }

    @Test
    public void testContainWithNegativeNumber() {
        assertEquals(true, new Interval1D(-5, 7).contains(-3));
    }

    @Test
    public void testContainLowerBoundWithNegativeNumber() {
        assertEquals(true, new Interval1D(-5, 7).contains(-5));
    }

    @Test
    public void testContainUpperBoundWithNegativeNumber() {
        assertEquals(true, new Interval1D(-5, -2).contains(-2));
    }

    @Test
    public void testSizeOfIntersectionWith() {
        Interval1D interval = new Interval1D(1, 3);
        Interval1D interval2 = new Interval1D(2, 4);
        assertEquals(2, interval.sizeOfIntersectionWith(interval2));
    }

    @Test
    public void testSizeOfIntersectionWith2() {
        Interval1D interval = new Interval1D(2, 4);
        Interval1D interval2 = new Interval1D(1, 3);
        assertEquals(2, interval.sizeOfIntersectionWith(interval2));
    }

    @Test
    public void testSizeOfIntersectionWith3() {
        Interval1D interval = new Interval1D(-1, 4);
        Interval1D interval2 = new Interval1D(1, 3);
        assertEquals(3, interval.sizeOfIntersectionWith(interval2));
    }

    @Test
    public void testSizeOfIntersectionWithNonOverlappingIntervals() {
        Interval1D interval = new Interval1D(1, 3);
        Interval1D interval2 = new Interval1D(5, 7);
        assertEquals(0, interval.sizeOfIntersectionWith(interval2));
    }

    @Test
    public void testSizeOfIntersectionWithNegativeBounds() {
        Interval1D interval = new Interval1D(-7, -2);
        Interval1D interval2 = new Interval1D(-4, -2);
        assertEquals(3, interval.sizeOfIntersectionWith(interval2));
    }

    @Test
    public void testSizeOfIntersectionWithNegativeBounds2() {
        Interval1D interval = new Interval1D(-7, -1);
        Interval1D interval2 = new Interval1D(-4, -3);
        assertEquals(2, interval.sizeOfIntersectionWith(interval2));
    }

    @Test
    public void testBoundingUnion() {
        assertEquals(new Interval1D(1, 7),
                new Interval1D(1, 5).boundingUnion(new Interval1D(4, 7)));
    }

    @Test
    public void testBoundingUnion2() {
        assertEquals(new Interval1D(1, 7),
                new Interval1D(4, 7).boundingUnion(new Interval1D(1, 5)));
    }

    @Test
    public void testBoundingUnionWithDisjunctIntervals() {
        assertEquals(new Interval1D(1, 7),
                new Interval1D(1, 2).boundingUnion(new Interval1D(6, 7)));
    }

    @Test
    public void testBoundingUnionWithNegativeValues() {
        assertEquals(new Interval1D(-7, -1),
                new Interval1D(-5, -1).boundingUnion(new Interval1D(-7, -4)));
    }

    @Test
    public void testBoundingUnion2WithNegativeValues() {
        assertEquals(new Interval1D(-7, -1),
                new Interval1D(-7, -4).boundingUnion(new Interval1D(-5, -1)));
    }

    @Test
    public void testBoundingUnionWithDisjunctIntervalsWithNegativeValues() {
        assertEquals(new Interval1D(-7, -1),
                new Interval1D(-2, -1).boundingUnion(new Interval1D(-7, -6)));
    }

    @Test
    public void testIsUnionable() {
        assertEquals(true,
                new Interval1D(1, 4).isUnionableWith(new Interval1D(3, 7)));
    }

    @Test
    public void testIsUnionable2() {
        assertEquals(false,
                new Interval1D(1, 2).isUnionableWith(new Interval1D(6, 7)));
    }

    @Test
    public void testIsUnionable3() {
        assertEquals(false,
                new Interval1D(6, 7).isUnionableWith(new Interval1D(1, 2)));
    }

    @Test
    public void testIsUnionable4() {
        assertEquals(true,
                new Interval1D(0, 7).isUnionableWith(new Interval1D(1, 2)));
    }

    @Test
    public void testIsUnionableWithNegativeValues() {
        assertEquals(true,
                new Interval1D(-4, -1).isUnionableWith(new Interval1D(-7, -3)));
    }

    @Test
    public void testIsUnionable2WithNegativeValues() {
        assertEquals(false,
                new Interval1D(-2, -1).isUnionableWith(new Interval1D(-7, -6)));
    }

    @Test
    public void testIsUnionable3WithNegativeValues() {
        assertEquals(false,
                new Interval1D(-7, -6).isUnionableWith(new Interval1D(-2, -1)));
    }

    @Test
    public void testIsUnionable4WithNegativeValues() {
        assertEquals(true,
                new Interval1D(-7, 0).isUnionableWith(new Interval1D(-2, -1)));
    }

    @Test
    public void testUnion() {
        assertEquals(new Interval1D(1, 7),
                new Interval1D(1, 5).union(new Interval1D(4, 7)));
    }

    @Test
    public void testUnion2() {
        assertEquals(new Interval1D(0, 7),
                new Interval1D(1, 5).union(new Interval1D(0, 7)));
    }

    @Test
    public void testUnionWithCommonMiddleBound() {
        assertEquals(new Interval1D(1, 7),
                new Interval1D(1, 4).union(new Interval1D(4, 7)));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testUnionThrowsExceptionWithUnvalidIntervals() {
        new Interval1D(1, 5).union(new Interval1D(7, 7));
    }

    public void testUnion3() {
        assertEquals(7, new Interval1D(1, 5).union(new Interval1D(6, 7)));
    }

    @Test
    public void testUnionWithNegativeValues() {
        assertEquals(new Interval1D(-7, -1),
                new Interval1D(-5, -1).union(new Interval1D(-7, -4)));
    }

    @Test
    public void testUnion2WithNegativeValues() {
        assertEquals(new Interval1D(-7, 0),
                new Interval1D(-5, -1).union(new Interval1D(-7, 0)));
    }

    @Test
    public void testUnionWithCommonMiddleBoundWithNegativeValues() {
        assertEquals(new Interval1D(-7, -1),
                new Interval1D(-4, -1).union(new Interval1D(-7, -4)));
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testUnionThrowsExceptionWithUnvalidIntervalsWithNegativeValues() {
        new Interval1D(-4, -1).union(new Interval1D(-7, -6));
    }

}
