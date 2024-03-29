package ch.epfl.alpano.gui;

import ch.epfl.alpano.Panorama;
import javafx.beans.property.DoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Permet de dessiner un panorama à l'aide d'un peintre d'image.
 *
 * @author Robin Mamié
 */
public interface PanoramaRenderer {

	/**
	 * Dessine un panorama à l'aide d'un peintre d'image.
	 * 
	 * @param p  Le panorama à dessiner.
	 * @param iP Le peintre d'image définissant les règles de dessin.
	 * 
	 * @return Une image représentant le panorama selon le peintre d'image passé en
	 *         argument.
	 */
	static Image renderPanorama(final Panorama p, final ImagePainter iP, final DoubleProperty status) {
		final var i = new WritableImage(p.parameters().width(), p.parameters().height());
		final var pW = i.getPixelWriter();
		final var increment = 1d / i.getWidth();
		for (var x = 0; x < i.getWidth(); ++x) {
			for (var y = 0; y < i.getHeight(); ++y) {
				pW.setColor(x, y, iP.colorAt(x, y));
			}
			status.set(status.get() + increment);
		}
		return i;
	}
}
