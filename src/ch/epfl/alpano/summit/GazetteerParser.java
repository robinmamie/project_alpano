package ch.epfl.alpano.summit;

import static java.lang.Integer.parseInt;
import static java.lang.Math.toRadians;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Collections.unmodifiableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    /**
     * Selon le split() effectué dans readSummitsFrom, le nom du sommet commence
     * à cet index.
     */
    private static final int NAME_POSITION = 6;

    /**
     * Constructeur privé, car la classe est non instanciable.
     */
    private GazetteerParser() {
    }

    /**
     * Récupère le nom du sommet.
     * 
     * @param line
     *            La ligne extraite du fichier.
     * 
     * @return Le nom du sommet.
     * 
     * @throws IOException
     *             si le format de la ligne est invalide.
     */
    private static String getName(String[] line) throws IOException {
        if (line.length <= NAME_POSITION)
            throw new IOException();
        StringBuilder sb = new StringBuilder();
        for (int i = NAME_POSITION; i < line.length; ++i) {
            sb.append(line[i]);
            if (i != line.length - 1)
                sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * Retourne la valeur en radians de l'angle donné en argument.
     * 
     * @param degrees
     *            String représentant l'angle sous le format "xxx:xx:xx"
     * 
     * @return L'angle en radians.
     * 
     * @throws NumberFormatException
     *             si au moins une partie de l'angle n'est pas composée de
     *             nombres.
     * @throws ArrayIndexOutOfBoundsException
     *             si l'angle est composé de moins de 3 valeurs.
     */
    private static double hmsToRadians(String degrees)
            throws NumberFormatException, ArrayIndexOutOfBoundsException {
        String[] hmsS = degrees.split(":");
        double[] hms = { parseInt(hmsS[0]), parseInt(hmsS[1]),
                parseInt(hmsS[2]) };
        hms[0] += (degrees.charAt(0) == '-' ? -1 : 1)
                * ((hms[1] + hms[2] / 60.0) / 60.0);
        return toRadians(hms[0]);
    }

    /**
     * Créée un point à partir de la longitude et la latitude passées en
     * argument.
     * 
     * @param lon
     *            La longitude du point.
     * @param lat
     *            La latitude du point.
     * 
     * @return Le point géographique associé aux valeurs passées en argument.
     * 
     * @throws NumberFormatException
     *             si au moins une partie de l'un des deux angles n'est pas
     *             composée de nombres.
     * @throws ArrayIndexOutOfBoundsException
     *             si l'un des deux angles est composé de moins de 3 valeurs.
     */
    private static GeoPoint getPoint(String lon, String lat)
            throws NumberFormatException, ArrayIndexOutOfBoundsException {
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
                String[] elements = s.trim().split("\\s+");
                summits.add(new Summit(getName(elements),
                        getPoint(elements[0], elements[1]),
                        parseInt(elements[2])));
            }
        } catch (NumberFormatException e) {
            throw new IOException(
                    "One of the angles given is not formed of letters.");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IOException("One of the angles given is too short.");
        }

        return unmodifiableList(summits);
    }
}
