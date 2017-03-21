package ch.epfl.alpano;

import static ch.epfl.test.ObjectTest.hashCodeIsCompatibleWithEquals;
import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.test.ObjectTest;

public class Interval1DTest__Global {
    
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
        System.out.println(interval.toString());
        assertTrue(interval.toString().equals("[0..3]"));
    }

}
