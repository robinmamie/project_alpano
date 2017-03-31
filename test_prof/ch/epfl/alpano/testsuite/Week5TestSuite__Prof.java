package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.PanoramaParametersTest__Prof;
import ch.epfl.alpano.summit.GazetteerParserTest__Prof;
import ch.epfl.alpano.summit.SummitTest__Prof;

@RunWith(Suite.class)
@SuiteClasses({
    GazetteerParserTest__Prof.class
    , SummitTest__Prof.class
    , PanoramaParametersTest__Prof.class
    })

public class Week5TestSuite__Prof {}
