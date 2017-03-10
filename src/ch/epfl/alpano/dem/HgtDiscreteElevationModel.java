package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

/**
 * Représente un MNT discret obtenu d'un fichier au
 * format HGT. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

    private static final int SIDE = SAMPLES_PER_DEGREE + 1;
    private final FileInputStream stream;
    private ShortBuffer source;

    private final int lonIndex;
    private final int latIndex;
    private final Interval2D ext;

    
    /**
     * Constructeur de la classe HgtDEM.
     * 
     * @param file
     *          fichier ".hgt"
     */
    public HgtDiscreteElevationModel(File file) {
        String n   = file.getName();
        
        // On vérifie si le nom du fichier est valide.
        String fni = "file name invalid: ";

        char ns = n.charAt(0);
        char ew = n.charAt(3);
        int lat, lon;

        try {        
            lat = Integer.parseInt(n.substring(1,3));
            lon = Integer.parseInt(n.substring(4,7));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(fni + "does not contain numbers at the right places");
        }

        lonIndex = (ew == 'E' ? 1 : -1) * lon * SAMPLES_PER_DEGREE;
        latIndex = (ns == 'N' ? 1 : -1) * lat * SAMPLES_PER_DEGREE;

        checkArgument(n.length() == 11
                , fni + "too short or too long");
        checkArgument(ns == 'N' ||  ns == 'S'
                , fni + "isn't defined for North or South");
        checkArgument(ns != 'N' ||  lat < 90
                , fni + "northern latitude impossible");
        checkArgument(lat <= 90
                , fni + "latitude impossible");
        checkArgument(ew == 'E' ||  ew == 'W'
                , fni + "isn't defined for East or West");
        checkArgument(ew != 'E' || lon < 180
                , fni + "eastern longitude impossible");
        checkArgument(lon <= 180
                , fni + "longitude impossible");
        checkArgument(n.substring(7, 11).equals(".hgt")
                , fni + "doesn't end with correct extension");


        // On vérifie si la taille du fichier est adéquate.
        final long l = file.length();

        checkArgument(l == 2 * SIDE * SIDE
                ,  "file invalid: does not comply to byte restrictions");

        // On extrait les points du fichiers pour les enregistrer
        // dans un ShortBuffer.
        try {
            stream = new FileInputStream(file);
            source = stream.getChannel()
                    .map(MapMode.READ_ONLY, 0, l)
                    .asShortBuffer();
        } catch(IOException e) {
            throw new IllegalArgumentException("file invalid");
        }

        // On enregistre l'étendue du MNT pour ne pas devoir le recalculer
        // à chaque fois que extent() est demandé.
        Interval1D longi = new Interval1D(lonIndex, lonIndex + SIDE - 1);
        Interval1D latit = new Interval1D(latIndex, latIndex + SIDE - 1);
        this.ext = new Interval2D(longi, latit);
    }

    @Override
    public void close() throws Exception {
        stream.close();
        source = null;
    }

    @Override
    public Interval2D extent() {
        return ext;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(ext.contains(x, y), "the HgtDEM does not contain the given index");

        int ySize = latIndex + SIDE - 1 - y;
        int xSize = x - lonIndex;

        return source.get(ySize * SIDE + xSize);
    }

}
