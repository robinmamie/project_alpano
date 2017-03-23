package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.AzimuthTest__Prof;
import ch.epfl.alpano.DistanceTest__Prof;
import ch.epfl.alpano.Math2Test__Prof;

@RunWith(Suite.class)
@SuiteClasses({
    AzimuthTest__Prof.class
    , DistanceTest__Prof.class
    , Math2Test__Prof.class
    })

public class Week1TestSuite__Prof {}
