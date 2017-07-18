package ch.epfl.alpano.gui;

import ch.epfl.alpano.Panorama;

import javafx.beans.property.DoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Permet de dessiner un panorama à l'aide d'un peintre d'image.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface PanoramaRenderer {

    /**
     * Dessine un panorama à l'aide d'un peintre d'image.
     * 
     * @param p
     *            Le panorama à dessiner.
     * @param iP
     *            Le peintre d'image définissant les règles de dessin.
     * 
     * @return Une image représentant le panorama selon le peintre d'image passé
     *         en argument.
     */
    static Image renderPanorama(Panorama p, ImagePainter iP,
            DoubleProperty status) {
        WritableImage i = new WritableImage(p.parameters().width(),
                p.parameters().height());
        PixelWriter pW = i.getPixelWriter();
        double increment = 1d / i.getWidth();
        for (int x = 0; x < i.getWidth(); ++x) {
            for (int y = 0; y < i.getHeight(); ++y)
                pW.setColor(x, y, iP.colorAt(x, y));
            status.set(status.get() + increment);
        }
        return i;
    }
}
