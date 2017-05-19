package ch.epfl.alpano.gui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.scene.Node;

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
        long start = System.nanoTime();
        List<Node> nodes = new Labelizer(
                new ContinuousElevationModel(
                        new SuperHgtDiscreteElevationModel()),
                GazetteerParser.readSummitsFrom(new File("alps.txt"))).labels(
                        PredefinedPanoramas.NIESEN.panoramaParameters());
        long stop = System.nanoTime();
        nodes.forEach(System.out::println);
        System.out.printf("Number of summits: %d%n", nodes.size() / 2);
        System.out.printf("Took %.3f s to load.", (stop - start) * 1e-9);
    }

}
