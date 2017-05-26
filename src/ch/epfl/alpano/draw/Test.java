package ch.epfl.alpano.draw;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ch.epfl.alpano.summit.GazetteerParser;
import ch.epfl.alpano.summit.Summit;

public class Test {

    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();
        List<Summit> list = GazetteerParser.readSummitsFrom(new File("alps.txt"));
        list.forEach(System.out::println);
        long stop = System.nanoTime();
        System.out.printf("%nThe test file took %.3f ms to run.%n",
                (stop - start) * 1e-6);
    }

}
