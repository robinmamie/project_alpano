package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.testsuite.Week1TestSuite;
import ch.epfl.alpano.testsuite.Week2TestSuite;


@RunWith(Suite.class)
@SuiteClasses({
                Week1TestSuite.class,
                Week2TestSuite.class,
                Week3TestSuite.class,
                Week4TestSuite.class,
                Week5TestSuite.class})

public class AlpanoGlobalTestSuite {

}
