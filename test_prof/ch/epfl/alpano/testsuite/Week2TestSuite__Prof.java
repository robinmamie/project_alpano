package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.GeoPointTest__Prof;
import ch.epfl.alpano.Interval1DTest__Prof;
import ch.epfl.alpano.Interval2DTest__Prof;

@RunWith(Suite.class)
@SuiteClasses({
    GeoPointTest__Prof.class
    , Interval1DTest__Prof.class
    , Interval2DTest__Prof.class
    })

public class Week2TestSuite__Prof {}
