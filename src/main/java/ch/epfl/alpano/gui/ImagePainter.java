package ch.epfl.alpano.gui;

import static java.lang.Float.POSITIVE_INFINITY;
import static java.lang.Math.PI;

import ch.epfl.alpano.Panorama;
import javafx.scene.paint.Color;

/**
 * Interface fonctionnelle permettant de définir un peintre d'image. Permet
 * ensuite de dessiner le panorama d'une façon spécifique.
 *
 * @author Robin Mamié
 */
@FunctionalInterface
public interface ImagePainter {

	/**
	 * Couleur du peintre d'image aux index spécifiés.
	 * 
	 * @param x L'index horizontal.
	 * @param y L'index vertical.
	 * 
	 * @return La couleur du peintre d'image aux index <i>(x,y)</i>.
	 */
	abstract Color colorAt(int x, int y);

	/**
	 * Permet d'obtenir la valeur TSV (HSB) de l'image.
	 * 
	 * @param h La teinte de chaque pixel de l'image (hue).
	 * @param s La saturation de chaque pixel de l'image.
	 * @param b La luminosité (brightness) de chaque pixel de l'image.
	 * @param o L'opacité de chaque pixel de l'image.
	 * 
	 * @return Une image définie à l'aide des valeurs TSV (HSB).
	 */
	static ImagePainter hsb(final ChannelPainter h, final ChannelPainter s, final ChannelPainter b,
			final ChannelPainter o) {
		return (x, y) -> Color.hsb(h.valueAt(x, y), s.valueAt(x, y), b.valueAt(x, y), o.valueAt(x, y));
	}

	/**
	 * Permet d'obtenir la valeur en niveaux de gris de l'image.
	 * 
	 * @param g Le niveau de gris de chaque pixel de l'image.
	 * @param o L'opacité de chaque pixel de l'image.
	 * 
	 * @return Une image définie à l'aide de niveaux de gris.
	 */
	static ImagePainter gray(final ChannelPainter g, final ChannelPainter o) {
		return (x, y) -> Color.gray(g.valueAt(x, y), o.valueAt(x, y));
	}

	/**
	 * Computes the standard colouring of a Panorama.
	 * 
	 * @param panorama The Panorama which we want to colour.
	 * 
	 * @return The corresponding standard colouring of the given Panorama.
	 */
	static ImagePainter stdPanorama(final Panorama panorama) {
		final var distance = (ChannelPainter) panorama::distanceAt;
		final var slope = (ChannelPainter) panorama::slopeAt;
		final var h = distance.div(100_000).cycle().mul(360);
		final var s = distance.div(200_000).clamp().invert();
		final var b = slope.mul(2).div((float) PI).invert().mul(0.7f).add(0.3f);
		final var o = distance.map(d -> d == POSITIVE_INFINITY ? 0 : 1);
		return hsb(h, s, b, o);
	}

	/**
	 * Returns the outline of the Panorama.
	 * 
	 * @param panorama
	 * @return
	 */
	static ImagePainter outlinePanorama(final Panorama panorama) {
		final var gray = ChannelPainter.maxDistanceToNeighbors(panorama).sub(500).div(4500).clamp().invert();

		final var distance = (ChannelPainter) panorama::distanceAt;
		final var opacity = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

		return gray(gray, opacity);
	}

	static ImagePainter outlineWithLakesPanorama(Panorama panorama) {
		final var gray = ChannelPainter.maxDistanceToNeighbors(panorama).sub(500).div(4500).clamp().invert();
		final var distance = (ChannelPainter) panorama::distanceAt;
		final var slopes = ChannelPainter.totalSlopeOfNeigbors(panorama);

		final var h = (ChannelPainter) (x, y) -> slopes.valueAt(x, y) == 0 ? 210 : gray.valueAt(x, y);
		final var s = slopes.map(v -> v == 0 ? 0.77f : 0);
		final var b = (ChannelPainter) (x, y) -> slopes.valueAt(x, y) == 0 ? 0.4f : gray.valueAt(x, y);
		final var o = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

		return hsb(h, s, b, o);
	}
}
