package ch.epfl.alpano.gui;

import static org.junit.Assert.*;

import org.junit.Test;

public class LabeledListStringConverterTest {

    @Test
    public void exampleOfProfWorks() {
        LabeledListStringConverter c =
                new LabeledListStringConverter("zéro", "un", "deux");
        assertEquals(2, (int)c.fromString("deux"));
        assertEquals("zéro", c.toString(0));    
    }
}
