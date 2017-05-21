package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ShortBuffer;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;
import ch.epfl.alpano.Preconditions;

public class HilbertDiscreteElevationModel implements DiscreteElevationModel {

    public final static int N = 8192;
    public final static int N_SQUARED = N * N;

    private final static int BASE_LON = SuperHgtDiscreteElevationModel.BASE_LON
            * DiscreteElevationModel.SAMPLES_PER_DEGREE;
    private final static int BASE_LAT = SuperHgtDiscreteElevationModel.BASE_LAT
            * DiscreteElevationModel.SAMPLES_PER_DEGREE;

    private final File hilbertHGT;
    private final ShortBuffer source;
    private final Interval2D extent;

    public HilbertDiscreteElevationModel(int x, int y) {
        // TODO Remove these magic numbers
        Preconditions.checkArgument(0 <= x && x < 3 && 0 <= y && y < 2,
                "Invalid .hhgt file.");
        hilbertHGT = new File(getFileName(x, y));
        Interval1D iX = new Interval1D(BASE_LON + x * N,
                BASE_LON + (x + 1) * N - 1);
        Interval1D iY = new Interval1D(BASE_LAT + y * N,
                BASE_LAT + (y + 1) * N - 1);
        this.extent = new Interval2D(iX, iY);

        if (!hilbertHGT.exists() || hilbertHGT.length() != N_SQUARED * 2) {
            try {
                createFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        try (FileInputStream stream = new FileInputStream(hilbertHGT)) {
            source = stream.getChannel().map(READ_ONLY, 0, N_SQUARED * 2)
                    .asShortBuffer();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "The file is either invalid, corrupt, or not found.");
        }

    }

    private String getFileName(int x, int y) {
        return String.format("alpano%d%d.hhgt", x, y);
    }

    @Override
    public Interval2D extent() {
        return extent;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y),
                "The HilbertDEM does not contain the given index.");
        x -= extent().iX().includedFrom();
        y -= extent().iY().includedFrom();
        int rx, ry, s, d = 0;
        for (s = N / 2; s > 0; s /= 2) {
            rx = (x & s) > 0 ? 1 : 0;
            ry = (y & s) > 0 ? 1 : 0;
            d += s * s * ((3 * rx) ^ ry);
            if (ry == 0) {
                if (rx == 1) {
                    x = s - 1 - x;
                    y = s - 1 - y;
                }

                // Swap x and y
                int t = x;
                x = y;
                y = t;
            }
        }
        return source.get(d);
    }

    private void createFile() throws FileNotFoundException, IOException {
        DiscreteElevationModel dem = new SuperHgtDiscreteElevationModel();
        try (FileOutputStream stream = new FileOutputStream(hilbertHGT)) {
            System.out.println("Creating the file " + hilbertHGT.getName());
            for (int i = 0; i < N_SQUARED; ++i) {
                int rx, ry, s, t = i, x = 0, y = 0;
                for (s = 1; s < N; s *= 2) {
                    rx = 1 & (t / 2);
                    ry = 1 & (t ^ rx);

                    // ROT
                    if (ry == 0) {
                        if (rx == 1) {
                            x = s - 1 - x;
                            y = s - 1 - y;
                        }

                        // Swap x and y
                        int temp = x;
                        x = y;
                        y = temp;
                    }

                    x += s * rx;
                    y += s * ry;
                    t /= 4;
                }
                x += BASE_LON;
                y += BASE_LAT;
                short elevation = dem.extent().contains(x, y)
                        ? (short) dem.elevationSample(x, y) : 0;
                byte[] b = new byte[2];
                b[0] = (byte) (elevation >> 8);
                b[1] = (byte) (elevation & 0xff);
                stream.write(b);
            }
            System.out.println(hilbertHGT.getName() + " created.");
        }
    }

}
