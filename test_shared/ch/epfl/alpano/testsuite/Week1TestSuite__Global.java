package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.AzimuthTest__Global;
import ch.epfl.alpano.DistanceTest__Global;
import ch.epfl.alpano.Math2Test__Global;
import ch.epfl.alpano.PreconditionsTest__Global;

@RunWith(Suite.class)
@SuiteClasses({
    AzimuthTest__Global.class
    , DistanceTest__Global.class
    , Math2Test__Global.class
    , PreconditionsTest__Global.class
    })

public class Week1TestSuite__Global {}
