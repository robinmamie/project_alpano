package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.dem.CompositeDiscreteElevationModelTest__Global;
import ch.epfl.alpano.dem.ContinuousElevationModelTest__Global;

@RunWith(Suite.class)
@SuiteClasses({
    CompositeDiscreteElevationModelTest__Global.class
    , ContinuousElevationModelTest__Global.class
    })

public class Week3TestSuite__Global {}
