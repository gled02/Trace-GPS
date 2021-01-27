package tracesgps;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe abstraite qui permet de gérer la liste des fichiers générés pendant
 * différents parcours. Dans le dossier "Settings.getPath()" se trouve des
 * fichiers où sont enregistrés les parcours.
 * @author Gledis Shkurti.
 */
public abstract class StdFileList {

    // ATTRIBUTS

    private static File pausedFile = null;
    private static List<File> fileList = new ArrayList<>();

    // REQUETES

    /**
     * Renvoie le fichier qui est en pause.
     */
    public static File getPausedFile() {
        return pausedFile;
    }

    /**
     * Renvoie une liste qui contient les fichiers où sont enregistré les
     * parcours, l'ecriture dans ces fichiers est terminée.
     */
    public static List<File> getFileList() {
        return fileList;
    }

    // COMMANDES

    /**
     * Change le fichier qui est en pause, en f.
     */
    static void setPausedFile(File f) {
        pausedFile = f;
    }

    /**
     * Méthode qui rafraîchit le dossier où sont enregistrés les fichiers.
     */
    public static void refresh() {
        for (FileExtension e : FileExtension.values()) {
            refresh(e.toString());
        }
    }

    // OUTILS

    /**
     * Cherche dans le dossier "Settings.getPath()" les fichiers qui sont en
     * attente de la reprise de l'écriture (resume) et les supprime.
     * Ajoute à la liste correspondante les fichiers terminés et affecte à
     * pausedFile le seul fichier dans le dossier qui est en pause.
     * @throws AssertionError
     *      Si la fonction ne réussit pas de supprimer l'un des fichiers ou
     *      s'il y a plusieurs fichiers en pause.
     */
    private static void refresh(String fileExtention) {
        if (Settings.getPath() != null) {
            File f = new File(Settings.getPath());
            File[] pausedFiles = f.listFiles(new MyFileNameFilter(fileExtention,
                    SaveToFile.State.PAUSE));
            File[] onresumeFiles = f.listFiles(new MyFileNameFilter(fileExtention,
                    SaveToFile.State.RESUME));
            File[] normalFiles = f.listFiles(new MyFileNameFilter2(fileExtention));
            if (pausedFiles != null) {
                if (pausedFiles.length > 1) {
                    throw new AssertionError("Il ne doit pas y " +
                            "avoir plusieurs fichiers en pause");
                } else if (pausedFiles.length == 1) {
                    pausedFile = pausedFiles[0];
                }
            }
            if (onresumeFiles != null) {
                for (File file : onresumeFiles) {
                    if (!file.delete()) {
                        throw new AssertionError("Impossible de "
                                + "supprimer le fichier");
                    }
                }
            }
            if (normalFiles != null) {
                fileList.clear();
                fileList.addAll(Arrays.asList(normalFiles));
            }
        }
    }

    /**
     * Classe qui cherche les fichiers qui sont en pause ou resume.
     */
    private static class MyFileNameFilter implements FilenameFilter {

        private String fileExtention;
        private SaveToFile.State s;

        MyFileNameFilter(String extension, SaveToFile.State s) {
            fileExtention = extension;
            this.s = s;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(s.toString() + fileExtention);
        }
    }

    /**
     * Classe qui cherche les fichiers dans lesquels l'écriture
     * est terminée.
     */
    private static class MyFileNameFilter2 implements FilenameFilter {

        private String fileExtention;

        MyFileNameFilter2(String extension) {
            fileExtention = extension;
        }

        @Override
        public boolean accept(File dir, String name) {
            return !(name.endsWith(SaveToFile.State.PAUSE.toString()
                    + fileExtention)
                    && name.endsWith(SaveToFile.State.RESUME.toString()
                    + fileExtention));
        }
    }
}
