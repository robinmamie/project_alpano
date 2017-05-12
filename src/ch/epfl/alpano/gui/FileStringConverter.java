package ch.epfl.alpano.gui;

import java.io.File;

import javafx.util.StringConverter;

public class FileStringConverter extends StringConverter<File> {

    private final String folder;
    private final int sizeOfExtension;

    public FileStringConverter(String folder, int sizeOfExtension) {
        this.folder = folder;
        this.sizeOfExtension = sizeOfExtension + 1;
    }

    @Override
    public File fromString(String arg0) {
        return new File(folder + arg0);
    }

    @Override
    public String toString(File arg0) {
        return arg0.getName().substring(0,
                (int) arg0.getName().length() - sizeOfExtension);
    }

}
