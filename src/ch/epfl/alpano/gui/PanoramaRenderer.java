package ch.epfl.alpano.gui;

import ch.epfl.alpano.Panorama;
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
    static Image renderPanorama(Panorama p, ImagePainter iP) {
        WritableImage i = new WritableImage(p.parameters().width(),
                p.parameters().height());
        PixelWriter pW = i.getPixelWriter();
        for (int y = 0; y < i.getHeight(); ++y)
            for (int x = 0; x < i.getWidth(); ++x)
                pW.setColor(x, y, iP.colorAt(x, y));
        return i;
    }
}
