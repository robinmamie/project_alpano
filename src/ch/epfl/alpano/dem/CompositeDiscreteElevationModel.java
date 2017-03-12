package ch.epfl.alpano.dem;

import ch.epfl.alpano.Interval2D;
import static java.util.Objects.requireNonNull;


/**
 * Représente l'union de deux MNT. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
final class CompositeDiscreteElevationModel implements DiscreteElevationModel {

    /**
     * Le premier MNT.
     */
    private final DiscreteElevationModel dem1;
    
    
    /**
     * Le second MNT.
     */
    private final DiscreteElevationModel dem2;
    
    
    private final Interval2D ext1;
    private final Interval2D ext2;
    
    /**
     * Construit un CompositeDiscreteElevationModel, composé par l'union de deux
     * autres MNT.
     * 
     * @param dem1
     *          Premier MNT
     * @param dem2
     *          Second MNT
     */
    public CompositeDiscreteElevationModel(DiscreteElevationModel dem1, DiscreteElevationModel dem2) {
        this.dem1 = requireNonNull(dem1
                , "The first given DEM is null.");
        this.dem2 = requireNonNull(dem2
                , "The second given DEM is null.");
        
        // Sauvegarde directement les étendues des MNT afin
        // de gagner en vitesse d'exécution.
        this.ext1 = dem1.extent();
        this.ext2 = dem2.extent();
    }
    
    @Override
    public void close() throws Exception {
        dem1.close();
        dem2.close();
    }

    @Override
    public Interval2D extent() {
        return ext1.union(ext2);
    }

    @Override
    public double elevationSample(int x, int y) {
        if(ext1.contains(x, y))
            return dem1.elevationSample(x, y);
        
        if(ext2.contains(x, y))
            return dem2.elevationSample(x, y);
        
        throw new IllegalArgumentException("The DEM does not contain the given index.");
    }

}
