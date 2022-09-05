package ch.epfl.alpano.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.util.StringConverter;

/**
 * Class that is used to convert a {@link String} in a list into its index in
 * the list
 */
public final class LabeledListStringConverter extends StringConverter<Integer> {

    private final List<String> stringList;

    /**
     * Constructs a LabeledListStringConverter
     * 
     * @param strings
     *            list of {@link String}
     */
    public LabeledListStringConverter(String... strings) {
        stringList = Collections.unmodifiableList(Arrays.asList(strings));
    }

    @Override
    public String toString(Integer object) {
        return stringList.get(object);
    }

    @Override
    public Integer fromString(String string) {
        return stringList.indexOf(string);
    }

}
