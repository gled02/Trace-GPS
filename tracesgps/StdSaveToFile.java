package tracesgps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author Gledis Shkurti.
 */
public class StdSaveToFile implements SaveToFile {

    // ATTRIBUTS

    private File filePath;
    private State fileState;

    // CONSTRUCTEURS

    StdSaveToFile() {
        fileState = State.RESUME;
        String date = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss",
                Locale.getDefault()).format(new Date());
        String nameFile = "path" + date + fileState.toString()
                + Settings.getFileExtention().toString();
        filePath = new File(Settings.getPath(), nameFile);
        try {
            if (!filePath.createNewFile()) {
                throw new AssertionError("Le fichier du parcours "
                        + "ne peut pas être crée");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!filePath.setReadable(true)) {
            throw new AssertionError("On ne peut pas lire");
        }
        if (!filePath.setWritable(true)) {
            throw new AssertionError("On ne peut pas écrire");
        }
    }

    public StdSaveToFile(File f) {
        if (f == null) {
            throw new AssertionError("Le fichier vaut null");
        }

        filePath = f;
        fileState = State.PAUSE;
    }

    // REQUETES

    @Override
    public File getFilePath() {
        return filePath;
    }

    @Override
    public String readFile() {
        if (getFilePath() == null) {
            throw new AssertionError("Le fichier du parcours "
                    + " vaut null");
        }

        StringBuilder value = new StringBuilder();
        try {
            BufferedReader fileRead =
                    new BufferedReader(new FileReader(getFilePath()));
            String res = fileRead.readLine();
            while (res != null) {
                value.append(res).append("\n");
                res = fileRead.readLine();
            }
            fileRead.close();
        }  catch (FileNotFoundException e) {
            throw new AssertionError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value.toString();
    }

    @Override
    public Parcours readFilePath() {
        if (getFilePath() == null) {
            throw new AssertionError("Le fichier du "
                    + "parcours vaut null");
        }

        String tmp = getFilePath().getName();
        Parcours p;
        if (tmp.endsWith(FileExtension.CSV.toString())) {
            p = readCsvFile(readFile());
        } else if (tmp.endsWith(FileExtension.XML.toString())) {
            p = readXmlFile(getFilePath());
        } else if (tmp.endsWith(FileExtension.JSON.toString())) {
            p = readJsonFile(readFile());
        } else {
            throw new AssertionError("Format invalide");
        }
        return p;
    }

    // COMMANDES

    @Override
    public void setStateOfFile(State s) {
        if (s == null) {
            throw new AssertionError("Invalid State");
        }
        fileState = s;
    }

    @Override
    public void writeToFile(Parcours p) {
        if (p == null) {
            throw new AssertionError("Le parcours vaut null");
        }
        if (getFilePath() == null) {
            throw new AssertionError("Le fichier du parcours "
                    + "vaut null");
        }

        String tmp = getFilePath().getName();
        if (tmp.endsWith(FileExtension.CSV.toString())) {
            writeToCsvFile(p, getFilePath());
        } else if (tmp.endsWith(FileExtension.XML.toString())) {
            writeToXmlFile(p, getFilePath());
        } else if (tmp.endsWith(FileExtension.JSON.toString())) {
            writeToJsonFile(p, getFilePath());
        } else {
            throw new AssertionError("Format invalide");
        }
    }

    @Override
    public boolean pause() {
        if (fileState != State.RESUME) {
            return false;
        }
        if (renameToState(State.PAUSE)) {
            return false;
        } else {
            StdFileList.setPausedFile(filePath);
            return true;
        }
    }

    @Override
    public boolean resume() {
        if (fileState != State.PAUSE) {
            return false;
        }
        if (renameToState(State.RESUME)) {
            return false;
        } else {
            StdFileList.setPausedFile(null);
            return true;
        }
    }

    @Override
    public boolean finish(Parcours p) {
        //setStateOfFile(State.FINISHED);
        StdFileList.setPausedFile(null);
        if (renameToState(State.FINISHED)) {
            return false;
        } else {
            StdFileList.getFileList().add(filePath);
            return true;
        }
    }

    // OUTILS

    /**
     * Ecrit dans le fichier f le parcours p au format csv.
     */
    private void writeToCsvFile(Parcours p, File f) {
        try {
            BufferedWriter fileWrite =
                    new BufferedWriter(new FileWriter(f));
            int i = 0;
            fileWrite.write(
                    "Nr.Balise; Latitude; Longitude; Temps; Commentaires");
            fileWrite.newLine();
            for (Balise b : p) {
                ++i;
                fileWrite.append(String.valueOf(i)).append(";")
                        .append(String.valueOf(b.getLatitude())).append(";")
                        .append(String.valueOf(b.getLongitude())).append(";")
                        .append(b.getStringFormatTime()).append(";")
                        .append(b.getComments());
                fileWrite.newLine();
            }
            fileWrite.close();
        }  catch (FileNotFoundException e) {
            throw new AssertionError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ecrit dans le fichier f le parcours p au format Json.
     */
    private void writeToJsonFile(Parcours p, File f) {
        JSONObject obj = new JSONObject();
        JSONArray balises = new JSONArray();
        int i = 0;
        for (Balise b : p) {
            JSONObject latitude = new JSONObject();
            JSONObject longitude = new JSONObject();
            JSONObject time = new JSONObject();
            JSONObject comments = new JSONObject();
            try {
                latitude.put("Latitude",
                        String.valueOf(b.getLatitude()));
                longitude.put("Longitude",
                        String.valueOf(b.getLongitude()));
                time.put("Temps", b.getStringFormatTime());
                comments.put("Commentaire", b.getComments());
                ++i;
                List<JSONObject> l = new ArrayList<>();
                l.add(latitude);
                l.add(longitude);
                l.add(time);
                l.add(comments);
                JSONObject temp = new JSONObject();
                temp.put("Balise " + i , l);
                balises.put(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            obj.put("Parcours", balises);
            BufferedWriter fileWrite =
                    new BufferedWriter(new FileWriter(f));
            fileWrite.write(obj.toString());
            fileWrite.close();
        } catch (JSONException e) {
            throw new AssertionError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ecrit dans le fichier f le parcours p au format xml.
     */
    private void writeToXmlFile(Parcours p, File f) {
        try {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            Element parcours = doc.createElement("Parcours");
            doc.appendChild(parcours);
            int i = 0;
            for (Balise b : p) {
                ++i;
                String tmp = "Balise" + i;
                Element balise = doc.createElement(tmp);
                parcours.appendChild(balise);

                Element latitude = doc.createElement("latitude");
                Attr attrType = doc.createAttribute("value");
                attrType.setValue(String.valueOf(b.getLatitude()));
                latitude.setAttributeNode(attrType);
                latitude.appendChild(doc.createTextNode("Latitude"));
                balise.appendChild(latitude);

                Element longitude = doc.createElement("longitude");
                Attr attrType1 = doc.createAttribute("value");
                attrType1.setValue(String.valueOf(b.getLongitude()));
                longitude.setAttributeNode(attrType1);
                longitude.appendChild(doc.createTextNode("Longitude"));
                balise.appendChild(longitude);

                Element time = doc.createElement("time");
                Attr attrType2 = doc.createAttribute("value");
                attrType2.setValue(b.getStringFormatTime());
                time.setAttributeNode(attrType2);
                time.appendChild(doc.createTextNode("Temps"));
                balise.appendChild(time);

                Element comments = doc.createElement("comments");
                Attr attrType3 = doc.createAttribute("value");
                attrType3.setValue(b.getComments());
                comments.setAttributeNode(attrType3);
                comments.appendChild(doc.createTextNode("Commentaires"));
                balise.appendChild(comments);
            }
            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Renvoie le parcours enregistré dans le fichier f de format xml.
     */
    private Parcours readXmlFile(File f) {
        Parcours p = new StdParcours();
        try {
            if (f != null) {
                DocumentBuilderFactory dbFactory =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                String file = readFile() + "\n";
                InputStream is = new ByteArrayInputStream(file.getBytes(
                        StandardCharsets.UTF_8));
                Document doc = dBuilder.parse(is);

                NodeList latitude = doc.getElementsByTagName("latitude");
                NodeList longitude = doc.getElementsByTagName("longitude");
                NodeList time = doc.getElementsByTagName("time");
                NodeList comments = doc.getElementsByTagName("comments");

                for (int i = 0; i < latitude.getLength(); i++) {
                    String lat = latitude.item(i).getAttributes()
                            .getNamedItem("value").getNodeValue();
                    String log = longitude.item(i).getAttributes()
                            .getNamedItem("value").getNodeValue();
                    String t = time.item(i).getAttributes()
                            .getNamedItem("value").getNodeValue();
                    String c = comments.item(i).getAttributes()
                            .getNamedItem("value").getNodeValue();
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                    Date d = sdf.parse(t);
                    if (d == null) {
                        throw new AssertionError(
                                "Impossible parse");
                    }
                    Balise b = new Balise(Double.parseDouble(lat),
                            Double.parseDouble(log), d.getTime(), c);
                    p.add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * Renvoie le parcours enregistré dans le fichier f de format Json.
     */
    private Parcours readJsonFile(String f) {
        Parcours p = new StdParcours();
        try {
            if (!f.isEmpty()){
                JSONObject obj = new JSONObject(f);
                int k = 0;
                for (int i = 0;
                     i < obj.getJSONArray("Parcours").length(); ++i) {

                    ++k;
                    JSONArray array = new JSONArray(obj.getJSONArray
                            ("Parcours").getJSONObject(i).
                            getString("Balise " + k));
                    JSONObject latitude = array.getJSONObject(0);
                    JSONObject longitude = array.getJSONObject(1);
                    JSONObject time = array.getJSONObject(2);
                    JSONObject comments = array.getJSONObject(3);

                    String lat = latitude.get("Latitude").toString();
                    String log = longitude.getString("Longitude");
                    String t = time.getString("Temps");
                    String c = comments.getString("Commentaire");
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                    Date d = sdf.parse(t);
                    if (d == null) {
                        throw new AssertionError(
                                "Impossible parse");
                    }
                    Balise b = new Balise(Double.parseDouble(lat),
                            Double.parseDouble(log), d.getTime(), c);
                    p.add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * Renvoie le parcours enregistré dans le fichier f de format Csv.
     */
    private Parcours readCsvFile(String f) {
        Parcours p = new StdParcours();
        try {
            String[] tmp = f.split(";");
            for (int i = 5; i < tmp.length; i += 4) {
                String lat = tmp[i];
                String log = tmp[i + 1];
                String t = tmp[i + 2];
                String c = tmp[i + 3];
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                Date d = sdf.parse(t);
                if (d == null) {
                    throw new AssertionError("Impossible parse");
                }
                Balise b = new Balise(Double.parseDouble(lat),
                        Double.parseDouble(log),
                        d.getTime(),
                        c.replace(c.charAt(c.length() - 1), ' ')
                                .trim());
                p.add(b);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * Change l'état d'un fichier en state.
     */
    private boolean renameToState(State state) {
        String name = "";
        for (int i = 0; i < filePath.getName().length(); ++i) {
            if (filePath.getName().charAt(i) != '?') {
                name += filePath.getName().charAt(i);
            } else {
                break;
            }
        }
        setStateOfFile(state);
        File dest = new File(Settings.getPath(), name
                + fileState.toString() + Settings.getFileExtention());
        if (!filePath.renameTo(dest)) {
            return true;
        }
        filePath = dest;
        return false;
    }
}
