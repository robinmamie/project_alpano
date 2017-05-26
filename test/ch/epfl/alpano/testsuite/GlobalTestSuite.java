package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
                Week2TestSuite.class,
                Week5TestSuite.class,
                Week9TestSuite.class})

public class GlobalTestSuite {

}
