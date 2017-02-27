package ch.epfl.alpano.dem;

import ch.epfl.alpano.Interval2D;
import static java.util.Objects.requireNonNull;
import static ch.epfl.alpano.Preconditions.checkArgument;


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
    private DiscreteElevationModel dem1;
    
    
    /**
     * Le second MNT.
     */
    private DiscreteElevationModel dem2;
    
    
    /**
     * Construit un CompositeDiscreteElevationModel, composé par l'union de deux
     * autres MNT.
     * 
     * @param dem1
     *          Premier MNT
     * @param dem2
     *          Second MNT
     *          
     * @throws NullPointerException
     *          si l'un des arguments est null.
     */
    public CompositeDiscreteElevationModel(DiscreteElevationModel dem1, DiscreteElevationModel dem2) {
        this.dem1 = requireNonNull(dem1);
        this.dem2 = requireNonNull(dem2);
    }
    
    @Override
    public void close() throws Exception {
        dem1.close();
        dem2.close();
    }

    @Override
    public Interval2D extent() {
        return dem1.extent().union(dem2.extent());
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y), "the DEM does not contain the index given");
        
        if(dem1.extent().contains(x, y))
            return dem1.elevationSample(x, y);
        
        return dem2.elevationSample(x, y);
    }

}
