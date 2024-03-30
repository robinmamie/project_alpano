package ch.epfl.alpano.draw;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Dessine un panorama test.
 *
 * @author Robin Mamié
 */
final class DrawPanorama extends AbstractDrawPanorama {

	private static final Logger logger = LogManager.getLogger(DrawPanorama.class);

	public static void main(final String[] args) throws Exception {
		final var start = System.nanoTime();

		final var p = panorama();
		final var i = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, TYPE_INT_RGB);

		for (var x = 0; x < IMAGE_WIDTH; ++x) {
			for (var y = 0; y < IMAGE_HEIGHT; ++y) {
				final var d = p.distanceAt(x, y);
				final var c = (d == Float.POSITIVE_INFINITY) ? 0x87_CE_EB : gray((d - 2_000) / 15_000);
				i.setRGB(x, y, c);
			}
		}
		ImageIO.write(i, "png", new File("niesen.png"));

		final var stop = System.nanoTime();
		logger.info("DrawPanorama took {} ms.", (stop - start) * 1e-6);
	}

	private static int gray(final double v) {
		final var clampedV = max(0, min(v, 1));
		final var gray = (int) (255.9999 * clampedV);
		return (gray << 16) | (gray << 8) | gray;
	}
}
