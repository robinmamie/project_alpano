package ch.epfl.alpano.dem;

import static java.lang.Math.PI;
import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

public class ElevationProfileTest__Global {
    @Test(expected = NullPointerException.class)
    public void constructorFailsWhenElevationModelIsNull() {
        new ElevationProfile(null, new GeoPoint(0,0), 0, 100);
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailsWhenOriginIsNull() {
        new ElevationProfile(newConstantSlopeDEM(), null, 0, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFailsWhenAzimuthIsNotCanonical() {
        new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), 6.3, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorFailsWhenLengthIsZero() {
        new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void elevationAtFailsWhenXIsTooBig() {
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), 0, 100);
        p.elevationAt(101);
    }

    @Test
    public void elevationAtWorksOnConstantSlopeDEMGoingNorth() {
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), 0, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 100d * i;
            assertEquals(x, p.elevationAt(x), 1e-5);
        }
    }

    @Test
    public void elevationAtWorksOnConstantSlopeDEMGoingSouth() {
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), PI, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 100d * i;
            assertEquals(-x, p.elevationAt(x), 1e-5);
        }
    }

    @Test
    public void elevationAtWorksOnConstantSlopeDEMGoingEast() {
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), PI/2d, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 100d * i;
            assertEquals(x, p.elevationAt(x), 1e-5);
        }
    }

    @Test
    public void elevationAtWorksOnConstantSlopeDEMGoingWest() {
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), 3d*PI/2d, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 100d * i;
            assertEquals(-x, p.elevationAt(x), 1e-5);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void positionAtFailsWhenXIsTooBig() {
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), 0, 100);
        p.positionAt(101);
    }

    @Test
    public void positionAtProducesConstantLongitudeWhenGoingNorth() {
        double lon = toRadians(3);
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(lon,toRadians(40)), 0, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 500d * i;
            assertEquals(lon, p.positionAt(x).longitude(), 1e-5);
        }
    }

    @Test
    public void positionAtProducesConstantLongitudeWhenGoingSouth() {
        double lon = toRadians(3);
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(lon,toRadians(40)), PI, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 500d * i;
            assertEquals(lon, p.positionAt(x).longitude(), 1e-5);
        }
    }

    @Test
    public void positionAtProducesConstantLatitudeWhenGoingEast() {
        double lat = toRadians(40);
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(toRadians(3),lat), PI/2d, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 500d * i;
            assertEquals(lat, p.positionAt(x).latitude(), 1e-4);
        }
    }

    @Test
    public void positionAtProducesConstantLatitudeWhenGoingWest() {
        double lat = toRadians(40);
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(toRadians(3),lat), 3d*PI/2d, 100_000);
        for (int i = 0; i < 100; ++i) {
            double x = 500d * i;
            assertEquals(lat, p.positionAt(x).latitude(), 1e-4);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void slopeAtFailsWhenXIsNegative() {
        ElevationProfile p = new ElevationProfile(newConstantSlopeDEM(), new GeoPoint(0,0), 0, 100);
        p.positionAt(-1);
    }

    private static ContinuousElevationModel newConstantSlopeDEM() {
        Interval2D extent = new Interval2D(
                new Interval1D(-10_000, 10_000),
                new Interval1D(-10_000, 10_000));
        return new ContinuousElevationModel(new ConstantSlopeDEM(extent));
    }
    


    @Test
    public void testPositionAtOrigin() {
        HgtDiscreteElevationModel h = new HgtDiscreteElevationModel(new File("N45E006.hgt"));
        ContinuousElevationModel cDEM = new ContinuousElevationModel(h);
        GeoPoint o = new GeoPoint(toRadians(6), toRadians(45));
        ElevationProfile p = new ElevationProfile(cDEM, o, toRadians(0), 40);
        assertEquals(o.latitude(),p.positionAt(0).latitude(),1e-10);
        assertEquals(o.longitude(),p.positionAt(0).longitude(),1e-10);
    }
    
    @Test
    public void testPositionAtOnKnownValues() {
        int x[]={0,4096,8192,12288,102400};
        double lon[]={6.00000,6.03751,6.07506,6.11265,6.94857};
        double lat[]={46.00000,46.02604,46.05207,46.07809,46.64729};
        
        HgtDiscreteElevationModel h = new HgtDiscreteElevationModel(new File("N46E006.hgt"));
        ContinuousElevationModel cDEM = new ContinuousElevationModel(h);
        GeoPoint o = new GeoPoint(toRadians(6), toRadians(46));
        ElevationProfile p = new ElevationProfile(cDEM, o, toRadians(45),  110_000);
        
        for(int i=0;i<x.length;++i){
            //System.out.println(i);

            //System.out.println(new GeoPoint(toRadians(lon[i]),toRadians(lat[i]))+" "+p.positionAt(x[i]));
            assertEquals(toRadians(lat[i]),p.positionAt(x[i]).latitude(),1e-7);
            assertEquals(toRadians(lon[i]),p.positionAt(x[i]).longitude(),1e-7);
        }
    }
    

    @Test
    public void testPositionAtMax() {
        HgtDiscreteElevationModel h = new HgtDiscreteElevationModel(new File("N46E006.hgt"));
        ContinuousElevationModel cDEM = new ContinuousElevationModel(h);
        GeoPoint o = new GeoPoint(toRadians(6), toRadians(46));
        ElevationProfile p = new ElevationProfile(cDEM, o, toRadians(45), 102400);
        assertEquals(toRadians(46.64729),p.positionAt(102400).latitude(),1e-7);
        assertEquals(toRadians(6.94857),p.positionAt(102400).longitude(),1e-7);
    }
    
    @Test
    public void testPositionAtOnKnownValuesReversed() {
        int x[]={0,90112,94208,98304,102400};
        double lon[]={6.94857,6.11265,6.07506,6.03751,6.00000};
        double lat[]={46.64729,46.07809,46.05207,46.02604,46.00000};
        
        HgtDiscreteElevationModel h = new HgtDiscreteElevationModel(new File("N46E006.hgt"));
        ContinuousElevationModel cDEM = new ContinuousElevationModel(h);
        GeoPoint o = new GeoPoint(toRadians(6.94857), toRadians(46.64729));
        ElevationProfile p = new ElevationProfile(cDEM, o, toRadians(225),  110_000);
        
        for(int i=0;i<x.length;++i){
           // System.out.println(new GeoPoint(toRadians(lon[i]),toRadians(lat[i]))+" "+p.positionAt(x[i])+toMeters(toRadians(lon[i])-p.positionAt(x[i]).longitude()));
            assertEquals(toRadians(lat[i]),p.positionAt(x[i]).latitude(),1e-3);
            assertEquals(toRadians(lon[i]),p.positionAt(x[i]).longitude(),1e-3);
        }
    }
    
    @Test
    public void testPositionAtOnKnownNegativeValues() {
        int x[]={0,4096,8192,12288,102400};
        double lon[]={-6.00000,-6.03751,-6.07506,-6.11265,-6.94857};
        double lat[]={-46.00000,-46.02604,-46.05207,-46.07809,-46.64729};
        
        HgtDiscreteElevationModel h = new HgtDiscreteElevationModel(new File("N46E006.hgt"));
        ContinuousElevationModel cDEM = new ContinuousElevationModel(h);
        GeoPoint o = new GeoPoint(toRadians(-6), toRadians(-46));
        ElevationProfile p = new ElevationProfile(cDEM, o, toRadians(225),  110_000);
        
        for(int i=0;i<x.length;++i){
            //System.out.println(i);

            //System.out.println(new GeoPoint(toRadians(lon[i]),toRadians(lat[i]))+" "+p.positionAt(x[i]));
            assertEquals(toRadians(lat[i]),p.positionAt(x[i]).latitude(),1e-7);
            assertEquals(toRadians(lon[i]),p.positionAt(x[i]).longitude(),1e-7);
        }
    }
}
