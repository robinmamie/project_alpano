package ch.epfl.alpano.gui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;
import javafx.scene.Node;

public class LabelizerTest {
    
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    
    @Test
    public void labelizerWorks() throws IOException {
        List<Summit> l = GazetteerParser.readSummitsFrom(new File("alps.txt"));
        List<Node> nodes = new Labelizer(
                new ContinuousElevationModel(
                        new SuperHgtDiscreteElevationModel()),
                l).labels(PredefinedPanoramas.NIESEN.panoramaParameters());
        for(Node n: nodes)
            System.out.println(n.toString());
    }
    
}
