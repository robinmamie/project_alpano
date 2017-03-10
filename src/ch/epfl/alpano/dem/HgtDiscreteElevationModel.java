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

    private static final int SIDE = SAMPLES_PER_DEGREE + 1;
    private final FileInputStream stream;
    private ShortBuffer source;

    private final int lonIndex;
    private final int latIndex;
    private final Interval2D ext;

    public HgtDiscreteElevationModel(File file) {
        String n   = file.getName();
        String fni = "file name invalid: ";

        char ns = n.charAt(0);
        char ew = n.charAt(3);
        int lat, lon;

        try {        
            lat = Integer.parseInt(n.substring(1,3));
            lon = Integer.parseInt(n.substring(4,7));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(fni + "does not contain numbers at the right places");
        }

        lonIndex = (ew == 'E' ? 1 : -1) * lon * SAMPLES_PER_DEGREE;
        latIndex = (ns == 'N' ? 1 : -1) * lat * SAMPLES_PER_DEGREE;

        checkArgument(n.length() == 11
                , fni + "too short or too long");
        checkArgument(ns == 'N' ||  ns == 'S'
                , fni + "isn't defined for North or South");
        checkArgument(ns != 'N' ||  lat < 90
                , fni + "northern latitude impossible");
        checkArgument(lat <= 90
                , fni + "latitude impossible");
        checkArgument(ew == 'E' ||  ew == 'W'
                , fni + "isn't defined for East or West");
        checkArgument(ew != 'E' || lon < 180
                , fni + "eastern longitude impossible");
        checkArgument(lon <= 180
                , fni + "longitude impossible");
        checkArgument(n.substring(7, 11).equals(".hgt")
                , fni + "doesn't end with correct extension");


        final long l = file.length();

        checkArgument(l == 2 * SIDE * SIDE
                ,  "file invalid: does not comply to byte restrictions");

        try {
            stream = new FileInputStream(file);
            source = stream.getChannel()
                    .map(MapMode.READ_ONLY, 0, l)
                    .asShortBuffer();
        } catch(IOException e) {
            throw new IllegalArgumentException("file invalid");
        }

        this.ext = extent();
    }

    @Override
    public void close() throws Exception {
        stream.close();
        source = null;
    }

    @Override
    public Interval2D extent() {
        Interval1D lon = new Interval1D(lonIndex, lonIndex + SIDE);
        Interval1D lat = new Interval1D(latIndex, latIndex + SIDE);
        return new Interval2D(lon, lat);
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(ext.contains(x, y), "the HgtDEM does not contain the given index");

        int ySize = latIndex + SIDE - 1 - y;
        int xSize = x - lonIndex;

        return source.get(ySize * SIDE + xSize);
    }

}
