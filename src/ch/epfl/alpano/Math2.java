package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static java.lang.Double.POSITIVE_INFINITY;

import java.util.function.DoubleUnaryOperator;

/**
 * Fournit des outils mathématiques utiles à ce projet complémentaires à
 * java.Lang.Math.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 * @see java.lang.Math
 */
public interface Math2 {

    /**
     * La valeur 2*Pi.
     */
    double PI2 = 2 * PI;

    /**
     * La valeur Pi/2.
     */
    double HALF_PI = PI / 2;

    /**
     * La valeur 3*Pi/2.
     */
    double THREE_HALF_PI = 3 * PI / 2;

    /**
     * La valeur 3*Pi/8.
     */
    double THREE_EIGHTH_PI = 3 * PI / 8;

    /**
     * Calcule le carré d'un nombre à l'aide d'une multiplication.
     * 
     * @param x
     *            Le nombre dont on souhaite calculer le carré.
     * 
     * @return Le carré du nombre passé en argument.
     */
    static double sq(double x) {
        return x * x;
    }

    /**
     * Calcule le reste de la division entière par défaut.
     * 
     * @param x
     *            Le nombre dont on souhaite connaître le reste.
     * @param y
     *            Le diviseur utilisé pour calculer ce reste.
     * 
     * @return Le reste de la division entière par défaut de x par y.
     */
    static double floorMod(double x, double y) {
        return x - y * floor(x / y);
    }

    /**
     * Calcule le demi sinus verse.
     * 
     * @param x
     *            Le nombre dont on souhaite connaître le demi sinus verse.
     * 
     * @return Le demi sinus verse du nombre passé en argument.
     */
    static double haversin(double x) {
        return sq(sin(x / 2.0));
    }

    /**
     * Calcule la distance angulaire entre deux angles.
     * 
     * @param a1
     *            Premier angle utilisé pour le calcul de la distance angulaire.
     * @param a2
     *            Second angle utilisé pour le calcul de la distance angulaire.
     * 
     * @return La distance angulaire entre les deux angles passés en argument.
     */
    static double angularDistance(double a1, double a2) {
        return floorMod(a2 - a1 + PI, PI2) - PI;
    }

    /**
     * Permet d'interpoler linéairement deux valeurs.
     * 
     * @param y0
     *            Premier point de l'interpolation.
     * @param y1
     *            Second point de l'interpolation.
     * @param x
     *            Paramètre de l'interpolation. 0 retourne le premier argument
     *            et 1 le second.
     * 
     * @return La valeur désignée par x sur la droite interpolé à l'aide les
     *         deux premiers arguments.
     */
    static double lerp(double y0, double y1, double x) {
        return y0 + (y1 - y0) * x;
    }

    /**
     * Permet d'interpoler bilinéairement quatre valeurs.
     * 
     * @param z00
     *            Premier point de l'interpolation.
     * @param z10
     *            Deuxième point de l'interpolation.
     * @param z01
     *            Troisième point de l'interpolation.
     * @param z11
     *            Quatrième point de l'interpolation.
     * @param x
     *            Paramètre de l'interpolation. 0 retourne une valeur se
     *            trouvant sur la droite interpolée par le premier et le
     *            troisième arguments et 1 sur celle par le deuxième et le
     *            quatrième.
     * @param y
     *            Paramètre de l'interpolation. 0 retourne une valeur se
     *            trouvant sur la droite interpolée par le premier et le
     *            deuxième arguments et 1 sur celle par le troisième et le
     *            quatrième.
     * 
     * @return La valeur désignée par x et y sur le plan interpolé à l'aide des
     *         quatre premiers arguments.
     */
    static double bilerp(double z00, double z10, double z01, double z11,
            double x, double y) {
        return lerp(lerp(z00, z10, x), lerp(z01, z11, x), y);
    }

    /**
     * Trouve un intervalle dans lequel une fonction contient une racine.
     * 
     * @param f
     *            Fonction dont on souhaite trouver une racine.
     * @param minX
     *            Borne inférieure de la recherche.
     * @param maxX
     *            Borne supérieure de la recherche.
     * @param dX
     *            Taille de l'intervalle. Permet d'affiner une recherche si la
     *            valeur est petite.
     * 
     * @return La borne inférieure d'un intervalle de taille prédéfinie dans
     *         lequel la fonction contient une racine ou
     *         Double.POSITIVE_INFINITY si l'intervalle ne contient pas de
     *         racine.
     * 
     * @throws IllegalArgumentException
     *             si la borne supérieure est inférieure à la borne inférieure
     *             ou si le pas <i>dX</i> n'est pas strictement positif.
     */
    static double firstIntervalContainingRoot(DoubleUnaryOperator f,
            double minX, double maxX, double dX) {
        checkArgument(minX <= maxX && 0 < dX);
        while (minX <= maxX - dX) {
            if (f.applyAsDouble(minX) * f.applyAsDouble(minX + dX) <= 0)
                return minX;
            minX += dX;
        }
        return POSITIVE_INFINITY;
    }

    /**
     * Effectue une recherche dichotomique dans un intervalle contenant une
     * racine afin de trouver de trouver une valeur précise.
     * 
     * @param f
     *            Fonction dont on souhaite trouver une racine.
     * @param x1
     *            Borne inférieure de la recherche.
     * @param x2
     *            Borne supérieure de la recherche.
     * @param epsilon
     *            Tolérance d'erreur pour la valeur de retour.
     * 
     * @return Une valeur déterminant une racine de la fonction à epsilon près.
     * 
     * @throws IllegalArgumentException
     *             si la fonction ne contient pas de racine dans l'intervalle.
     */
    static double improveRoot(DoubleUnaryOperator f, double x1, double x2,
            double epsilon) {
        checkArgument(f.applyAsDouble(x1) * f.applyAsDouble(x2) <= 0,
                "The interval does not contain a root.");
        while (x2 - x1 > epsilon) {
            double mid = (x1 + x2) / 2.0;
            if (f.applyAsDouble(x1) * f.applyAsDouble(mid) <= 0)
                x2 = mid;
            else
                x1 = mid;
        }
        return x1;
    }

}
