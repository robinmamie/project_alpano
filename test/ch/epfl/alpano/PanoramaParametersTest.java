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

    PanoramaParameters pTest = new PanoramaParameters(
            new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)), 1280,
            Math.toRadians(162d), Math.PI, 4, 2500, 800);

    // Constructor
    @Test(expected = java.lang.NullPointerException.class)
    public void testPanoramaParametersConstructorNullGeoPoint() {
        new PanoramaParameters(null, 1280,
                Math.toRadians(162d), Math.toRadians(27d), 300000, 2500, 800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNoCanonicalAzimuthCornerCase() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math2.PI2, Math.toRadians(27d), 300000, 2500, 800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNoCanonicalAzimuth() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math2.PI2 + Math.PI, Math.toRadians(27d), 300000, 2500,
                800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorInvalidHorizontalFieldOfViewCornerCase() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), 0, 300000, 2500, 800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorInvalidHorizontalFieldOfView() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2 + 3, 300000, 2500, 800);
    }

    @Test
    public void testPanoramaParametersConstructorValidHorizontalFieldOfViewCornerCase() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2, 300000, 2500, 800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNullWidth() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2 + 3, 300000, 2500, 0);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNegativeWidth() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2 + 3, 300000, 2500, -2);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNullHeight() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2 + 3, 300000, 0, 800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNegativeHeight() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2 + 3, 300000, -5, 800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNullMaximalDistance() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2 + 3, 0, 2500, 800);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testPanoramaParametersConstructorNegativeMaximalDistance() {
        new PanoramaParameters(
                new GeoPoint(Math.toRadians(6.8087), Math.toRadians(6.8087)),
                1280, Math.toRadians(162d), Math2.PI2 + 3, -6, 2500, 800);
    }

    // Methods

    // azimuthForX
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testAzimuthForXNegativeIndex() {
        pTest.azimuthForX(-1);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testAzimuthForXInvalidIndex() {
        pTest.azimuthForX(pTest.width() - 1 + 0.000001);
    }

    @Test
    public void testAzimuthForXValidIndexCornerCase1() {
        pTest.azimuthForX(0);
    }

    @Test
    public void testAzimuthForXValidIndexCornerCase2() {
        pTest.azimuthForX(pTest.width() - 1);
    }

    // azimuthForY
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testAzimuthForYNegativeIndex() {
        pTest.altitudeForY(-1);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testAzimuthForYInvalidIndex() {
        pTest.altitudeForY(pTest.height() - 1 + 0.000001);
    }

    @Test
    public void testAzimuthForYValidIndexCornerCase1() {
        pTest.altitudeForY(0);
    }

    @Test
    public void testAzimuthForYValidIndexCornerCase2() {
        pTest.altitudeForY(pTest.height() - 1);
    }

    // xForAzimuth

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testXForAzimuthInvalidValue() {
        pTest.xForAzimuth(pTest.centerAzimuth()
                + (pTest.horizontalFieldOfView() / 2) + 1);
    }

    /* wut
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testXForAzimuthNegativeValue() {
        pTest.xForAzimuth(-3);
    }
    */
    
    @Test
    public void testXForAzimuthValidCornerCaseLowerBound() {
        pTest.xForAzimuth(
                pTest.centerAzimuth() - (pTest.horizontalFieldOfView() / 2));
    }

    @Test
    public void testXForAzimuthValidCornerCaseUpperBound() {
        pTest.xForAzimuth(
                pTest.centerAzimuth() + (pTest.horizontalFieldOfView() / 2));
    }

    // YForAltitude

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testYForAltitudeInvalidValue() {
        pTest.yForAltitude(pTest.centerAzimuth()
                + (pTest.horizontalFieldOfView() / 2) + 1);
    }

    /* wut
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testYForAltitudeNegativeValue() {
        pTest.yForAltitude(-3);
    }
    */

    /* wut
    @Test
    public void testYForAltitudeValidCornerCaseLowerBound() {
        pTest.yForAltitude(
                pTest.centerAzimuth() - (pTest.horizontalFieldOfView() / 2));
    }
    */

    /* wut
    @Test
    public void testYForAltitudeValidCornerCaseUpperBound() {
        pTest.yForAltitude(
                pTest.centerAzimuth() + (pTest.horizontalFieldOfView() / 2));
    }
    */
}
