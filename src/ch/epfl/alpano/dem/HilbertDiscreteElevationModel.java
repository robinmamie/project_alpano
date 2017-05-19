package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

public class HilbertDiscreteElevationModel implements DiscreteElevationModel {

    private final ShortBuffer source;
    private final Interval2D extent;

    public HilbertDiscreteElevationModel(int file) {
        File hilbertHGT;
        Interval1D iX, iY;
        switch (file) {
        case 1:
            hilbertHGT = new File("HIL01.hgt");
            iX = new Interval1D(6 * 3600, 6 * 3600 + HilbertCreator.N - 1);
            iY = new Interval1D(45 * 3600, 45 * 3600 + HilbertCreator.N - 1);
            this.extent = new Interval2D(iX, iY);
            break;
        case 2:
            hilbertHGT = new File("HIL02.hgt");
            iX = new Interval1D(6 * 3600 + HilbertCreator.N,
                    6 * 3600 + 2 * HilbertCreator.N - 1);
            iY = new Interval1D(45 * 3600, 45 * 3600 + HilbertCreator.N - 1);
            this.extent = new Interval2D(iX, iY);
            break;
        default:
            throw new IllegalArgumentException();
        }

        try (FileInputStream stream = new FileInputStream(hilbertHGT)) {
            source = stream.getChannel()
                    .map(READ_ONLY, 0, HilbertCreator.N_SQUARED * 2)
                    .asShortBuffer();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "The file is either invalid, corrupt, or not found.");
        }

    }

    @Override
    public Interval2D extent() {
        return extent;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y),
                "The HilbertDEM does not contain the given index.");
        int rx, ry, s, d = 0;
        for (s = HilbertCreator.N/2; s > 0; s /= 2) {
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

}
