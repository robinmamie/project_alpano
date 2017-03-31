package ch.epfl.alpano.summit;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.alpano.GeoPoint;

/**
 * Importe un fichier spécifique contenant les coordonnées des sommets alpins.
 * Classe non instantiable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class GazetteerParser {

    private static final int NAME_POSITION = 6;

    private GazetteerParser() {
    }

    private static String getName(String[] line) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (line.length <= NAME_POSITION)
            throw new IOException();
        for (int i = NAME_POSITION; i < line.length; ++i) {
            sb.append(line[i]);
            if (i + 1 < line.length)
                sb.append(" ");
        }
        return sb.toString();
    }

    private static double hmsToRadians(String degrees)
            throws NumberFormatException {
        String[] hmsS = degrees.split(":");
        double[] hms = { Integer.parseInt(hmsS[0]), Integer.parseInt(hmsS[1]),
                Integer.parseInt(hmsS[2]) };
        double fractional = (hms[1] + hms[2] / 60.0) / 60.0;
        hms[0] += (degrees.charAt(0) == '-' ? -1 : 1) * fractional;
        return Math.toRadians(hms[0]);
    }

    private static GeoPoint getPoint(String lon, String lat) {
        return new GeoPoint(hmsToRadians(lon), hmsToRadians(lat));
    }

    /**
     * Construit une liste immuable des sommets alpins.
     * 
     * @param file
     *            Le fichier contenant les sommets alpins.
     * 
     * @return Une liste immuable des sommets alpins.
     * 
     * @throws IOException
     *             s'il y a un problème à la lecture du fichier ou si le fichier
     *             n'est pas conforme aux restrictions.
     */
    public static List<Summit> readSummitsFrom(File file) throws IOException {
        List<Summit> summits = new ArrayList<>();
        try (BufferedReader b = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), US_ASCII))) {
            String s;
            while ((s = b.readLine()) != null) {
                // Seperates the line in blocks
                // Cuts where there is one or more whitespace
                String[] elements = s.trim().split("\\s+");

                String summit = getName(elements);
                GeoPoint point = getPoint(elements[0], elements[1]);
                int elevation = Integer.parseInt(elements[2]);

                summits.add(new Summit(summit, point, elevation));
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (NumberFormatException e) {
            throw new IOException(e.getMessage());
        }

        return Collections.unmodifiableList(new ArrayList<>(summits));
    }
}
