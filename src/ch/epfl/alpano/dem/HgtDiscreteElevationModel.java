package ch.epfl.alpano.dem;

import static ch.epfl.alpano.Math2.sq;
import static ch.epfl.alpano.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;

import ch.epfl.alpano.Interval1D;
import ch.epfl.alpano.Interval2D;

/**
 * Elevation model based on the data contained in a binary file
 */
public final class HgtDiscreteElevationModel implements DiscreteElevationModel {

    private final static int VALID_FILE_LENGTH = (int) (2
            * sq(SAMPLES_PER_DEGREE + 1));
    private final static int VALID_FILENAME_LENGTH = 11;
    private final static String VALID_FILE_EXTENSION = ".hgt";

    private ShortBuffer buffer;

    private final Interval2D extent;

    /**
     * Constructs a {@link DiscreteElevationModel} from the data of a hgt file
     * 
     * @param file
     *            hgt file
     */
    public HgtDiscreteElevationModel(File file) {
        fileIsValid(file);
        try (FileInputStream s = new FileInputStream(file)) {
            ShortBuffer b = s.getChannel()
                    .map(MapMode.READ_ONLY, 0, VALID_FILE_LENGTH)
                    .asShortBuffer();
            this.buffer = b;
            this.extent = determineExtent(file);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Error: Can't create ShortBuffer");
        }
    }

    @Override
    public void close() throws Exception {
        buffer = null;
    }

    @Override
    public Interval2D extent() {
        return extent;
    }

    @Override
    public double elevationSample(int x, int y) {
        checkArgument(extent.contains(x, y));
        int indexX = x - extent.iX().includedFrom();
        int indexY = extent.iY().includedTo() - y;
        double result = buffer.get(indexX + indexY * 3601);
        return result;
    }

    /**
     * Checks if the file is valid, throws an {@link IllegalArgumentException}
     * if it is not
     * 
     * @param file
     *            the file that needs to be checked
     * @return a boolean value which represents whether the file is valid or not
     */
    private boolean fileIsValid(File file) {
        String path = file.getName();
        checkArgument(path.length() >= VALID_FILENAME_LENGTH,
                "Error: file name is too short");

        String name = path.substring(path.length() - VALID_FILENAME_LENGTH);
        checkArgument(name.length() == VALID_FILENAME_LENGTH,
                "Error: invalid file name length");

        checkArgument(name.charAt(0) == 'N' || name.charAt(0) == 'S',
                "Error: invalid latitude letter");

        checkArgument(name.charAt(3) == 'E' || name.charAt(3) == 'W',
                "Error: invalid longitude letter");

        String latitude = name.substring(1, 3);
        checkArgument(latitude.matches("^[0-9]*$"),
                "Error: invalid latitude format in file name");

        String longitude = name.substring(4, 7);
        checkArgument(longitude.matches("^[0-9]*$"),
                "Error: invalid longitude format in file name");

        String extension = name.substring(7);
        checkArgument(extension.equals(VALID_FILE_EXTENSION),
                "Error: invalid extension in file");

        long length = file.length();
        checkArgument(length == VALID_FILE_LENGTH,
                "Error: wrong data length in file.");
        
        return true;
    }

    /**
     * Determine the extent described by the file, throws an
     * {@link IllegalArgumentException} if the file is incorrectly formated
     * 
     * @param file
     *            file that contains the data
     * @return an {@link Interval2D} of the extent
     */
    private Interval2D determineExtent(File file) {
        String path = file.getName();
        String name = path.substring(path.length() - VALID_FILENAME_LENGTH);

        int lat = 0;
        int lon = 0;
        try {
            lat = Integer.parseInt(name.substring(1, 3));
            lon = Integer.parseInt(name.substring(4, 7));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Error: illegal latitude/longitude format in file name");
        }

        if (name.charAt(0) == 'S') {
            lat *= -1;
        }
        if (name.charAt(3) == 'W') {
            lon *= -1;
        }
        int lonIndex = lon * SAMPLES_PER_DEGREE;
        int latIndex = lat * SAMPLES_PER_DEGREE;

        return new Interval2D(
                new Interval1D(lonIndex, lonIndex + SAMPLES_PER_DEGREE),
                new Interval1D(latIndex, latIndex + SAMPLES_PER_DEGREE));
    }
}
