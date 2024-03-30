package ch.epfl.alpano.draw;

import static java.lang.Math.PI;

import java.io.File;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.epfl.alpano.gui.ChannelPainter;
import ch.epfl.alpano.gui.ImagePainter;
import ch.epfl.alpano.gui.PanoramaRenderer;
import javafx.embed.swing.SwingFXUtils;

/**
 * Dessine un panorama en couleurs.
 *
 * @author Robin Mamié
 */
final class DrawPanoramaColor extends AbstractDrawPanorama {

	private static final Logger logger = LogManager.getLogger(DrawPanoramaColor.class);

	public static void main(final String[] args) throws Exception {
		final var start = System.nanoTime();

		final var p = panorama();

		final var distance = (ChannelPainter) p::distanceAt;
		final var slope = (ChannelPainter) p::slopeAt;

		final var h = distance.div(100_000).cycle().mul(360);
		final var s = distance.div(200_000).clamp().invert();
		final var b = slope.mul(2).div((float) PI).invert().mul(0.7f).add(0.3f);
		final var o = distance.map(d -> d == Float.POSITIVE_INFINITY ? 0 : 1);

		final var l = ImagePainter.hsb(h, s, b, o);
		final var i = PanoramaRenderer.renderPanorama(p, l, null);
		ImageIO.write(SwingFXUtils.fromFXImage(i, null), "png", new File("niesen-shaded.png"));

		final var stop = System.nanoTime();
		logger.info("DrawPanoramaColor took {} ms.", (stop - start) * 1e-6);
	}
}
