package ch.epfl.alpano.gui;

import static ch.epfl.alpano.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import javafx.util.StringConverter;

/**
 * Convertit entre des chaînes de caractères prédifinies et des entiers.
 *
 * @author Robin Mamie (257234)
 * @author Maxence Jouve (269716)
 */
public final class LabeledListStringConverter extends StringConverter<Integer> {

    /**
     * Valeurs possibles définies dans le constructeur.
     */
    private final List<String> values;

    /**
     * Construit un convertisseur entre chaînes de caractères prédéfinis et des
     * entiers.
     * 
     * @param values
     *            Les valeurs prédéfinies de chaînes de caractères.
     */
    public LabeledListStringConverter(String... values) {
        this.values = unmodifiableList(new ArrayList<>(asList(values)));
    }

    @Override
    public Integer fromString(String arg0) {
        checkArgument(values.contains(arg0), "Invalid input string.");
        return values.indexOf(arg0);
    }

    @Override
    public String toString(Integer arg0) {
        if(arg0 == null)
            return "";
        checkArgument(0 <= arg0 && arg0 < values.size(), "Invalid index.");
        return values.get(arg0);
    }

}
