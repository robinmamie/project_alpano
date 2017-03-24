package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.dem.ElevationProfileTest__Prof;
import ch.epfl.alpano.dem.HgtDiscreteElevationModelTest__Prof;

@RunWith(Suite.class)
@SuiteClasses({
    ElevationProfileTest__Prof.class
    , HgtDiscreteElevationModelTest__Prof.class
    })

public class Week4TestSuite__Prof {}
