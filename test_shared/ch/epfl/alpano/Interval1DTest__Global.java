package ch.epfl.alpano;

import static ch.epfl.test.ObjectTest.hashCodeIsCompatibleWithEquals;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import ch.epfl.test.ObjectTest;

public class Interval1DTest__Global {
    private static Interval1D i_0_9() { return new Interval1D(0, 9); }
    private static Interval1D i_0_2() { return new Interval1D(0, 2); }
    private static Interval1D i_3_5() { return new Interval1D(3, 5); }
    private static Interval1D i_4_6() { return new Interval1D(4, 6); }
    private static Interval1D i_6_9() { return new Interval1D(6, 9); }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFailsForInvalidBounds() {
        new Interval1D(1, 0);
    }

    @Test
    public void constructorWorksForSingletonInterval() {
        new Interval1D(10, 10);
    }

    @Test
    public void containsIsTrueOnlyForTheIntervalsElements() {
        int sqrtIt = (int)ceil(sqrt(RANDOM_ITERATIONS));
        Random rng = newRandom();
        for (int i = 0; i < sqrtIt; ++i) {
            int a = rng.nextInt(200) - 100;
            int b = a + rng.nextInt(50);
            Interval1D interval = new Interval1D(a, b);
            for (int j = 0; j < sqrtIt; ++j) {
                int v = rng.nextInt(200) - 100;
                assertEquals(a <= v && v <= b, interval.contains(v));
            }
        }
    }

    @Test
    public void containsWorksAtTheLimit() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int max = rng.nextInt(2000);
            int a = rng.nextInt(max) - 1000;
            int b = max - 1000;
            Interval1D interval = new Interval1D(a, b);
            assertFalse(interval.contains(a - 1));
            assertTrue(interval.contains(a));
            assertTrue(interval.contains(b));
            assertFalse(interval.contains(b + 1));
        }
    }
    @Test
    public void sizeWorksOnKnownIntervals() {
        assertEquals(10, i_0_9().size());
        assertEquals(3, i_0_2().size());
        assertEquals(3, i_3_5().size());
        assertEquals(3, i_4_6().size());
        assertEquals(4, i_6_9().size());
    }

    @Test
    public void sizeOfIntersectionWorksOnNonIntersectingIntervals() {
        assertEquals(0, i_0_2().sizeOfIntersectionWith(i_3_5()));
        assertEquals(0, i_0_2().sizeOfIntersectionWith(i_4_6()));
        assertEquals(0, i_0_2().sizeOfIntersectionWith(i_6_9()));
    }

    @Test
    public void sizeOfIntersectionWorksOnIntersectingIntervals() {
        assertEquals(3, i_0_2().sizeOfIntersectionWith(i_0_9()));
        assertEquals(3, i_0_9().sizeOfIntersectionWith(i_0_2()));
        assertEquals(1, i_4_6().sizeOfIntersectionWith(i_6_9()));
    }

    @Test
    public void boundingUnionWorksOnKnownIntervals() {
        assertEquals(0, i_0_2().boundingUnion(i_6_9()).includedFrom());
        assertEquals(9, i_0_2().boundingUnion(i_6_9()).includedTo());
        assertEquals(0, i_6_9().boundingUnion(i_0_2()).includedFrom());
        assertEquals(9, i_6_9().boundingUnion(i_0_2()).includedTo());
        assertEquals(0, i_0_9().boundingUnion(i_0_9()).includedFrom());
        assertEquals(9, i_0_9().boundingUnion(i_0_9()).includedTo());
    }

    @Test
    public void isUnionableWithWorksOnKnownIntervals() {
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

    @Test(expected = IllegalArgumentException.class)
    public void unionFailsOnNonUnionableIntervals() {
        i_0_2().union(i_6_9());
    }

    @Test
    public void unionWorksWhenOneIntervalContainsTheOther() {
        assertEquals(i_0_9(), i_0_9().union(i_3_5()));
        assertEquals(i_0_9(), i_3_5().union(i_0_9()));
    }

    @Test
    public void unionWorksWithASingleInterval() {
        assertEquals(i_3_5(), i_3_5().union(i_3_5()).union(i_3_5()));
    }

    @Test
    public void unionWorksWhenOneIntervalIsContiguousWithTheOther() {
        Interval1D i = i_0_2().union(i_6_9().union(i_3_5()));
        assertEquals(i_0_9(), i);
    }

    @Test
    public void equalsIsStructural() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int a = rng.nextInt(), b = rng.nextInt();
            Interval1D int1 = new Interval1D(min(a, b), max(a, b));
            Interval1D int2 = new Interval1D(min(a, b), max(a, b));
            Interval1D int3 = new Interval1D(min(a, b) + 1, max(a, b) + 1);
            assertTrue(int1.equals(int2));
            assertFalse(int1.equals(int3));
        }
    }

    @Test
    public void hashCodeAndEqualsAreCompatible() {
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int a = rng.nextInt(), b = rng.nextInt();
            int c = rng.nextInt(), d = rng.nextInt();
            Interval1D int1 = new Interval1D(min(a, b), max(a, b));
            Interval1D int2 = new Interval1D(min(c, d), max(c, d));
            Interval1D int3 = new Interval1D(min(c, d), max(c, d));
            assertTrue(hashCodeIsCompatibleWithEquals(int1, int2));
            assertTrue(hashCodeIsCompatibleWithEquals(int2, int3));
        }
    }
    
    Interval1D i1 = new Interval1D(0, 25);
    Interval1D i2 = new Interval1D(10, 50);
    Interval1D i3 = new Interval1D(10, 10);
    Interval1D i4 = new Interval1D(25, 25);
    Interval1D i5 = new Interval1D(51, 60);

    
    @Test(expected = IllegalArgumentException.class)
    public void constructorFailIfIncludedFromBiggerThanIncludedTo(){
        new Interval1D(10, 5);
    }
    
    @Test
    public void constructorWorksOnNormalCase(){
        try{
            new Interval1D(5,7);
        }catch(Exception e){
            fail("Error with the creation of an intervall");
        }
        assertTrue(true);
    }
    
    @Test
    public void includedFromTest(){
        assertEquals(0, i1.includedFrom());
        assertEquals(10, i2.includedFrom());
        assertEquals(10, i3.includedFrom());
    }
    
    @Test
    public void includedToTest() {
        assertEquals(25, i1.includedTo());
        assertEquals(50, i2.includedTo());
        assertEquals(10, i3.includedTo());
    }
    
    @Test
    public void containsTest(){
        assertFalse(i1.contains(30));
        assertTrue(i1.contains(10));
        assertTrue(i1.contains(0));
        assertTrue(i1.contains(0));
        assertTrue(i1.contains(25));
        
        assertTrue(i3.contains(10));
        assertFalse(i3.contains(0));
        assertFalse(i3.contains(25));
    }
   
    @Test
    public void sizeTest() {
        assertEquals(26, i1.size());
        assertEquals(1, i3.size());
        assertEquals(41, i2.size());
    }
    
    @Test
    public void sizeOfIntersectionWithTest() {
        assertEquals(16, i1.sizeOfIntersectionWith(i2));
        assertEquals(16, i2.sizeOfIntersectionWith(i1));
        assertEquals(1, i1.sizeOfIntersectionWith(i3));
        assertEquals(1, i2.sizeOfIntersectionWith(i3));
        assertEquals(1, i3.sizeOfIntersectionWith(i3));
        assertEquals(1, i1.sizeOfIntersectionWith(i4));
        assertEquals(1, i4.sizeOfIntersectionWith(i1));
    }
    
    @Test
    public void boundingUnionTest() {
        assertEquals(new Interval1D(0, 50), i1.boundingUnion(i2));
        assertEquals(new Interval1D(0, 50), i2.boundingUnion(i1));
        assertEquals(i3, i3.boundingUnion(i3));
        assertEquals(i1, i1.boundingUnion(i1));
        assertEquals(i1, i1.boundingUnion(i4));
        assertEquals(i1, i4.boundingUnion(i1));
    }
    
    @Test
    public void equalsTest() {
        assertEquals(i1, new Interval1D(0, 25));
        assertEquals(i3, new Interval1D(10, 10));
    }
    
    @Test
    public void isUnionableWith() {
        assertTrue(i1.isUnionableWith(i4));
        assertTrue(i4.isUnionableWith(i1));
        assertTrue(i2.isUnionableWith(i3));
        assertFalse(i3.isUnionableWith(i4));
        assertFalse(i4.isUnionableWith(i3));
        assertTrue(i2.isUnionableWith(i5));
        assertTrue(i2.isUnionableWith(i2));
    }
    
    @Test
    public void unionTest() {
        Interval1D l1 = new Interval1D(10, 10), l2 = new Interval1D(11, 11);
        assertEquals(new Interval1D(10, 11), l1.union(l2));
        assertEquals(new Interval1D(10, 60), i2.union(i5));
    }
    
    @Test (expected = IllegalArgumentException.class )
    public void unionTestFail(){
        i1.union(i5);
    }
    
    @Test
    public void equalsCompatibleWithHashCode() {
        ObjectTest.hashCodeIsCompatibleWithEquals(new Interval1D(10, 10), new Interval1D(10, 10));
    }
    
    @Test
    public void toStringTest() {
        assertEquals("[0..25]", i1.toString());
        assertEquals("[10..10]", i3.toString());
    }
    


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
    


    @Test (expected = IllegalArgumentException.class)
    public void testConstructeur() {
        new Interval1D(6,0);
    }
    
    @Test
    public void testConstructeurOk(){
        new Interval1D (0,6);
    }
    
    @Test 
    public void testIncludedFrom1(){
        Interval1D interval = new Interval1D(0,6);
        assertEquals(0, interval.includedFrom(), 0);
    }
    
    @Test
    public void testIncludedTo1() {
        Interval1D interval = new Interval1D(0,6);
        assertEquals(6, interval.includedTo(), 0);
    }
    
    @Test
    public void testContains(){
        Interval1D interval = new Interval1D(0,6);
        assertTrue(interval.contains(5));
    }
    
    @Test
    public void testContainsUp(){
        Interval1D interval = new Interval1D(0,6);
        assertTrue(interval.contains(6));
    }
    
    @Test
    public void testContainsDown(){
        Interval1D interval = new Interval1D(0,6);
        assertTrue(interval.contains(0));
    }
    
    @Test
    public void testContainsWrong(){
        Interval1D interval = new Interval1D(0,6);
        assertFalse(interval.contains(8));
    }
    
    @Test
    public void testSize1(){
        Interval1D interval = new Interval1D(0,3);
        assertEquals(4, interval.size(), 0);
    }
    
    @Test
    public void testSizeZero(){
        Interval1D interval = new Interval1D(0,0);
        assertEquals(1, interval.size(), 0);
    }
    
    @Test
    public void testSizeOfIntersectionWith1(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(1,6);
        assertEquals(3, interval.sizeOfIntersectionWith(interval2),0);
    }
    
    @Test
    public void testSizeOfIntersectionWith21(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(3,6);
        assertEquals(1, interval.sizeOfIntersectionWith(interval2),0);
    }
    
    @Test
    public void testSizeOfIntersectionWith31(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(0,3);
        assertEquals(4, interval.sizeOfIntersectionWith(interval2),0);
    }
    
    @Test
    public void testSizeOfIntersectionWith4(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(4,6);
        assertEquals(0, interval.sizeOfIntersectionWith(interval2),0);
    }
    
    @Test
    public void testBoundingUnion1(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(6,7);
        Interval1D interval3 = new Interval1D(0,7);
        assertEquals(interval3, interval.boundingUnion(interval2));
    }
    
    @Test
    public void testBoundingUnion21(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(6,7);
        Interval1D interval3 = new Interval1D(0,7);
        assertEquals(interval3, interval2.boundingUnion(interval));
    }
    
    @Test
    public void testBoundingUnion3(){
        Interval1D interval = new Interval1D(0,12);
        Interval1D interval2 = new Interval1D(6,7);
        Interval1D interval3 = new Interval1D(0,12);
        assertEquals(interval3, interval.boundingUnion(interval2));
    }
    
    @Test
    public void testBoundingUnion4(){
        Interval1D interval = new Interval1D(0,10);
        Interval1D interval2 = new Interval1D(6,7);
        Interval1D interval3 = new Interval1D(0,10);
        assertEquals(interval3, interval2.boundingUnion(interval));
    }
    
    @Test
    public void testIsUnionableWith(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(2,7);
        assertTrue(interval.isUnionableWith(interval2));
    }
    
    @Test
    public void testIsUnionableWith2(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(0,3);
        assertTrue(interval.isUnionableWith(interval2));
    }
    
    @Test
    public void testIsUnionableWith3(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(3,7);
        assertTrue(interval.isUnionableWith(interval2));
    }
    
    @Test
    public void testIsUnionableWith5(){
        Interval1D interval = new Interval1D(0,0);
        Interval1D interval2 = new Interval1D(0,7);
        assertTrue(interval.isUnionableWith(interval2));
    }
    
    @Test
    public void testIsUnionableWithWrong(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(5,7);
        assertFalse(interval.isUnionableWith(interval2));
    }
    
    @Test
    public void testUnion1(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(2,5);
        Interval1D interval3 = new Interval1D(0,5);
        assertEquals(interval3, interval.union(interval2));
    }
    
    @Test
    public void testUnion21(){
        Interval1D interval = new Interval1D(0,0);
        Interval1D interval2 = new Interval1D(0,5);
        assertEquals(interval2, interval.union(interval2));
    }
    
    @Test
    public void testUnion31(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(3,5);
        Interval1D interval3 = new Interval1D(0,5);
        assertEquals(interval3, interval.union(interval2));
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testUnionException(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(6,7);
        interval.union(interval2);
    }
    
    @Test
    public void testEquals(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(0,3);
        assertTrue(interval.equals(interval2));
    }
    
    @Test
    public void testEqualsWrong(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(5,8);
        assertFalse(interval.equals(interval2));
    }
    
    @Test
    public void testHashCode(){
        Interval1D interval = new Interval1D(0,3);
        Interval1D interval2 = new Interval1D(5,8);
        assertTrue(hashCodeIsCompatibleWithEquals(interval, interval2));
    }
    
    @Test
    public void testToString(){
        Interval1D interval = new Interval1D(0,3);
        assertTrue(interval.toString().equals("[0..3]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsErrorIfIncorrectArgumentsGiven() {
        new Interval1D(1, 0);
    }
    
    @Test
    public void containsWorksOnMiddleValue() {
        Interval1D test = new Interval1D(0,2);
        assertTrue(test.contains(1));
    }
    
    @Test
    public void containsWorksOnLowerBound() {
        Interval1D test = new Interval1D(0,2);
        assertTrue(test.contains(0));
    }
    
    @Test
    public void containsWorksOnUpperBound() {
        Interval1D test = new Interval1D(0,2);
        assertTrue(test.contains(2));
    }
    
    @Test
    public void containsFailsOnOtherValue() {
        Interval1D test = new Interval1D(0,2);
        assertFalse(test.contains(3));
    }
    
    @Test
    public void sizeWorksOnRandomInterval() {
        Interval1D test = new Interval1D(0,2);
        int expectedSize = 3;
        int actualSize   = test.size();
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void sizeWorksOnSmallestInterval() {
        Interval1D test = new Interval1D(0,0);
        int expectedSize = 1;
        int actualSize   = test.size();
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void sizeOfIntersectionWithWorksWithRandomIntervals() {
        Interval1D a = new Interval1D(0,2);
        Interval1D b = new Interval1D(1,3);
        int expectedSize = 2;
        int actualSize    = a.sizeOfIntersectionWith(b);
        assertEquals(expectedSize, actualSize);
        actualSize        = b.sizeOfIntersectionWith(a);
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void sizeOfIntersectionWithWorksWithDisjointIntervals() {
        Interval1D a = new Interval1D(0,2);
        Interval1D b = new Interval1D(3,5);
        int expectedSize = 0;
        int actualSize    = a.sizeOfIntersectionWith(b);
        assertEquals(expectedSize, actualSize);
        actualSize        = b.sizeOfIntersectionWith(a);
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void sizeOfIntersectionWithWorksWithInnerIntervals() {
        Interval1D a = new Interval1D(0,5);
        Interval1D b = new Interval1D(2,4);
        int expectedSize = 3;
        int actualSize    = a.sizeOfIntersectionWith(b);
        assertEquals(expectedSize, actualSize);
        actualSize        = b.sizeOfIntersectionWith(a);
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void unionWorksWithUnionableIntervals() {
        Interval1D a = new Interval1D(0,5);
        Interval1D b = new Interval1D(2,6);
        Interval1D expectedInterval = new Interval1D(0,6);
        Interval1D actualInterval   = a.union(b);
        assertEquals(expectedInterval, actualInterval);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void unionFailsWithNonUnionableIntervals() {
        Interval1D a = new Interval1D(0,5);
        Interval1D b = new Interval1D(7,7);
        
        a.union(b);
    }
    
    @Test
    public void unionWorksWithUnionableInnerIntervals() {
        Interval1D a = new Interval1D(0,5);
        Interval1D b = new Interval1D(2,3);
        Interval1D expectedInterval = a;
        Interval1D actualInterval   = a.union(b);
        assertEquals(expectedInterval, actualInterval);
    }
    
    @Test
    public void toStringWorksWithRandomInterval() {
        Interval1D test = new Interval1D(4,12);
        String expectedString = "[4..12]";
        String actualString   = test.toString();
        assertEquals(expectedString, actualString);
    }

}
