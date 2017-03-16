package ch.epfl.alpano.summit;

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
 * Importe un fichier spécifique contenant les coordonnées des
 * sommets alpins. Classe non instantiable.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class GazetteerParser {

    private GazetteerParser() {}

    private static String getSummit(String[] line) {
        StringBuilder sb = new StringBuilder();
        for(int i = 6; i < line.length; ++i) {
            sb.append(line[i]);
            if(i+1 < line.length)
                sb.append(" ");
        }
        return sb.toString();
    }

    private static double hmsToRadians(String degrees) {
        String[] hms = degrees.split(":");
        double factor = 1.0;
        double angleDeg = 0.0;
        for(int i = 0; i < hms.length; ++i) {
            angleDeg += Integer.parseInt(hms[i]) / factor;
            factor *= 60;
        }
        return Math.toRadians(angleDeg);
    }

    private static GeoPoint getPoint(String lon, String lat) {
        return new GeoPoint(hmsToRadians(lon), hmsToRadians(lat));
    }

    /**
     * Construit une liste immuable des sommets alpins.
     * 
     * @param file
     *          Le fichier contenant les sommets alpins.
     *          
     * @return Une liste immuable des sommets alpins.
     * 
     * @throws IOException
     *          s'il y a un problème à la lecture du fichier
     *          ou si le fichier n'est pas conforme aux restrictions.
     */
    public static List<Summit> readSummitsFrom(File file) 
            throws IOException {
        ArrayList<Summit> summits = new ArrayList<>();
        String s;
        try (BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            while((s = b.readLine()) != null) {
                // Seperates the line in blocks
                // Cuts where there is one or more whitespace
                String[] elements = s.trim().split("\\s+");
                
                String summit = getSummit(elements);
                GeoPoint point = getPoint(elements[0], elements[1]);
                int elevation = Integer.parseInt(elements[2]);
                
                summits.add(new Summit(summit, point, elevation));
            }
        } catch(Exception e) {
            throw new IOException(e.getMessage());
        }

        return Collections.unmodifiableList(summits);
    }
}
