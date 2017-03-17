package ch.epfl.alpano;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PanoramaParametersTest {

    private static PanoramaParameters p = new PanoramaParameters(
            new GeoPoint(0, 0), 1000, 0, Math.PI / 2.0, 3000, 101, 51);

    @Test
    public void verticalFieldOfViewWorksOnRandomPanorama() {
        double expectedValue = Math.PI / 4.0;
        double actualValue = p.verticalFieldOfView();
        assertEquals(expectedValue, actualValue, 1e-20);
    }

    @Test
    public void azimuthForXWorksForMidPixel() {
        double expectedAzimuth = 0;
        double actualAzimuth = p.azimuthForX(50.0);
        assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
    }

    @Test
    public void azimuthForXWorksForLowPixel() {
        double expectedAzimuth = Azimuth.canonicalize(-Math.PI / 4);
        double actualAzimuth = p.azimuthForX(0);
        assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
    }

    @Test
    public void azimuthForXWorksForUppPixel() {
        double expectedAzimuth = Math.PI / 4;
        double actualAzimuth = p.azimuthForX(100);
        assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
    }

    @Test
    public void xForAzimuthWorksForMidAzimuth() {
        double expectedAzimuth = 50.0;
        double actualAzimuth = p.xForAzimuth(0);
        assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
    }

    @Test
    public void xForAzimuthWorksForLowPixel() {
        double expectedAzimuth = 0.0;
        double actualAzimuth = p.xForAzimuth(7.0 * Math.PI / 4);
        assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
    }

    @Test
    public void xForAzimuthWorksForUppPixel() {
        double expectedAzimuth = 100.0;
        double actualAzimuth = p.xForAzimuth(Math.PI / 4);
        assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
    }
    
    @Test
    public void altitudeForYWorksForMidPixel() {
        double expectedAltitude = 0;
        double actualAltitude = p.altitudeForY(25.0);
        assertEquals(expectedAltitude, actualAltitude, 1e-10);
    }

    @Test
    public void altitudeForYWorksForLowPixel() {
        double expectedAltitude = Azimuth.canonicalize(Math.PI / 8);
        double actualAltitude = p.altitudeForY(0);
        assertEquals(expectedAltitude, actualAltitude, 1e-10);
    }

    @Test
    public void altitudeForYWorksForUppPixel() {
        double expectedAzimuth = -Math.PI / 8;
        double actualAzimuth = p.altitudeForY(50);
        assertEquals(expectedAzimuth, actualAzimuth, 1e-10);
    }
    
    @Test
    public void yForAltitudeWorksForMidAzimuth() {
        double expectedAltitude = 25.0;
        double actualAltitude = p.yForAltitude(0);
        assertEquals(expectedAltitude, actualAltitude, 1e-10);
    }

    @Test
    public void yForAltitudeWorksForLowPixel() {
        double expectedAltitude = 0.0;
        double actualAltitude = p.yForAltitude(Math.PI / 8);
        assertEquals(expectedAltitude, actualAltitude, 1e-10);
    }

    @Test
    public void yForAltitudeWorksForUppPixel() {
        double expectedAltitude = 50.0;
        double actualAltitude = p.yForAltitude(-Math.PI / 8);
        assertEquals(expectedAltitude, actualAltitude, 1e-10);
    }
}
