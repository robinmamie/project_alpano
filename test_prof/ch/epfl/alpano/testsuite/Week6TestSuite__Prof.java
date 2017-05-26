package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.PanoramaComputerTest__Prof;
import ch.epfl.alpano.PanoramaTest__Prof;

@RunWith(Suite.class)
@SuiteClasses({
    PanoramaComputerTest__Prof.class
    , PanoramaTest__Prof.class
    })

public class Week6TestSuite__Prof {}
