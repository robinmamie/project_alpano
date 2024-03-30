package ch.epfl.alpano.draw;

import java.io.File;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.gui.ChannelPainter;
import ch.epfl.alpano.gui.ImagePainter;
import ch.epfl.alpano.gui.PanoramaRenderer;
import javafx.embed.swing.SwingFXUtils;

/**
 * Dessine un panorama en niveaux de gris.
 *
 * @author Robin Mamié
 */
final class DrawPanoramaGray extends AbstractDrawPanorama {

	private static final Logger logger = LogManager.getLogger(DrawPanoramaGray.class);

	public static void main(final String[] args) throws Exception {
		final var start = System.nanoTime();

		final var p = panorama();

		final var gray = ChannelPainter.maxDistanceToNeighbors(p).sub(500).div(4500).clamp().invert();

		final var distance = (ChannelPainter) p::distanceAt;
		final var opacity = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

		final var l = ImagePainter.gray(gray, opacity);

		final var i = PanoramaRenderer.renderPanorama(p, l, null);
		ImageIO.write(SwingFXUtils.fromFXImage(i, null), "png", new File("niesen-profile.png"));

		final var stop = System.nanoTime();
		logger.info("DrawPanoramaGray took {} ms.", (stop - start) * 1e-6);
	}
}
