package ch.epfl.alpano;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoPointTest {

    private static GeoPoint rolex = new GeoPoint(46.51779, 6.56727, true);
    private static GeoPoint eiger = new GeoPoint(46.57759, 8.00529, true);
    private static GeoPoint lausanne = new GeoPoint(46.521, 6.631, true);
    private static GeoPoint moscow   = new GeoPoint(55.753, 37.623, true);
    
    
    @Test
    public void distanceWorksBetweenRolexEiger() {
        double expectedDistance = 110;
        double actualDistance   = rolex.distanceTo(eiger)/1000;
        assertEquals(expectedDistance, actualDistance, 0.5);
    }
    
    @Test
    public void azimuthWorksBetweenRolexEiger() {
        double expectedAzimuth = -86.66;
        double actualAzimuth   = Math.toDegrees(rolex.azimuthTo(eiger));
        assertEquals(expectedAzimuth, actualAzimuth, 0.7);
    }
    
    @Test
    public void distanceWorksBetweenLausanneMoscow() {
        double expectedDistance = 2370;
        double actualDistance   = lausanne.distanceTo(moscow)/1000;
        assertEquals(expectedDistance, actualDistance, 5);
    }
    
    @Test
    public void azimuthWorksBetweenLausanneMoscow() {
        double expectedAzimuth = -52.95;
        double actualAzimuth   = Math.toDegrees(lausanne.azimuthTo(moscow));
        assertEquals(expectedAzimuth, actualAzimuth, 1e-2);
    }

}
