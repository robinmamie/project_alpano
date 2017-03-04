package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.AzimuthTest;
import ch.epfl.alpano.DistanceTest;
import ch.epfl.alpano.Math2Test;
import ch.epfl.alpano.PreconditionsTest;


@RunWith(Suite.class)
@SuiteClasses({
                AzimuthTest.class,
                DistanceTest.class,
                Math2Test.class,
                PreconditionsTest.class,
                ch.epfl.alpano.test2.AzimuthTest.class,
                ch.epfl.alpano.test2.DistanceTest.class,
                ch.epfl.alpano.test2.Math2Test.class,
                ch.epfl.alpano.test2.PreconditionsTest.class})

public class Week1TestSuite {

}
