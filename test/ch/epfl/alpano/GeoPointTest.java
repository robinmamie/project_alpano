package ch.epfl.alpano;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoPointTest {

    private static GeoPoint rolex = new GeoPoint(Math.toRadians(46.51779), Math.toRadians(6.56727));
    private static GeoPoint eiger = new GeoPoint(Math.toRadians(46.57759), Math.toRadians(8.00529));
    private static GeoPoint lausanne = new GeoPoint(Math.toRadians(46.521), Math.toRadians(6.631));
    private static GeoPoint moscow   = new GeoPoint(Math.toRadians(55.753), Math.toRadians(37.623));
    
    
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
