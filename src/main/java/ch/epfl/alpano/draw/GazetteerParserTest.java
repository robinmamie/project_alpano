package ch.epfl.alpano.draw;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.summit.GazetteerParser;

public class GazetteerParserTest {

	private static final Logger logger = LogManager.getLogger(GazetteerParserTest.class);

	public static void main(final String[] args) throws IOException {
		final var start = System.nanoTime();
		final var file = new File(GazetteerParserTest.class.getResource("/alps.txt").getFile());
		final var list = GazetteerParser.readSummitsFrom(file);
		list.forEach(logger::info);
		final var stop = System.nanoTime();
		logger.info("The test file took {} ms to run.", (stop - start) * 1e-6);
	}

}
