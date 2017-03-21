package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    Week1TestSuite__Global.class
    , Week2TestSuite__Global.class
    , Week3TestSuite__Global.class
    , Week4TestSuite__Global.class
    , Week5TestSuite__Global.class
    })

public class GlobalTestSuite__Global {}
