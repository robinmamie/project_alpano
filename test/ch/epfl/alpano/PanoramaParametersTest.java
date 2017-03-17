package ch.epfl.alpano;

import static ch.epfl.alpano.Preconditions.checkArgument;

import org.junit.Test;

public class PanoramaParametersTest {
    
    private static PanoramaParameters p = new PanoramaParameters(
            new GeoPoint(0,0),
            1000,
            0,
            Math.PI/2.0,
            3000,
            100,
            100
            );
    
    @Test
    public void truc() {
        
    }
    
    ////////// checkArgument (1 argument)

    @Test
    public void checkArgument1SucceedsForTrue() {
        checkArgument(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkArgument1ThrowsForFalse() {
        checkArgument(false);
    }
}
