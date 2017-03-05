package ch.epfl.alpano;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GeoPointTest {

    private GeoPoint rolex = new GeoPoint(Math.toRadians(6.56727), Math.toRadians(46.51779));
    private GeoPoint eiger = new GeoPoint(Math.toRadians(8.00529), Math.toRadians(46.57759));
    private GeoPoint lausanne = new GeoPoint(Math.toRadians(6.631), Math.toRadians(46.521));
    private GeoPoint moscow   = new GeoPoint(Math.toRadians(37.623), Math.toRadians(55.753));
    
    
    @Test
    public void distanceWorksBetweenRolexEiger() {
        double expectedDistance = 110.49;
        double actualDistance   = rolex.distanceTo(eiger)/1000;
        assertEquals(expectedDistance, actualDistance, 10e-1);
    }
    
    @Test
    public void azimuthWorksBetweenRolexEiger() {
        double expectedAzimuth = 86.66;
        double actualAzimuth   = Math.toDegrees(rolex.azimuthTo(eiger));
        assertEquals(expectedAzimuth, actualAzimuth, 10e-1);
    }
    
    @Test
    public void distanceWorksBetweenLausanneMoscow() {
        double expectedDistance = 2370;
        double actualDistance   = lausanne.distanceTo(moscow)/1000;
        assertEquals(expectedDistance, actualDistance, 10e0);
    }
    
    @Test
    public void azimuthWorksBetweenLausanneMoscow() {
        double expectedAzimuth = 52.95;
        double actualAzimuth   = Math.toDegrees(lausanne.azimuthTo(moscow));
        assertEquals(expectedAzimuth, actualAzimuth, 10e-2);
    }
    
    @Test
    public void azimuthIsCanonical() {
        GeoPoint zero = new GeoPoint(0, 0);
        for(int i = 0; i < 360; ++i) {
            double expectedAngle = i;
            double actualAngle   = Math.toDegrees(zero.azimuthTo(new GeoPoint(10e-4 * Math.sin(Math.toRadians(i)),10e-4 * Math.cos(Math.toRadians(i)))));
            assertEquals(expectedAngle
                    , actualAngle
                    , 10e-5);
        }
    }
    
    @Test
    public void toStringOutputsCorrectValue() {
        double longitude = -5.6578;
        double latitude  = 12.3457;
        GeoPoint test = new GeoPoint(Math.toRadians(longitude), Math.toRadians(latitude));
        String expectedString = "(" + longitude + "," + latitude + ")";
        String actualString   = test.toString();
        
        assertEquals(expectedString, actualString);
    }
    
    @Test
    public void distanceToSamePointIsZero() {
        double expectedDistance = 0;
        double actualDistance   = lausanne.distanceTo(lausanne);
        assertEquals(expectedDistance, actualDistance, 1e-10);
    }

}
