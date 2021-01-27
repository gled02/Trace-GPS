package tracesgps;

import androidx.annotation.NonNull;

import java.io.File;
/**
 * Interface qui permet d'enregistrer un parcours dans un fichier de type
 * xml, json ou csv.
 * Le dossier où sont enregistrés les fichiers a comme chemin absolu
 * "Settings.getPath()".
 * Les fichiers où sont enregistrés les parcours sont nommés de la façon
 * suivante:
 * 		"path" + date + "*" + Settings.getFileExtention(),
 * 		où * vaut :
 * 			- ?pause? -> le fichier est en pause et l'écriture dans le fichier
 * 						 est arretée.
 * 			- ?resume? -> le fichier est en attente de la reprise de l'écriture
 * 			- "" -> l'écriture dans le fichier est terminée.
 * <cons>
 *  $DESC$
 *  	Un fichier de parcours qui a comme chemin absolu :
 *  		Settings.getPath() + "path" + date + "?resume?"
 *  		+ Settings.getFileExtention()
 *  $ARGS$ -
 *  $POST$
 *    getFileState() == State.RESUME
 *    getFilePath() == new File(Settings.getPath(), nom du fichier) </cons>
 * <cons>
 *  $DESC$
 *    Un fichier de parcours de chemin 'f', où l'écriture est en pause.
 *  $ARGS$
 *    File f
 *  $PRE$
 *    f != null
 *  $POST$
 *    getFilePath() == f
 *    getFileState() == State.PAUSE </cons>
 * @author Gledis Shkurti
 */

public interface SaveToFile {

    // CONSTANTES

    /**
     * Type énuméré pour l'état du fichier.
     */
    enum State {
        PAUSE("?pause?"),
        RESUME("?resume?"),
        FINISHED("");

        private String state;

        State(String s) {
            state = s;
        }

        @NonNull
        public String toString() {
            return state;
        }
    }

    // REQUETES

    /**
     * Retourne le chemin du fichier de sauvegarde pour le parcours.
     */
    File getFilePath();

    /**
     * Change l'état du fichier en s.
     * <pre>
     * 	s != null </pre>
     */
    void setStateOfFile(State s);

    /**
     * Renvoie le contenu du fichier de parcours sous la forme d'une chaîne de
     * caractères.
     * <pre>
     *   getFilePath() != null </pre>
     */
    String readFile();

    /**
     * Renvoie le parcours enregistré dans le fichier du parcours.
     * <pre>
     *   getFilePath() != null </pre>
     * <post>
     * 	 Soit p le parcours envoyé :
     *   	Si le fichier est d'extention .csv : p = readCsvFile(readFile()).
     *   	Si le fichier est d'extention .xml : p = readXmlFile(getFilePath()).
     *   	Si le fichier est d'extention .json : p = readJsonFile(readFile()).
     * @throws AssertionError
     *   Si le fichier n'est pas d'extention .xml ou .csv ou .json.
     */
    Parcours readFilePath();

    // COMMANDES

    /**
     * Écrit dans le fichier du parcours, le parcours donné en paramètre.
     * <pre>
     * 	 p != null
     *   getFilePath() != null </pre>
     * <post>
     *   Le fichier contient le parcours avec les balises en format décimal.
     *   Si le fichier est d'extention .csv : writeToCsvFile(p, getFile()).
     *   Si le fichier est d'extention .xml : writeToXmlFile(p, getFile()).
     *   Si le fichier est d'extention .json : writeToJsonFile(p, getFile()).
     * </post>
     * @throws AssertionError
     *   Si le fichier n'est pas d'extention .xml ou .csv ou .json.
     */
    void writeToFile(Parcours p);

    /**
     * Change le nom du fichier de parcours en : Settings.getPath() + "path"
     * + date + "?pause?" + Settings.getFileExtention().
     * Renvoie true si le nom du fichier a changé avec succès, false sinon.
     * <pre>
     * 	 getStateOfFile() == State.RESUME
     * </pre>
     * <post>
     * 	 getStateOfFile() == State.PAUSE
     * 	 getFilePath() == Settings.getPath() + "path" + date
     * 		+ getStateOfFile().toString() + Settings.getFileExtention()
     * 	 StdFileList.setPausedFile(getFilePath())
     * </post>
     */
    boolean pause();

    /**
     * Change le nom du fichier de parcours en : Settings.getPath() + "path"
     * + date + "?resume?"+ Settings.getFileExtention().
     * Renvoie true si le nom du fichier a changé avec succès, false sinon.
     * <pre>
     * 	 getStateOfFile() == State.PAUSE
     * </pre>
     * <post>
     * 	 getStateOfFile() == State.RESUME
     * 	 getFilePath() == Settings.getPath() + "path" + date
     * 		+ getStateOfFile().toString() + Settings.getFileExtention()
     * 	 StdFileList.setPausedFile(null)
     *  </post>
     */
    boolean resume();

    /**
     * Change le nom du fichier de parcours en : Settings.getPath() +"path"
     * + date + "$" + Settings.getFileExtention().
     * Renvoie true si le nom du fichier a changé avec succès, false sinon.
     * <post>
     *   getStateOfFile() == State.FINISHED
     *   StdFileList.setPausedFile(null)
     * 	 getFilePath() == Settings.getPath() + "path" + date
     * 		+ getStateOfFile().toString() + Settings.getFileExtention()
     * 	 StdFileList.getFileList().add(getFilePath())
     * </post>
     */
    boolean finish(Parcours p);
}
