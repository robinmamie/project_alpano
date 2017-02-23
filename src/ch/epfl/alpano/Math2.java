package ch.epfl.alpano;

import java.util.function.DoubleUnaryOperator;
import static ch.epfl.alpano.Preconditions.checkArgument;


/**
 * Fournit des outils mathématiques complémentaires à java.Lang.Math.
 * 
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface Math2 {

    
    /**
     * La valeur 2*Pi.
     */
    double PI2 = 2*Math.PI;
    
    
    /**
     * Calcule le carré d'un nombre.
     * 
     * @param x
     *          un nombre
     *          
     * @return le carré de x
     */
    static double sq(double x) {
        return x*x;
    }
    
    
    /**
     * Calcule le reste de la division entière par défaut.
     * 
     * @param x
     *          un nombre
     * @param y
     *          le diviseur
     *          
     * @return le reste de la division entière par défaut de x par y
     * 
     * @throws IllegalArgumentException
     *          si y est égal à zéro
     */
    static double floorMod(double x, double y) {
        checkArgument(y != 0, "division by zero");
        return x - y * Math.floor(x / y);
    }
    
    
    /**
     * Calcule le demi sinus verse.
     * 
     * @param x
     *          un nombre
     *          
     * @return le demi sinus verse de x
     */
    static double haversin(double x) {
        return Math.pow(Math.sin(x / 2), 2);
    }
    
    
    /**
     * Calcule la distance angulaire entre deux angles.
     * 
     * @param a1
     *          le premier angle
     * @param a2
     *          le second angle
     *          
     * @return la distance angulaire entre a1 et a2
     */
    static double angularDistance(double a1, double a2) {
        return floorMod(a2 - a1 + Math.PI, PI2) - Math.PI;
    }
    
    
    /**
     * Permet d'interpoler linéairement.
     * 
     * @param y0
     *          premier point
     * @param y1
     *          second point
     * @param x
     *          paramètre, 0 retourne y0 et 1 y1
     *          
     * @return la valeur interpolée par la droite tracée par y0 et y1 et désignée par x
     */
    static double lerp(double y0, double y1, double x) {
        return y0 + (y1 - y0) * x;
    }
    
    
    /**
     * Permet d'interpoler bilinéairement.
     * 
     * @param z00 
     *          premier point
     * @param z10
     *          deuxième point
     * @param z01
     *          troisième point
     * @param z11
     *          quatrième point
     * @param x
     *          premier paramètre
     * @param y
     *          second paramètre
     *          
     * @return la valeur interpolée par le plan désigné par les points
     *         donnée en paramètre désignée par x et y
     */
    static double bilerp(double z00, double z10, double z01, double z11, double x, double y) {
        return lerp(lerp(z00, z10, x), lerp(z01, z11, x), y);
    }
    
    
    /**
     * Trouve un intervale contenant un zéro.
     * 
     * @param f
     *          fonction 
     * @param minX
     *          borne inférieure de la recherche
     * @param maxX
     *          borne supérieure de la recherche
     * @param dX
     *          distance entre chaque intervalle
     * 
     * @return la borne inférieure d'un intervalle contenant un zéro à moins de dX 
     */
    static double firstIntervalContainingRoot(DoubleUnaryOperator f, double minX, double maxX, double dX) {
        while(minX <= maxX - dX) {
            if(f.applyAsDouble(minX)*f.applyAsDouble(minX + dX) <= 0)
                return minX;
            minX += dX;
        }
        return Double.POSITIVE_INFINITY;
    }
    
    
    /**
     * Effectue une recherche dichotomique dans un intervalle afin de trouver un zéro.
     * 
     * @param f
     *          fonction
     * @param x1
     *          borne inférieure de la recherche
     * @param x2
     *          borne supérieure de la recherche
     * @param epsilon
     *          tolérance d'erreur pour le retour
     *          
     * @return une position à moins d'epsilon d'un zéro
     * 
     * @throws IllegalArgumentException
     *          ssi l'intervalle ne contient pas de zéro
     */
    static double improveRoot(DoubleUnaryOperator f, double x1, double x2, double epsilon) {
        // Limite définie à 0.01 afin d'avoir un écart convenable
        double limit = 0.01
                , inf = firstIntervalContainingRoot(f, x1, x2, limit);
        checkArgument(inf != Double.POSITIVE_INFINITY);
        double sup = inf + limit
                , mid
                , value;
        while(sup-inf > epsilon) {
            mid = (inf+sup)/2.0;
            value = f.applyAsDouble((inf+sup)/2.0);
            if(value * f.applyAsDouble(inf) < 0)
                sup = mid;
            else
                inf = mid;
        }
        return inf;
    }
    
}
