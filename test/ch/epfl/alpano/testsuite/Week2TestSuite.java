package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.GeoPointTest;
import ch.epfl.alpano.Interval1DTest;
import ch.epfl.alpano.Interval2DTest;


@RunWith(Suite.class)
@SuiteClasses({
                GeoPointTest.class,
                Interval1DTest.class,
                Interval2DTest.class,
                ch.epfl.alpano.test2.GeoPointTest.class,
                ch.epfl.alpano.test2.Interval1DTest.class,
                ch.epfl.alpano.test2.Interval2DTest.class})

public class Week2TestSuite {

}
