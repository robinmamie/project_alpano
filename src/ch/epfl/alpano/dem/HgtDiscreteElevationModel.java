package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.io.File;

import ch.epfl.alpano.Interval2D;
import ch.epfl.alpano.Math2;

public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

    private static final int LIMIT = (60*60+1)*(60*60+1);
    private final File file;
    
    public HgtDiscreteElevationModel(File file) {
        checkArgument(file.exists() && file.length(), "file name invalid");
        this.file = file;
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Interval2D extent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double elevationSample(int x, int y) {
        // TODO Auto-generated method stub
        return 0;
    }

}
