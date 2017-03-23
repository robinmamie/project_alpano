package ch.epfl.alpano.dem;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

public class ContinuousElevationModelTest__Global {
    private final static Interval2D EXT_100_100 = new Interval2D(
            new Interval1D(0, 100),
            new Interval1D(0, 100));

    private final static Interval2D EXT_13_13 = new Interval2D(
            new Interval1D(0, 13),
            new Interval1D(0, 13));

    @Test(expected = NullPointerException.class)
    public void constructorFailsWithNullDEM() {
        new ContinuousElevationModel(null);
    }

    @Test
    public void elevationAtReturns0OutsideOfExtent() {
        DiscreteElevationModel dDEM = new ConstantElevationDEM__Prof(EXT_100_100, 1000);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        assertEquals(0, cDEM.elevationAt(pointForSampleIndex(101, 0)), 0);
    }

    @Test
    public void elevationAtReturnsCorrectElevationInsideExtent() {
        double elevation = 1000;
        DiscreteElevationModel dDEM = new ConstantElevationDEM__Prof(EXT_100_100, elevation);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double x = rng.nextDouble() * 100d, y = rng.nextDouble() * 100d;
            assertEquals(elevation, cDEM.elevationAt(pointForSampleIndex(x, y)), 1e-10);
        }
    }

    @Test
    public void elevationAtInterpolatesJustOutsideExtent() {
        DiscreteElevationModel dDEM = new ConstantElevationDEM__Prof(EXT_100_100, 1000);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        assertEquals(500, cDEM.elevationAt(pointForSampleIndex(100.5, 10)), 1e-10);
    }

    @Test
    public void elevationAtReturnsCorrectInterpolatedElevation() {
        DiscreteElevationModel dDEM = new ConstantSlopeDEM__Global(EXT_100_100);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        Random rng = new Random();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double x = rng.nextDouble() * 100;
            double y = rng.nextDouble() * 100;
            assertEquals((x + y) * ConstantSlopeDEM__Global.INTER_SAMPLE_DISTANCE, cDEM.elevationAt(pointForSampleIndex(x, y)), 1e-6);
        }
    }

    @Test
    public void elevationAtStaysWithinBoundsOnRandomTerrain() {
        int maxElevation = 1000;
        DiscreteElevationModel dDEM = new RandomElevationDEM__Global(EXT_13_13, maxElevation);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double x = rng.nextDouble() * dDEM.extent().iX().size();
            double y = rng.nextDouble() * dDEM.extent().iY().size();
            double e = cDEM.elevationAt(pointForSampleIndex(x, y));
            assertTrue(0 <= e && e <= maxElevation);
        }
    }

    @Test
    public void slopeAtReturnsCorrectInterpolatedSlope() {
        DiscreteElevationModel dDEM = new ConstantSlopeDEM__Global(EXT_100_100);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        Random rng = new Random();
        double expectedSlope = Math.acos(1 / Math.sqrt(3));
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double x = 5 + rng.nextDouble() * 90;
            double y = 5 + rng.nextDouble() * 90;
            assertEquals(expectedSlope, cDEM.slopeAt(pointForSampleIndex(x, y)), 1e-4);
        }
    }

    @Test
    public void slopeAtStaysWithinBoundsOnRandomTerrain() {
        int maxElevation = 1000;
        DiscreteElevationModel dDEM = new RandomElevationDEM__Global(EXT_13_13, maxElevation);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        Random rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            double x = rng.nextDouble() * dDEM.extent().iX().size();
            double y = rng.nextDouble() * dDEM.extent().iY().size();
            double e = toDegrees(cDEM.slopeAt(pointForSampleIndex(x, y)));
            assertTrue(0 <= e && e < 90);
        }
    }

    private static GeoPoint pointForSampleIndex(double x, double y) {
        return new GeoPoint(toRadians(x / 3600d), toRadians(y / 3600d));
    }
    


	GeoPoint origin = new GeoPoint (0, 0);
	GeoPoint upperRightCorner = new GeoPoint(10/DiscreteElevationModel.SAMPLES_PER_RADIAN, 10/DiscreteElevationModel.SAMPLES_PER_RADIAN);
	GeoPoint middlePoint = new GeoPoint(5/DiscreteElevationModel.SAMPLES_PER_RADIAN, 5/DiscreteElevationModel.SAMPLES_PER_RADIAN);
	GeoPoint outOfExtent = new GeoPoint(1, 1);
	
	TestElevationModelConstantHeight__Global constantDEM = new TestElevationModelConstantHeight__Global();
	ContinuousElevationModel constantCEM = new ContinuousElevationModel	(constantDEM);
	
	TestElevationModelLinearHeight__Global linearDEM = new TestElevationModelLinearHeight__Global();
	ContinuousElevationModel linearCEM = new ContinuousElevationModel	(linearDEM);

	
	// Constant DEM
	
	@Test(expected = NullPointerException.class)
	public void testConstructorWithNullDem() {
		new ContinuousElevationModel(null);
	}

	@Test
	public void testElevationAtConstantElevation() {
		assertEquals(1.0, constantCEM.elevationAt(origin), 10e-7);
	}

	@Test
	public void testSlopeAtConstantElevationOutOfTheExtentOfTheCEM() {
		assertEquals(0.0, constantCEM.slopeAt(origin), 10e-7);
	}

	@Test
	public void testSlopeAtCornerPoint() {
		assertEquals(0.0, constantCEM.slopeAt(origin), 10e-7);
	}
	
	@Test
	public void testSlopeAtMiddlePoint() {
//		System.out.println("Longitude: " + DiscreteElevationModel.sampleIndex(middlePoint.longitude()));
//		System.out.println("Latitude: " + DiscreteElevationModel.sampleIndex(middlePoint.latitude()));
		assertEquals(0.0, constantCEM.slopeAt(middlePoint), 10e-7);
	}

	
	// Linear DEM
	
	@Test
	public void testElevationAtLinearElevation() {
		assertEquals(0.0, linearCEM.elevationAt(origin), 10e-7);
	}
	
	@Test
	public void testElevationAtLinearElevation2() {
		assertEquals(5.0, linearCEM.elevationAt(middlePoint), 10e-7);
	}
	
	@Test
	public void testElevationAtLinearElevation3() {
		assertEquals(10.0, linearCEM.elevationAt(upperRightCorner), 10e-7);
	}

	@Test
	public void testSlopeAtLinearElevationOutOfTheExtentOfTheCEM() {
		assertEquals(0.0, linearCEM.slopeAt(outOfExtent), 10e-7);
	}
}

class RandomElevationDEM__Global implements DiscreteElevationModel {
    private final Interval2D extent;
    private final double[][] elevations;

    public RandomElevationDEM__Global(Interval2D extent, int maxElevation) {
        this.extent = extent;
        this.elevations = randomElevations(extent.iX().size(), extent.iY().size(), maxElevation);
    }

    private static double[][] randomElevations(int width, int height, int maxElevation) {
        Random rng = newRandom();
        double[][] es = new double[width][height];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                es[x][y] = rng.nextInt(maxElevation + 1);
            }
        }
        return es;
    }

    @Override
    public Interval2D extent() {
        return extent;
    }

    @Override
    public double elevationSample(int x, int y) {
        return elevations[x][y];
    }

    @Override
    public void close() throws Exception { }
}



class TestElevationModelConstantHeight__Global implements DiscreteElevationModel {

	private final int elevation;
	private final Interval2D extent; 
	
	public TestElevationModelConstantHeight__Global(Interval2D i2d, int e) {
		extent = i2d;
		elevation = e;
	}
	
	public TestElevationModelConstantHeight__Global(int e) {
		extent = new Interval2D(new Interval1D(0, 10), new Interval1D(0, 10));
		elevation = e;
	}
	
	public TestElevationModelConstantHeight__Global() {
		extent = new Interval2D(new Interval1D(0, 10), new Interval1D(0, 10));
		elevation = 1;
	}
	
	@Override
	public void close() throws Exception {
	}

	@Override
	public Interval2D extent() {
		return extent;
	}

	@Override
	public double elevationSample(int x, int y) {
		return elevation;
	}

}

class TestElevationModelLinearHeight__Global implements DiscreteElevationModel {

	@Override
	public void close() throws Exception {
	}

	@Override
	public Interval2D extent() {
		return new Interval2D(new Interval1D(0, 10), new Interval1D(0, 10));
	}

	@Override
	public double elevationSample(int x, int y) {
		return x;
	}

}


class ConstantSlopeDEM__Global implements DiscreteElevationModel {
    public final static double INTER_SAMPLE_DISTANCE =
            2d * Math.PI * 6_371_000d / (3600d * 360d);

    private final Interval2D extent;

    public ConstantSlopeDEM__Global(Interval2D extent) {
        this.extent = extent;
    }

    @Override
    public Interval2D extent() { return extent; }

    @Override
    public double elevationSample(int x, int y) {
        return (x + y) * INTER_SAMPLE_DISTANCE;
    }

    @Override
    public void close() throws Exception {}
}