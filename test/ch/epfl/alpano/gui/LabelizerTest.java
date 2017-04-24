package ch.epfl.alpano.gui;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;

public class LabelizerTest {

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

    @Test
    public void labelizerWorks() throws IOException {
        new Labelizer(
                new ContinuousElevationModel(
                        new SuperHgtDiscreteElevationModel()),
                GazetteerParser.readSummitsFrom(new File("alps.txt")))
                        .labels(PredefinedPanoramas.NIESEN.panoramaParameters())
                        .forEach(System.out::println);
    }

}
