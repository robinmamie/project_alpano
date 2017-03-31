package ch.epfl.alpano;

import static ch.epfl.alpano.Math2.PI2;
import static ch.epfl.alpano.Math2.angularDistance;
import static ch.epfl.alpano.Math2.floorMod;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.PI;
import static java.lang.Math.abs;

/**
 * Fournit des opérations nécessaires aux différents calculs de l'azimut.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Azimuth {

    /**
     * Vérifie si l'azimut est sous forme canonique (compris entre 0 inclus et
     * 2Pi exclu).
     * 
     * @param azimuth
     *            L'azimut dont on souhaite savoir s'il est sous forme
     *            canonique.
     * 
     * @return True si l'azimut est sous forme canonique.
     */
    static boolean isCanonical(double azimuth) {
        return 0 <= azimuth && azimuth < PI2;
    }

    /**
     * Transforme l'azimut en valeur canonique (compris entre 0 inclus et 2Pi
     * exclu).
     * 
     * @param azimuth
     *            L'azimut dont on veut connaître la forme canonique.
     * 
     * @return La forme canonique de l'azimut passé en argument.
     */
    static double canonicalize(double azimuth) {
        return floorMod(azimuth, PI2);
    }

    /**
     * Transforme un azimut en angle mathématique (sens antihoraire).
     * 
     * @param azimuth
     *            L'azimut dont on veut connaître la valeur en tant qu'angle
     *            mathématique.
     * 
     * @return L'angle mathématique correspondant à l'azimut passé en argument.
     * 
     * @throws IllegalArgumentException
     *             si l'azimut donné n'est pas sous forme canonique.
     */
    static double toMath(double azimuth) {
        checkArgument(isCanonical(azimuth),
                "The given azimuth is not in canonical form.");
        return canonicalize(PI2 - azimuth);
    }

    /**
     * Transforme un angle mathématique en azimut (sens horaire).
     * 
     * @param angle
     *            L'angle mathématique dont on veut connaître la valeur en tant
     *            qu'azimut.
     * 
     * @return L'azimut correspondant à l'angle mathématique passé en argument.
     * 
     * @throws IllegalArgumentException
     *             si l'azimut donné n'est pas sous forme canonique.
     */
    static double fromMath(double angle) {
        checkArgument(isCanonical(angle),
                "The given angle is not in canonical form.");
        return canonicalize(PI2 - angle);
    }

    /**
     * Retourne une chaîne de caractères correspondant à l'octant dans lequel se
     * trouve l'azimut.
     * 
     * @param azimuth
     *            L'azimut qui servira au calcul de l'octant.
     * @param n
     *            Chaîne de caractère pour le nord.
     * @param e
     *            Chaîne de caractère pour l'est.
     * @param s
     *            Chaîne de caractère pour le sud.
     * @param w
     *            Chaîne de caractère pour l'ouest.
     * 
     * @return La chaîne de caractère correspondant à l'octant dans lequel se
     *         trouve l'azimut passé en argument.
     * 
     * @throws IllegalArgumentException
     *             si l'azimut donné n'est pas sous forme canonique.
     */
    static String toOctantString(double azimuth, String n, String e, String s,
            String w) {
        checkArgument(isCanonical(azimuth),
                "The given azimuth is not in canonical form.");

        StringBuilder answer = new StringBuilder();
        final double fieldSize = 3 * PI / 8;

        if (abs(angularDistance(azimuth, 0)) < fieldSize)
            answer.append(n);
        else if (abs(angularDistance(azimuth, PI)) < fieldSize)
            answer.append(s);

        if (abs(angularDistance(azimuth, PI / 2)) < fieldSize)
            answer.append(e);
        else if (abs(angularDistance(azimuth, 3 * PI / 2)) < fieldSize)
            answer.append(w);

        return answer.toString();
    }

}
