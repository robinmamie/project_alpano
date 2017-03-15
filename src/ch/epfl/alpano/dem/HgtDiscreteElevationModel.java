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
 * Représente un MNT discret obtenu d'un fichier au format HGT. Classe immuable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

    private static final int SIDE = SAMPLES_PER_DEGREE + 1;
    private final ShortBuffer source;

    private final Interval2D ext;

    /**
     * Construit un MNT discret qui prend ses valeurs, des altitudes, dans un
     * fichier.
     * 
     * @param file
     *            Fichier au format .hgt et correspondant au restrictions
     *            définies.
     * 
     * @throws IllegalArgumentException
     *             si le fichier ou son nom sont invalides.
     */
    public HgtDiscreteElevationModel(File file) {

        checkArgument(file.isFile(), "The file does not exist.");

        final String n = file.getName();

        // On vérifie si le nom du fichier est valide.
        String fni = "The file name is invalid: it ";

        checkArgument(n.length() == 11,
                new StringBuilder(fni).append("is too short or too long: ")
                        .append(n.length()).append(" characters.").toString());

        // On vérifie si la taille du fichier est adéquate.
        final long l = file.length();

        checkArgument(l == 2 * SIDE * SIDE,
                new StringBuilder("The file is invalid: ")
                        .append("it does not comply to byte restrictions: ")
                        .append(l).append(" instead of ")
                        .append(2 * SIDE * SIDE).append(".").toString());

        char ns = n.charAt(0);
        char ew = n.charAt(3);
        int lat = 0, lon = 0;

        checkArgument(ns == 'N' || ns == 'S',
                new StringBuilder(fni)
                        .append("isn't defined for North or South: ").append(ns)
                        .toString());

        checkArgument(ew == 'E' || ew == 'W',
                new StringBuilder(fni)
                        .append("isn't defined for East or West: ").append(ew)
                        .toString());

        checkArgument(n.substring(7, 11).equals(".hgt"), new StringBuilder(fni)
                .append("doesn't end with the extension \".hgt\".").toString());

        try {
            lat = Integer.parseInt(n.substring(1, 3));
            lon = Integer.parseInt(n.substring(4, 7));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    fni + "does not contain numbers at the right places.");
        }

        checkArgument(0 <= lat,
                new StringBuilder(fni)
                        .append("cannot have a negative latitude: ").append(lat)
                        .toString());

        checkArgument(0 <= lon,
                new StringBuilder(fni)
                        .append("cannot have a negative longitude: ")
                        .append(lon).toString());

        checkArgument(ns != 'N' || lat < 90,
                new StringBuilder(fni)
                        .append("cannot have this northern latitude: ")
                        .append(lat).toString());

        checkArgument(lat <= 90, new StringBuilder(fni)
                .append("cannot have this latitude: ").append(lat).toString());

        checkArgument(ew != 'E' || lon < 180,
                new StringBuilder(fni)
                        .append("cannot have this eastern longitude: ")
                        .append(lon).toString());

        checkArgument(lon <= 180, new StringBuilder(fni)
                .append("cannot have this longitude: ").append(lon).toString());

        // On extrait les points du fichiers pour les enregistrer
        // dans un ShortBuffer.
        try (FileInputStream stream = new FileInputStream(file)) {
            source = stream.getChannel().map(MapMode.READ_ONLY, 0, l)
                    .asShortBuffer();
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "The file is either invalid, corrupted, or not found.");
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
    public void close() throws Exception {
    }

    @Override
    public Interval2D extent() {
        return ext;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent().contains(x, y),
                new StringBuilder("The HgtDEM ").append(extent())
                        .append(" does not contain the given index: ").append(x)
                        .append(" and ").append(y).toString());

        int ySize = extent().iY().includedTo() - y;
        int xSize = x - extent().iX().includedFrom();

        return source.get(ySize * SIDE + xSize);
    }

}
