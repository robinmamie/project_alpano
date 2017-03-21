package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.GeoPointTest__Global;
import ch.epfl.alpano.Interval1DTest__Global;
import ch.epfl.alpano.Interval2DTest__Global;

@RunWith(Suite.class)
@SuiteClasses({
    GeoPointTest__Global.class
    , Interval1DTest__Global.class
    , Interval2DTest__Global.class
    })

public class Week2TestSuite__Global {}
