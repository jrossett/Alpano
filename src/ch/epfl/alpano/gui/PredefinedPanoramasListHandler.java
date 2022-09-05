package ch.epfl.alpano.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class used to manage the predefined panorama list files
 */
public final class PredefinedPanoramasListHandler {
    private Map<String, PanoramaUserParameters> savedPanoramas = new HashMap<>();
    private List<String> names = new ArrayList<>();
    private List<String> savedCopy = new ArrayList<>();
    private List<String> defaultList;

    /**
     * Constructs a list of predefined panorama with default entries
     */
    public PredefinedPanoramasListHandler() {
        loadDefaults();
        defaultList = new ArrayList<>(Collections.unmodifiableList(names));
    }

    /**
     * Loads a list of predefined panoramas from a text file
     * 
     * @param file
     *            valid file path
     * @param overwrite
     *            boolean value that tells if the current list must be
     *            overwritten
     * @return true if the values were correctly added, false otherwise
     */
    public boolean loadFromFile(File file, boolean overwrite) {
        if (file.exists() && file.canRead()) {
            try (BufferedReader buffer = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file)))) {
                List<String> panorama = new ArrayList<String>();
                String line;
                while ((line = buffer.readLine()) != null) {
                    panorama.add(line);
                }
                if (overwrite) {
                    clearList();
                }
                return fromListToMap(panorama);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Adds the raw data lines as read in the file to usable
     * {@link PanoramaUserParameters}
     * 
     * @param line
     *            raw line of data
     * @return true if the panoramas were correctly added, false otherwise
     */
    private boolean fromListToMap(List<String> line) {
        for (String s : line) {
            String[] stringParameters = s.split(":");
            int[] integerParameters = new int[stringParameters.length - 1];
            if (integerParameters.length != 8) {
                return false;
            }
            for (int i = 1; i < stringParameters.length; i++) {
                try {
                    integerParameters[i - 1] = Integer.parseInt(stringParameters[i]);
                } catch (NumberFormatException e) {
                    return false;
                }

            }
            names.add(stringParameters[0]);
            savedPanoramas.put(stringParameters[0],
                    new PanoramaUserParameters(integerParameters[0], integerParameters[1],
                            integerParameters[2], integerParameters[3], integerParameters[4],
                            integerParameters[5], integerParameters[6], integerParameters[7], 0));
        }
        return true;
    }

    /**
     * Loads the default list values
     */
    public void clearList() {
        names.clear();
        savedPanoramas.clear();
        loadDefaults();
    }

    /**
     * Loads the default {@link PanoramaUserParameters} in the list
     */
    private void loadDefaults() {
        names.add("Niesen");
        names.add("Alpes du Jura");
        names.add("Finsteraarhorn");
        names.add("Mont Racine");
        names.add("Plage du Pélican");
        names.add("Tour de Sauvabelin");
        names.forEach(s -> savedCopy.add(s));
        savedPanoramas.put("Niesen", PredefinedPanoramas.NIESEN);
        savedPanoramas.put("Alpes du Jura", PredefinedPanoramas.ALPES_DU_JURA);
        savedPanoramas.put("Finsteraarhorn", PredefinedPanoramas.FINSTERAARHORN);
        savedPanoramas.put("Mont Racine", PredefinedPanoramas.MONT_RACINE);
        savedPanoramas.put("Plage du P�lican", PredefinedPanoramas.PLAGE_DU_PELICAN);
        savedPanoramas.put("Tour de Sauvabelin", PredefinedPanoramas.TOUR_DE_SAUVABELIN);
    }

    /**
     * Writes the current list of {@link PanoramaUserParameters} in a file
     * 
     * @param file
     *            path of the written file
     */
    public void writeFile(File file) {
        try (BufferedWriter buffer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file)))) {
            for (int i = 0; i < 6; i++) {
                names.remove(0);
            }
            savedCopy.clear();
            names.forEach(s -> {
                StringBuilder builder = new StringBuilder();
                PanoramaUserParameters params = savedPanoramas.get(s);
                builder.append(s).append(":").append(params.observerLongitude()).append(":")
                        .append(params.observerLatitude()).append(":")
                        .append(params.observerElevation()).append(":")
                        .append(params.centerAzimuth()).append(":")
                        .append(params.horizontalFieldOfView()).append(":")
                        .append(params.maxDistance()).append(":").append(params.width()).append(":")
                        .append(params.height());
                savedCopy.add(s);
                try {
                    buffer.write(builder.toString());
                    buffer.newLine();
                } catch (IOException e) {
                    throw new NullPointerException("Error while writing file");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a {@link PanoramaUserParameters} to the list
     * 
     * @param name
     *            display name of the parameter
     * @param parameters
     *            {@link PanoramaUserParameters}
     */
    public void add(String name, PanoramaUserParameters parameters) {
        names.add(name);
        savedPanoramas.put(name, parameters);
    }

    /**
     * Removes a {@link PanoramaUserParameters} of the list
     * 
     * @param name
     *            display name of the parameter that will be removed
     */
    public void remove(String name) {
        names.remove(name);
        savedPanoramas.remove(name);
    }

    /**
     * Gets a {@link PanoramaUserParameters} from the list
     * 
     * @param name
     *            display name of the parameter
     * @return corresponding {@link PanoramaUserParameters}
     */
    public PanoramaUserParameters get(String name) {
        return savedPanoramas.get(name);
    }

    /**
     * Gets the String list property containing the display names of the
     * parameters
     * 
     * @return the object property of the list of names
     */
    public ObjectProperty<ObservableList<String>> names() {
        return new SimpleObjectProperty<ObservableList<String>>(
                FXCollections.observableArrayList(names));
    }

    /**
     * Checks if the list is at its default stage
     * 
     * @return true if the list contains only the defaults values, false
     *         otherwise
     */
    public boolean isDefault() {
        return names.equals(defaultList);
    }

    /**
     * Tells if a panorama is removable (default values are not removable from
     * the list)
     * 
     * @param name
     *            display name of the parameter
     * @return true if the panorama is removable, false otherwise
     */
    public boolean isRemovable(String name) {
        return !name.equals("Niesen") && !name.equals("Alpes du Jura")
                && !name.equals("Finsteraarhorn") && !name.equals("Mont Racine")
                && !name.equals("Plage du P�lican") && !name.equals("Tour de Sauvablin");
    }

    /**
     * Checks if the list has been modified since the last save
     * 
     * @return true if the list was modified, false otherwise
     */
    public boolean hasChanged() {
        return !savedCopy.equals(names) && !names.equals(defaultList);
    }
}
