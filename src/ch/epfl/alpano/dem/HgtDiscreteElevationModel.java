package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

    private ShortBuffer source;
    private final int side = SAMPLES_PER_DEGREE + 1;
    private int lonIndex;
    private int latIndex;

    public HgtDiscreteElevationModel(File file) {
        final long l = file.length();
        final int limit =  2 * side * side;
        checkArgument(valid(file), "file name invalid");
        checkArgument(l == limit,  "file does not comply to byte restrictions");
        
        try (FileInputStream s = new FileInputStream(file)) {
            source = s.getChannel()
              .map(MapMode.READ_ONLY, 0, l)
              .asShortBuffer();
          } catch(IOException e) {
              throw new IllegalArgumentException("file invalid");
          }
    }

    private boolean valid(File file) {
        String n = file.getName();

        char ns = n.charAt(0);
        char ew = n.charAt(3);
        int lat, lon;

        try {        
            lat = Integer.parseInt(n.substring(1,3));
            lon = Integer.parseInt(n.substring(4,7));
        } catch(NumberFormatException e) {
            return false;
        }
        
        lonIndex = (ew == 'E' ? 1 : -1) * lon * SAMPLES_PER_DEGREE;
        latIndex = (ns == 'N' ? 1 : -1) * lat * SAMPLES_PER_DEGREE;
        
        boolean invalid = n.length() != 11
                || ns != 'N' &&  ns != 'S'
                || ns == 'N' &&  90 <= lat
                || ns == 'S' &&  90 <  lat
                || ew != 'E' &&  ew != 'W'
                || ew == 'E' && 180 <= lon
                || ew == 'W' && 180 <  lon
                || !n.substring(7, 11).equals(".hgt");

        return !invalid;
    }

    @Override
    public void close() throws Exception {
        source = null;
    }

    @Override
    public Interval2D extent() {
        Interval1D lon = new Interval1D(lonIndex, lonIndex + side);
        Interval1D lat = new Interval1D(latIndex, latIndex + side);
        return new Interval2D(lon, lat);
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y), "the HgtDEM does not contain the given index");
        
        int ySize = latIndex + side - y;
        int xSize = x - lonIndex;
        
        return source.get(ySize * side + xSize);
    }

}
