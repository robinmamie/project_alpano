package ch.epfl.alpano.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class LabeledListStringConverterTest {

	@Test
	void exampleOfProfWorks() {
		final var c = new LabeledListStringConverter("zéro", "un", "deux");
		assertEquals(2, (int) c.fromString("deux"));
		assertEquals("zéro", c.toString(0));
	}

}
