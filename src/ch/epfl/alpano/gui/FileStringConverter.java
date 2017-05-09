package ch.epfl.alpano.gui;

import java.io.File;

import javafx.util.StringConverter;

public class FileStringConverter extends StringConverter<File> {
    
    private final String folder;
    
    public FileStringConverter(String folder) {
        this.folder = folder;
    }

    @Override
    public File fromString(String arg0) {
        return new File(folder + arg0);
    }

    @Override
    public String toString(File arg0) {
        return arg0.getName();
    }

}
