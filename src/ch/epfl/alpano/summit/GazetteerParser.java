package ch.epfl.alpano.summit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.alpano.GeoPoint;

/**
 * Class containing static methods used to load summit data from txt files
 */
public class GazetteerParser {

    private static final int VALID_LINE_PARAMETERS_MIN_COUNT = 7;

    private GazetteerParser() {
    }

    /**
     * Loads every summit data from a txt file and stores it in an array
     * 
     * @param file
     *            path of the file to read
     * @return an {@link ArrayList} of summit or null if the file is invalid
     * @throws IOException
     */
    public static List<Summit> readSummitsFrom(File file) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.US_ASCII))) {
            List<Summit> summit = new ArrayList<Summit>();
            String line;
            while ((line = buffer.readLine()) != null) {
                summit.add(parseSummitLineData(
                        line.trim().replaceAll("\\s+", " ")));
            }
            return Collections.unmodifiableList(summit);
        } catch (FileNotFoundException e) {
            throw new IOException("Error: file not found");
        }
    }

    /**
     * Parse a line data of a summit
     * 
     * @param line
     *            string line of data
     * @return a new Summit with the data stored in the line
     */
    private static Summit parseSummitLineData(String line) throws IOException {
        String[] param = line.split("\\s");

        if (param.length < VALID_LINE_PARAMETERS_MIN_COUNT) {
            throw new IOException(
                    "Error: invalid number of parameters in line");
        }

        double longitude = parseAngleData(param[0]);
        double latitude = parseAngleData(param[1]);

        int altitude = 0;
        try {
            altitude = Integer.parseInt(param[2]);
        } catch (NumberFormatException e) {
            throw new IOException("Error: invalid altitude format in line");
        }
        if (altitude < 0) {
            throw new IOException("Error: invalid elevation data in line");
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 6; i < param.length; i++) {
            nameBuilder.append(param[i]).append(" ");
        }
        String name = nameBuilder.toString().trim();

        return new Summit(name, new GeoPoint(longitude, latitude), altitude);
    }

    /**
     * Converts a string representing an angle in deg/min/sec into radians
     * 
     * @param angle
     *            String containing a deg/min/sec representation of an angle
     * @return the numerical representation in radians of the angle
     */
    private static double parseAngleData(String angle) throws IOException {
        try {
            String[] degree = angle.split(":");
            double decimalDegree = Integer.parseInt(degree[0])
                    + Integer.parseInt(degree[1]) / 60d
                    + Integer.parseInt(degree[2]) / 3600d;
            return Math.toRadians(decimalDegree);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("Error: not enough parameters in string data");
        } catch (NumberFormatException e) {
            throw new IOException("Error: invalid angle format in line");
        }
    }
}
