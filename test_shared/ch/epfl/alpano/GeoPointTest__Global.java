package ch.epfl.alpano;

import static java.lang.Math.PI;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;

/**
 * Tests GeoPoint
*/

import org.junit.Test;

public class GeoPointTest__Global {
    private static GeoPoint CORNAVIN = new GeoPoint(toRadians(6.14308), toRadians(46.21023));
    private static GeoPoint M1_EPFL = new GeoPoint(toRadians(6.56599), toRadians(46.52224));
    private static GeoPoint FEDERAL_PALACE = new GeoPoint(toRadians(7.44428), toRadians(46.94652));
    private static GeoPoint SAENTIS = new GeoPoint(toRadians(9.34324), toRadians(47.24942));
    private static GeoPoint MONTE_TAMARO = new GeoPoint(toRadians(8.86598), toRadians(46.10386));

    @Test
    public void distanceToWorksOnKnownPoints() {
        assertEquals(226_000, M1_EPFL.distanceTo(SAENTIS), 10);
        assertEquals( 81_890, M1_EPFL.distanceTo(FEDERAL_PALACE), 10);
        assertEquals(143_560, FEDERAL_PALACE.distanceTo(MONTE_TAMARO), 10);
        assertEquals(269_870, SAENTIS.distanceTo(CORNAVIN), 10);
    }

    @Test
    public void azimuthToWorksOnKnownPoints() {
        assertEquals( 68.03, toDegrees(M1_EPFL.azimuthTo(SAENTIS)), 0.01);
        assertEquals( 54.50, toDegrees(M1_EPFL.azimuthTo(FEDERAL_PALACE)), 0.01);
        assertEquals(130.23, toDegrees(FEDERAL_PALACE.azimuthTo(MONTE_TAMARO)), 0.01);
        assertEquals(245.82, toDegrees(SAENTIS.azimuthTo(CORNAVIN)), 0.01);
    }

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
    

    private GeoPoint Lausanne = new GeoPoint(0.11576,0.81186);// 6.63282 , 46.516 
    private GeoPoint NewYork = new GeoPoint(-1.29165,0.71060);// -74.00597, 40.71427
    private GeoPoint Montpellier = new GeoPoint(0.06767, 0.76115);// 3.87723, 43.61092 
    private GeoPoint LevinNZ = new GeoPoint(3.05913,-0.70919);// 175.275 ,-40.63333 
    private GeoPoint Toulouse = new GeoPoint(0.02520,0.76104);//1.44367, 43.60426 
    private GeoPoint Zurich =  new GeoPoint(0.14923, 0.82670);//8.55, 47.3666
    private GeoPoint Geneve = new GeoPoint(toRadians(6.14569), toRadians(46.20222));

    
    // Constructor
    
    
    @Test
    public void CanConstruct() {
        new GeoPoint(Math.PI, Math.PI / 2);
    }

    @Test
    public void testCorrectValuesConstructor(){
        //cas limites
        new GeoPoint(-PI,0);
        new GeoPoint(PI,0);
        new GeoPoint(0,-PI/2);
        new GeoPoint(0,PI/2);
        
        //cas trivial
        new GeoPoint(PI/3,-PI/4);
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
    
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testWrongLongitudeConstructor1(){
        new GeoPoint(PI+1, 0);
    }
    
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testWrongLatitudeConstructor1(){
        new GeoPoint(0, PI);
    }
    
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testWrongLongitudeConstructor2(){
        new GeoPoint(-PI-1, 0);
    }
    
    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testWrongLatitudeConstructor2(){
        new GeoPoint(0, -3*PI/4);
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
    
    @Test
    public void distanceToOnKnownValue1(){
        GeoPoint lausanne = new GeoPoint(toRadians(6.631),toRadians(46.521));
        GeoPoint moscou = new GeoPoint(toRadians(37.623),toRadians(55.753));
        
        assertEquals(2370000, lausanne.distanceTo(moscou),3000);
    }

    @Test
    public void distanceToOnKnownValue2(){
        GeoPoint bale = new GeoPoint(toRadians(7.57327),toRadians(47.5584));
        GeoPoint lausanne = new GeoPoint(toRadians(6.63282),toRadians(46.516));
        
        assertEquals(136000, lausanne.distanceTo(bale),70);
    }
    
    @Test
    public void azimuthToOnKnownValue(){

        GeoPoint lausanne = new GeoPoint(toRadians(6.631),toRadians(46.521));
        GeoPoint moscou = new GeoPoint(toRadians(37.623),toRadians(55.753));
        
        assertEquals(toRadians(52.95), lausanne.azimuthTo(moscou),0.0001);
    }
    

    @Test
    public void distanceToAsTheRightValue(){
        assertEquals(19233000, Montpellier.distanceTo(LevinNZ),300);
        assertEquals(196000, Montpellier.distanceTo(Toulouse),300);
        assertEquals(6235000, NewYork.distanceTo(Lausanne),300);
    }
    @Test
    public void azymuthToIsWorkingFine(){
        assertEquals(Math.toRadians(57.08), Lausanne.azimuthTo(Zurich),0.1);
        assertEquals(49.28, Math.toDegrees(Geneve.azimuthTo(Lausanne)),3);        
    }
    
    @Test
    public void distanceToWorksWith2SameInputs (){
        assertEquals(0, lausanne.distanceTo(lausanne),10e-4);
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
