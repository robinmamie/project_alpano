package ch.epfl.alpano;

import static org.junit.Assert.*;

import org.junit.Test;

public class Interval2DTest {

    @Test(expected = NullPointerException.class)
    public void constructorThrowsErrorIfIncorrectArgumentsGiven() {
        new Interval2D(null, null);
    }
    
    @Test
    public void containsWorksOnMiddleValue() {
        Interval1D a = new Interval1D(0,2);
        Interval1D b = new Interval1D(5,8);
        Interval2D c = new Interval2D(a, b);
        assertTrue(c.contains(1, 7));
    }
    
    @Test
    public void containsWorksOnLowerBound() {
        Interval1D a = new Interval1D(0,2);
        Interval1D b = new Interval1D(5,8);
        Interval2D c = new Interval2D(a, b);
        assertTrue(c.contains(0,5));
    }
    
    @Test
    public void containsWorksOnUpperBound() {
        Interval1D a = new Interval1D(0,2);
        Interval1D b = new Interval1D(5,8);
        Interval2D c = new Interval2D(a, b);
        assertTrue(c.contains(2,8));
    }
    
    @Test
    public void containsFailsOnOtherValue() {
        Interval1D a = new Interval1D(0,2);
        Interval1D b = new Interval1D(5,8);
        Interval2D c = new Interval2D(a, b);
        assertFalse(c.contains(10,6));
    }
    
    @Test
    public void sizeWorksOnRandomInterval() {
        Interval1D a = new Interval1D(0,2);
        Interval1D b = new Interval1D(5,8);
        Interval2D c = new Interval2D(a, b);
        int expectedSize = 12;
        int actualSize   = c.size();
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void sizeOfIntersectionWithWorksWithRandomIntervals() {
        Interval1D a1 = new Interval1D(0,2);
        Interval1D b1 = new Interval1D(5,8);
        Interval2D c1 = new Interval2D(a1,b1);
        Interval1D a2 = new Interval1D(2,4);
        Interval1D b2 = new Interval1D(3,8);
        Interval2D c2 = new Interval2D(a2,b2);
        int expectedSize = 4;
        int actualSize    = c1.sizeOfIntersectionWith(c2);
        assertEquals(expectedSize, actualSize);
        actualSize        = c2.sizeOfIntersectionWith(c1);
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void sizeOfIntersectionWithWorksWithDisjointIntervals() {
        Interval1D a1 = new Interval1D(0,2);
        Interval1D b1 = new Interval1D(5,8);
        Interval2D c1 = new Interval2D(a1,b1);
        Interval1D a2 = new Interval1D(3,4);
        Interval1D b2 = new Interval1D(3,8);
        Interval2D c2 = new Interval2D(a2,b2);
        int expectedSize = 0;
        int actualSize    = c1.sizeOfIntersectionWith(c2);
        assertEquals(expectedSize, actualSize);
        actualSize        = c2.sizeOfIntersectionWith(c1);
        assertEquals(expectedSize, actualSize);
    }
    
    @Test
    public void unionWorksWithUnionableIntervals() {
        Interval1D a1 = new Interval1D(0,2);
        Interval1D b1 = new Interval1D(5,8);
        Interval2D c1 = new Interval2D(a1,b1);
        Interval1D a2 = new Interval1D(2,4);
        Interval1D b2 = new Interval1D(3,8);
        Interval2D c2 = new Interval2D(a2,b2);
        
        Interval1D e1 = new Interval1D(0,4);
        Interval1D e2 = new Interval1D(3,8);
        Interval2D expectedInterval = new Interval2D(e1,e2);
        Interval2D actualInterval   = c1.union(c2);
        assertEquals(expectedInterval, actualInterval);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void unionFailsWithNonUnionableIntervals() {
        Interval1D a1 = new Interval1D(0,2);
        Interval1D b1 = new Interval1D(5,8);
        Interval2D c1 = new Interval2D(a1,b1);
        
        Interval1D a2 = new Interval1D(3,4);
        Interval1D b2 = new Interval1D(3,8);
        Interval2D c2 = new Interval2D(a2,b2);
        
        c1.union(c2);
    }
    
    @Test
    public void toStringWorksWithRandomInterval() {
        Interval1D a1 = new Interval1D(-1,14);
        Interval1D b1 = new Interval1D(0,22);
        Interval2D c1 = new Interval2D(a1,b1);
        
        String expectedString = "[-1..14]×[0..22]";
        String actualString   = c1.toString();
        assertEquals(expectedString, actualString);
    }

}
