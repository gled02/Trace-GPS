package tracesgps;

import androidx.annotation.NonNull;

/**
 * Type énuméré pour représenter les extentions des fichiers.
 * @author Gledis Shkurti, William Li.
 */
public enum FileExtension {
    XML (".xml"),
    CSV (".csv"),
    JSON (".json");

    // ATTRIBUTS

    private String s;

    // CONSTRUCTEURS

    FileExtension(String s) {
        this.s = s;
    }

    // REQUETES

    @NonNull
    public String toString() {
        return s;
    }

    public static FileExtension findValue(String s) {
        switch (s) {
            case ".xml" :
                return XML;
            case ".csv" :
                return CSV;
            case ".json" :
                return JSON;
        }
        return null;
    }
}