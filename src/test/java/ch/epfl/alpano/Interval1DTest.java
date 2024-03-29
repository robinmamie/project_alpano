package ch.epfl.alpano;

import static org.junit.Assert.*;

import org.junit.Test;

public class Interval1DTest {

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
