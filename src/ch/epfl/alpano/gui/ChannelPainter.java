package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Math2.floorMod;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.Panorama;

/**
 * Interface fonctionnelle permettant de définir un peintre de canal. Utile pour
 * ensuite définir une image qui elle dessinera le panorama.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
@FunctionalInterface
public interface ChannelPainter {

    /**
     * Valeur du peintre de canal aux index spécifiés.
     * 
     * @param x
     *            L'index horizontal.
     * @param y
     *            L'index vertical.
     * 
     * @return La valeur (float) du peintre de canal aux index <i>(x,y)</i>.
     */
    float valueAt(int x, int y);

    static ChannelPainter maxDistanceToNeighbors(Panorama p) {
        return (x, y) -> max(
                max(p.distanceAt(x - 1, y, 0), p.distanceAt(x + 1, y, 0)),
                max(p.distanceAt(x, y - 1, 0), p.distanceAt(x, y + 1, 0)))
                - p.distanceAt(x, y);
    }

    /**
     * Permet d'additionner une certaine valeur au peintre de canal.
     * 
     * @param n
     *            La valeur à additionner.
     * 
     * @return Un nouveau peintre de canal additionné avec l'argument <i>n</i>.
     */
    default ChannelPainter add(float n) {
        return (x, y) -> valueAt(x, y) + n;
    }

    /**
     * Permet de soustraire une certaine valeur au peintre de canal.
     * 
     * @param n
     *            La valeur à soustraire.
     * 
     * @return Un nouveau peintre de canal soustrait par l'argument <i>n</i>.
     */
    default ChannelPainter sub(float n) {
        return (x, y) -> valueAt(x, y) - n;
    }

    /**
     * Permet de multiplier une certaine valeur au peintre de canal.
     * 
     * @param n
     *            La valeur à multiplier.
     * 
     * @return Un nouveau peintre de canal multiplié avec l'argument <i>n</i>.
     */
    default ChannelPainter mul(float n) {
        return (x, y) -> valueAt(x, y) * n;
    }

    /**
     * Permet de diviser une certaine valeur au peintre de canal.
     * 
     * @param n
     *            La valeur à diviser.
     * 
     * @return Un nouveau peintre de canal divisé par l'argument <i>n</i>.
     */
    default ChannelPainter div(float n) {
        return (x, y) -> valueAt(x, y) / n;
    }

    /**
     * Permet d'appliquer une lambda au peintre de canal.
     * 
     * @param f
     *            Le lambda à appliquer.
     * 
     * @return Un nouveau peintre de canal auquel un lambda a été appliqué.
     */
    default ChannelPainter map(DoubleUnaryOperator f) {
        return (x, y) -> (float) f.applyAsDouble(valueAt(x, y));
    }

    /**
     * "Inverse" le peintre de canal.
     * 
     * @return Le peintre de canal actualisé - inversé.
     */
    default ChannelPainter invert() {
        return (x, y) -> 1 - valueAt(x, y);
    }

    /**
     * Retourne un peintre de canal n'ayant que des valeurs entre 0 et 1. Les
     * autres valeurs sont arrondies à la valeur valide la pluis proche.
     * 
     * @return Le peintre de canal actualisé.
     */
    default ChannelPainter clamp() {
        return (x, y) -> max(0, min(valueAt(x, y), 1));
    }

    /**
     * Retourne un peintre de canal n'ayant que des valeurs entre 0 et 1. Les
     * valeurs ne s'y trouvant pas déjà sont obtenues en gardant leur partie
     * décimal (floorMod).
     * 
     * @return Le peintre de canal actualisé.
     */
    default ChannelPainter cycle() {
        return (x, y) -> (float) floorMod(valueAt(x, y), 1);
    }
}
