package ch.epfl.alpano.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.epfl.alpano.gui.FixedPointStringConverterTest;
import ch.epfl.alpano.gui.LabeledListStringConverterTest;
import ch.epfl.alpano.gui.LabelizerTest;


@RunWith(Suite.class)
@SuiteClasses({
    LabeledListStringConverterTest.class,
    FixedPointStringConverterTest.class,
    LabelizerTest.class})

public class Week9TestSuite {

}
