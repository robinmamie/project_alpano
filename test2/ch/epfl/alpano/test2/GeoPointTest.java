package ch.epfl.alpano.test2;

import static org.junit.Assert.assertEquals;

/**
 * Tests GeoPoint
 *
 * @author Charline Montial
 * @author Yves Zumbach
*/

import org.junit.Test;

import ch.epfl.alpano.Azimuth;
import ch.epfl.alpano.GeoPoint;

public class GeoPointTest {

    private GeoPoint learningCenterPoint = new GeoPoint(Math.toRadians(6.56730),
            Math.toRadians(46.51780));
    private GeoPoint eigerSummit = new GeoPoint(Math.toRadians(8.00537),
            Math.toRadians(46.57756));

    private GeoPoint lausanne = new GeoPoint(Math.toRadians(6.631),
            Math.toRadians(46.521));
    private GeoPoint moscow = new GeoPoint(Math.toRadians(37.623),
            Math.toRadians(55.753));

    private GeoPoint randomPoint = new GeoPoint(Math.toRadians(7.6543),
            Math.toRadians(54.3210));
    private GeoPoint randomPoint2 = new GeoPoint(Math.toRadians(56.8743),
            Math.toRadians(3.4567));

    private GeoPoint oneSideOfTheEarth = new GeoPoint(Math.toRadians(0),
            Math.toRadians(0));
    private GeoPoint otherSideOfTheEarth = new GeoPoint(Math.toRadians(180),
            Math.toRadians(0));

    private GeoPoint topOfTheEarth = new GeoPoint(Math.toRadians(0),
            Math.toRadians(-Math.PI / 2));
    private GeoPoint bottomOfTheEarth = new GeoPoint(Math.toRadians(180),
            Math.toRadians(Math.PI / 2));

    
    // Constructor
    
    
    @Test
    public void CanConstruct() {
        new GeoPoint(Math.PI, Math.PI / 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionWhenConstructedWithIllegalArgument1() {
        new GeoPoint(Math.PI, Math.PI);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionWhenConstructedWithIllegalArgument2() {
        new GeoPoint(Math.PI, -Math.PI);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionWhenConstructedWithIllegalArgument3() {
        new GeoPoint(2 * Math.PI, Math.PI);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsExceptionWhenConstructedWithIllegalArgument4() {
        new GeoPoint(-2 * Math.PI, Math.PI);
    }

    
    // Getters
    
    
    @Test
    public void testLongitude() {
        assertEquals(Math.toRadians(7.6543), randomPoint.longitude(), 0);
    }

    @Test
    public void testLatitude() {
        assertEquals(Math.toRadians(54.3210), randomPoint.latitude(), 0);
    }
    
    
    // Distances
    

    @Test
    public void testDistanceToLearningCenterEiger() {
        assertEquals(110490, learningCenterPoint.distanceTo(eigerSummit), 10e2);
    }

    @Test
    public void testDistanceToSamePoint() {
        assertEquals(0, learningCenterPoint.distanceTo(learningCenterPoint),
                10e-5);
    }

    @Test
    public void testDistanceToFromLausanneToMoscow() {
        assertEquals(2370000, lausanne.distanceTo(moscow), 10e3);
    }

    @Test
    public void testDistanceToFromLausanneToMoscowReverse() {
        assertEquals(2370000, moscow.distanceTo(lausanne), 10e3);
    }

    @Test
    public void testDistanceToFromRandom1ToRandom2() {
        assertEquals(7181000, randomPoint.distanceTo(randomPoint2), 10e3);
    }

    @Test
    public void testDistanceToFromOneSideOfTheEarthToOtherSideOfTheEarth() {
        assertEquals(20015086,
                oneSideOfTheEarth.distanceTo(otherSideOfTheEarth), 10e3);
    }

    @Test
    public void testDistanceToFromTopOfTheEarthToBottomOfTheEarth() {
        assertEquals(20015086, topOfTheEarth.distanceTo(bottomOfTheEarth),
                10e3);
    }
    
    
    // Azimuths
    

    @Test
    public void testAzimuthToLearningCenterToEiger() {
        assertEquals(Azimuth.canonicalize(Math.toRadians(86.02055555555556)),
                learningCenterPoint.azimuthTo(eigerSummit), 10e-2);
    }

    @Test
    public void testAzimuthToLausanneMoscow() {
        assertEquals(Azimuth.canonicalize(Math.toRadians(52.95)),
                lausanne.azimuthTo(moscow), 10e-4);
    }

    @Test
    public void testAzimuthToMoscowLausanne() {
        assertEquals(Azimuth.canonicalize(Math.toRadians(257.39472222222224)),
                moscow.azimuthTo(lausanne), 10e-4);
    }

    @Test
    public void testAzimuthToFromRandom1ToRandom2() {
        assertEquals(Math.toRadians(123.190278),
                randomPoint.azimuthTo(randomPoint2), 1e-4);
    }
    
    @Test
    public void testAzimuthToFromOneSideOfTheEarthToOtherSideOfTheEarth() {
        assertEquals(Azimuth.fromMath(Math.toRadians(180)),
                oneSideOfTheEarth.azimuthTo(otherSideOfTheEarth), 10e3);
    }
    
    
    // To String
    

    @Test
    public void testToString() {
        assertEquals("(6.5673,46.5178)", learningCenterPoint.toString());
    }

    @Test
    public void testToString2() {
        assertEquals("(8.0054,46.5776)", eigerSummit.toString());
    }

}
