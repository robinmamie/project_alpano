package ch.epfl.alpano.draw;

import static ch.epfl.alpano.gui.PredefinedPanoramas.COURTEPIN;
import static ch.epfl.alpano.gui.PredefinedPanoramas.FINSTER;
import static ch.epfl.alpano.gui.PredefinedPanoramas.JURA;
import static ch.epfl.alpano.gui.PredefinedPanoramas.NIESEN;
import static ch.epfl.alpano.gui.PredefinedPanoramas.PELICAN;
import static ch.epfl.alpano.gui.PredefinedPanoramas.RACINE;
import static ch.epfl.alpano.gui.PredefinedPanoramas.SAUVABELIN;
import static ch.epfl.alpano.gui.PredefinedPanoramas.SEEDORF;
import static ch.epfl.alpano.gui.PredefinedPanoramas.ZIMMERWALD;
import static java.lang.Float.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javax.imageio.ImageIO.write;

import java.io.File;
import java.io.IOException;

import ch.epfl.alpano.Panorama;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.DiscreteElevationModel;
import ch.epfl.alpano.dem.SuperHgtDiscreteElevationModel;
import ch.epfl.alpano.gui.ChannelPainter;
import ch.epfl.alpano.gui.ImagePainter;
import ch.epfl.alpano.gui.PanoramaRenderer;
import javafx.scene.image.Image;

/**
 * Dessine plusieurs panoramas en couleurs.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
final class DrawUserPanoramaColor {
    private static long START;

    final static File HGT_FILE_46_7 = new File("N46E007.hgt"),
            HGT_FILE_46_6 = new File("N46E006.hgt"),
            HGT_FILE_45_7 = new File("N45E007.hgt"),
            HGT_FILE_45_6 = new File("N45E006.hgt");

    public static void main(String[] as) throws IOException {
        resetTime();
        DiscreteElevationModel dDEM = new SuperHgtDiscreteElevationModel();
        //DiscreteElevationModel dDEM = new
        //HgtDiscreteElevationModel(HGT_FILE_46_7);
        PanoramaComputer p = new PanoramaComputer(
                new ContinuousElevationModel(dDEM));
        System.out.printf("PanoramaComputer loaded in %.3f s.%n", getSec());
        outputImage(p.computePanorama(NIESEN.panoramaDisplayParameters()),
                "niesen-user.png");
        System.out.printf("Niesen drawn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(JURA.panoramaDisplayParameters()),
                "jura-user.png");
        System.out.printf("Jura drawn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(RACINE.panoramaDisplayParameters()),
                "racine-user.png");
        System.out.printf("Racine drawn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(FINSTER.panoramaDisplayParameters()),
                "finsteraarhorn-user.png");
        System.out.printf("Finsteraarhorn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(SAUVABELIN.panoramaDisplayParameters()),
                "sauvabelin-user.png");
        System.out.printf("Sauvabelin drawn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(PELICAN.panoramaDisplayParameters()),
                "pelican-user.png");
        System.out.printf("Pelican drawn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(COURTEPIN.panoramaDisplayParameters()),
                "courtepin-user.png");
        System.out.printf("Courtepin drawn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(SEEDORF.panoramaDisplayParameters()),
                "seedorf-user.png");
        System.out.printf("Seedorf drawn in %.3f s.%n", getSec());
        outputImage(p.computePanorama(ZIMMERWALD.panoramaDisplayParameters()),
                "zimmerwald-user.png");
        System.out.printf("Zimmerwald drawn in %.3f s, programm finished.%n",
                getSec());
    }

    private static void outputImage(Panorama p, String name)
            throws IOException {
        ChannelPainter distance = p::distanceAt;
        ChannelPainter slope = p::slopeAt;

        ChannelPainter h = distance.div(100_000).cycle().mul(360);
        ChannelPainter s = distance.div(200_000).clamp().invert();
        ChannelPainter b = slope.mul(2).div((float) PI).invert().mul(0.7f)
                .add(0.3f);
        ChannelPainter o = distance.map(d -> d == POSITIVE_INFINITY ? 0 : 1);

        ImagePainter l = ImagePainter.hsb(h, s, b, o);

        Image i = PanoramaRenderer.renderPanorama(p, l);
        write(fromFXImage(i, null), "png", new File(name));
    }
    
    private static void resetTime() {
        START = System.nanoTime();
    }

    private static double getSec() {
        double t = (System.nanoTime() - START) * 1e-9;
        resetTime();
        return t;
    }
}
