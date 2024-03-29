package ch.epfl.alpano.draw;

import java.io.File;
import java.io.IOException;

import ch.epfl.alpano.summit.GazetteerParser;

public class Test {

	public static void main(final String[] args) throws IOException {
		final var start = System.nanoTime();
		final var list = GazetteerParser.readSummitsFrom(new File("alps.txt"));
		list.forEach(System.out::println);
		final var stop = System.nanoTime();
		System.out.printf("%nThe test file took %.3f ms to run.%n", (stop - start) * 1e-6);
	}

}
