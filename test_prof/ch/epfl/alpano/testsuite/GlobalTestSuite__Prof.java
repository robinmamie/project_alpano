package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    Week1TestSuite__Prof.class
    , Week2TestSuite__Prof.class
    , Week3TestSuite__Prof.class
    , Week4TestSuite__Prof.class
    , Week5TestSuite__Prof.class
    })

public class GlobalTestSuite__Prof {}
