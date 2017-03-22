package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.dem.ElevationProfileTest__Global;
import ch.epfl.alpano.dem.HgtDiscreteElevationModelTest__Global;

@RunWith(Suite.class)
@SuiteClasses({
    ElevationProfileTest__Global.class
    , HgtDiscreteElevationModelTest__Global.class
    })

public class Week4TestSuite__Global {}
