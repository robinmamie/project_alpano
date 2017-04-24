package ch.epfl.alpano.draw;

import java.io.File;
import java.io.IOException;

import ch.epfl.alpano.summit.GazetteerParser;

public class Test {

    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();
        GazetteerParser.readSummitsFrom(new File("alps.txt"));
        long stop = System.nanoTime();
        System.out.printf("%nThe test file took %.3f ms to run.%n",
                (stop - start) * 1e-6);
    }

}
