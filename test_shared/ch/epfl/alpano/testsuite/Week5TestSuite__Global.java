package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.summit.GazetteerParserTest__Global;
import ch.epfl.alpano.summit.SummitTest__Global;

@RunWith(Suite.class)
@SuiteClasses({
    GazetteerParserTest__Global.class
    , SummitTest__Global.class
    })

public class Week5TestSuite__Global {}
