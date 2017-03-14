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
    private final ShortBuffer source;

    private final Interval2D ext;

    
    /**
     * Constructeur de la classe HgtDEM.
     * 
     * @param file
     *          fichier ".hgt"
     */
    public HgtDiscreteElevationModel(File file) {    
      
        //isFile()
        
        final String n   = file.getName();
        
        // On vérifie si le nom du fichier est valide.
        String fni = "The file name is invalid: it ";
        
        checkArgument(n.length() == 11
                , fni + "is too short or too long.");
        
        // On vérifie si la taille du fichier est adéquate.
        final long l = file.length();

        checkArgument(l == 2 * SIDE * SIDE
                ,  "The file is invalid: it does not comply to byte restrictions.");

        char ns = n.charAt(0);
        char ew = n.charAt(3);
        int lat = 0, lon = 0;
        
        checkArgument(ns == 'N' ||  ns == 'S'
                , fni + "isn't defined for North or South.");
        checkArgument(ew == 'E' ||  ew == 'W'
                , fni + "isn't defined for East or West");
        checkArgument(n.substring(7, 11).equals(".hgt")
                , fni + "doesn't end with the correct extension.");


        try {        
            lat = Integer.parseInt(n.substring(1,3));
            lon = Integer.parseInt(n.substring(4,7));
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException(fni + "does not contain numbers at the right places.");
        }


        checkArgument(0 <= lat
                , fni + "cannot have a negative latitude.");
        checkArgument(0 <= lon
                , fni + "cannot have a negative latitude.");
        checkArgument(ns != 'N' ||  lat < 90
                , fni + "cannot have this northern latitude.");
        checkArgument(lat <= 90
                , fni + "cannot have this latitude.");
        checkArgument(ew != 'E' || lon < 180
                , fni + "cannot have this eastern longitude.");
        checkArgument(lon <= 180
                , fni + "cannot have this longitude.");

        // On extrait les points du fichiers pour les enregistrer
        // dans un ShortBuffer.
        try (FileInputStream stream = new FileInputStream(file)) {
            source = stream.getChannel()
                    .map(MapMode.READ_ONLY, 0, l)
                    .asShortBuffer();
        } catch(IOException e) {
            throw new IllegalArgumentException("The file is either invalid, corrupted, or not found.");
        }

        // On enregistre l'étendue du MNT pour ne pas devoir le recalculer
        // à chaque fois que extent() est demandé.
        int lonIndex = (ew == 'E' ? 1 : -1) * lon * SAMPLES_PER_DEGREE;
        int latIndex = (ns == 'N' ? 1 : -1) * lat * SAMPLES_PER_DEGREE;
        Interval1D longi = new Interval1D(lonIndex, lonIndex + SIDE - 1);
        Interval1D latit = new Interval1D(latIndex, latIndex + SIDE - 1);
        this.ext = new Interval2D(longi, latit);
    }

    @Override
    public void close() throws Exception {}

    @Override
    public Interval2D extent() {
        return ext;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y)
                , "The HgtDEM does not contain the given index.");

        int ySize = extent().iY().includedTo() - y;
        int xSize = x - extent().iX().includedFrom();

        return source.get(ySize * SIDE + xSize);
    }

}
