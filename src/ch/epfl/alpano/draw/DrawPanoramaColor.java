package ch.epfl.alpano.draw;

import static java.lang.Math.PI;
import static java.lang.Math.toRadians;

import java.io.File;

import javax.imageio.ImageIO;

import ch.epfl.alpano.GeoPoint;
import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.HgtDiscreteElevationModel;
import ch.epfl.alpano.gui.ChannelPainter;
import ch.epfl.alpano.gui.ImagePainter;
import ch.epfl.alpano.gui.PanoramaRenderer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Dessine un panorama en couleurs.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
final class DrawPanoramaColor {
    final static File HGT_FILE = new File("N46E007.hgt");

    final static int IMAGE_WIDTH = 500;
    final static int IMAGE_HEIGHT = 200;

    final static double ORIGIN_LON = toRadians(7.65);
    final static double ORIGIN_LAT = toRadians(46.73);
    final static int ELEVATION = 600;
    final static double CENTER_AZIMUTH = toRadians(180);
    final static double HORIZONTAL_FOV = toRadians(60);
    final static int MAX_DISTANCE = 100_000;

    // final static int IMAGE_WIDTH = 5000;
    // final static int IMAGE_HEIGHT = 2000;
    //
    // final static double ORIGIN_LON = toRadians(7.1302);
    // final static double ORIGIN_LAT = toRadians(46.86575);
    // final static int ELEVATION = 615;
    // final static double CENTER_AZIMUTH = toRadians(170);
    // final static double HORIZONTAL_FOV = toRadians(60);
    // final static int MAX_DISTANCE = 300_000;

    final static PanoramaParameters PARAMS = new PanoramaParameters(
            new GeoPoint(ORIGIN_LON, ORIGIN_LAT), ELEVATION, CENTER_AZIMUTH,
            HORIZONTAL_FOV, MAX_DISTANCE, IMAGE_WIDTH, IMAGE_HEIGHT);

    public static void main(String[] as) throws Exception {
        long start = System.nanoTime();
        DiscreteElevationModel dDEM = new HgtDiscreteElevationModel(HGT_FILE);
        ContinuousElevationModel cDEM = new ContinuousElevationModel(dDEM);
        Panorama p = new PanoramaComputer(cDEM).computePanorama(PARAMS);

        ChannelPainter distance = p::distanceAt;
        ChannelPainter slope = p::slopeAt;

        ChannelPainter h = distance.div(100_000).cycle().mul(360);
        ChannelPainter s = distance.div(200_000).clamp().invert();
        ChannelPainter b = slope.mul(2).div((float) PI).invert().mul(0.7f)
                .add(0.3f);
        ChannelPainter o = distance
                .map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

        ImagePainter l = ImagePainter.hsb(h, s, b, o);

        Image i = PanoramaRenderer.renderPanorama(p, l);
        ImageIO.write(SwingFXUtils.fromFXImage(i, null), "png",
                new File("niesen-shaded.png"));
        long stop = System.nanoTime();
        System.out.printf("DrawPanoramaColor took %.3f ms.%n",
                (stop - start) * 1e-6);
    }
}
