package ch.epfl.alpano.gui;

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

    private final List<String> values;

    /**
     * Construit un convertisseur entre chaînes de caractères prédéfinis et des
     * entiers.
     * 
     * @param values
     *            Les valeurs prédéfinies de chaînes de caractères.
     */
    public LabeledListStringConverter(String... values) {
        this.values = new ArrayList<>();
        for (String s : values)
            this.values.add(s);
    }

    @Override
    public Integer fromString(String arg0) {
        if (!values.contains(arg0))
            throw new IllegalArgumentException("Invalid input string.");
        return values.indexOf(arg0);
    }

    @Override
    public String toString(Integer arg0) {
        if (arg0 < 0 || values.size() <= arg0)
            throw new IllegalArgumentException("Invalid index.");
        return values.get(arg0);
    }

}
