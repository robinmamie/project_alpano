package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.dem.CompositeDiscreteElevationModelTest__Prof;
import ch.epfl.alpano.dem.ContinuousElevationModelTest__Prof;

@RunWith(Suite.class)
@SuiteClasses({
    CompositeDiscreteElevationModelTest__Prof.class
    , ContinuousElevationModelTest__Prof.class
    })

public class Week3TestSuite__Prof {}
