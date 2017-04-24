package ch.epfl.alpano.gui;

import static org.junit.Assert.*;

import org.junit.Test;

public class FixedPointStringConverterTest {

    @Test
    public void exampleOfProfWorks() {
        FixedPointStringConverter c =
                new FixedPointStringConverter(1);

        assertEquals(120, (int)c.fromString("12"));
        assertEquals(123, (int)c.fromString("12.3"));
        assertEquals(123, (int)c.fromString("12.34"));
        assertEquals(124, (int)c.fromString("12.35"));
        assertEquals(124, (int)c.fromString("12.36789"));
        assertEquals("67.8", c.toString(678));
    }
}
