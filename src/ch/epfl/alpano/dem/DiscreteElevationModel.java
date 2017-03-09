package ch.epfl.alpano.dem;

import ch.epfl.alpano.Interval2D;
import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.lang.Math.toDegrees;

/**
 * Représente un MNT (modèle numérique du terrain) discret.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public interface DiscreteElevationModel extends AutoCloseable {
    
    
    /**
     * Le nombre d'échantillons par degré d'un MNT discret.
     */
    int SAMPLES_PER_DEGREE = 3600;
    
    
    /**
     * Le nombre d'échantillons par radian d'un MNT discret.
     */
    double SAMPLES_PER_RADIAN = toDegrees(SAMPLES_PER_DEGREE);
    
    
    /**
     * Retourne l'îndex correspondant à un angle donné en radians.
     * 
     * @param angle
     *          un angle en radians
     *          
     * @return l'index correspondant à l'angle
     */
    static double sampleIndex(double angle) {
        return angle * SAMPLES_PER_RADIAN;
    }
    
    
    /**
     * Retourne l'étendue du MNT.
     * 
     * @return l'étendue du MNT à l'aide d'un Interval2D
     */
    Interval2D extent();
    
    
    /**
     * Retourne l'altitude d'un échantillon du MNT donné.
     * 
     * @param x
     *          index de la longitude
     * @param y
     *          index de la latitude
     *          
     * @return l'altitude de l'échantillon de position <code>x</code> et <code>y</code>
     * 
     * @throws IllegalArgumentException
     *          si l'index ne fait pas partie de l'étendue du MNT
     */
    double elevationSample(int x, int y);
    
    
    /**
     * Retourne un MNT discret représentant l'union du récepteur
     * et de l'argument donné.
     * 
     * @param that
     *          autre instance de DiscreteElevationModel
     * 
     * @return l'union du récepteur et de l'argument <code>that</code>.
     * 
     * @throws IllegalArgumentException
     *          si leurs étendues ne sont pas unionables
     */
    default DiscreteElevationModel union(DiscreteElevationModel that) {
        checkArgument(this.extent().isUnionableWith(that.extent())
                , "The provided DEM are not unionable.");
        return new CompositeDiscreteElevationModel(this, that);
    }

}
