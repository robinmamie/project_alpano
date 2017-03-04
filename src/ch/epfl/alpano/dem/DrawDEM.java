package ch.epfl.alpano.dem;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.toRadians;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

public final class DrawDEM {
    @SuppressWarnings("resource")
    public static void main(String[] args)
            throws IOException {

        long startTime = System.nanoTime();

        DiscreteElevationModel dDEM1 =
                new WavyDEM(new Interval2D(new Interval1D(0, 50),
                        new Interval1D(0, 100)));
        DiscreteElevationModel dDEM2 =
                new WavyDEM(new Interval2D(new Interval1D(50, 100),
                        new Interval1D(0, 100)));
        DiscreteElevationModel dDEM =
                dDEM1.union(dDEM2);
        ContinuousElevationModel cDEM =
                new ContinuousElevationModel(dDEM);

        int size = 300;
        double scale = (100d / 3600d) / (size - 1);
        BufferedImage elI =
                new BufferedImage(size, size, TYPE_INT_RGB);
        BufferedImage slI =
                new BufferedImage(size, size, TYPE_INT_RGB);
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                GeoPoint p = new GeoPoint(toRadians(x * scale),
                        toRadians(y * scale));
                double el = cDEM.elevationAt(p);
                elI.setRGB(x, y, gray(el / 1000d));

                double sl = cDEM.slopeAt(p);
                slI.setRGB(x, y, gray(sl / (PI / 2d)));
            }
        }

        ImageIO.write(elI, "png", new File("elevation.png"));
        ImageIO.write(slI, "png", new File("slope.png"));

        long endTime = System.nanoTime();
        System.out.println("Took "+(endTime - startTime) + " ns");
        System.out.println("Took "+(endTime - startTime)/1e6 + " ms");
    }

    private static int gray(double v) {
        double clampedV = max(0, min(v, 1));
        int gray = (int) (255.9999 * clampedV);
        return (gray << 16) | (gray << 8) | gray;
    }
}
