package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.GeoPointTest;
import ch.epfl.alpano.Interval1DTest;
import ch.epfl.alpano.Interval2DTest;


@RunWith(Suite.class)
@SuiteClasses({
                ch.epfl.alpano.dem.ElevationProfileTest.class,
                ch.epfl.alpano.dem.HgtDiscreteElevationModelTest.class})

public class Week4TestSuite {

}
