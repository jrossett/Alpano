package ch.epfl.alpano.gui;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import ch.epfl.alpano.Math2;
import ch.epfl.alpano.PanoramaComputer;
import ch.epfl.alpano.PanoramaParameters;
import ch.epfl.alpano.dem.ContinuousElevationModel;
import ch.epfl.alpano.dem.ElevationProfile;
import ch.epfl.alpano.summit.Summit;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Class used to label the summits in a panorama
 */
public final class Labelizer {

    private final ContinuousElevationModel mnt;
    private final List<Summit> summits;
    private final static int FONT_SIZE = 18;
    private final static int UPPER_BOUND = 170;
    private final static int SIDE_BOUND = 20;
    private final static int TEXT_ORIENTATION = -60;
    private final static int PADDING = 20;

    /**
     * Constructs a Labelizer
     * 
     * @param MNT
     *            {@link ContinuousElevationModel} in which the summits are
     *            located
     * @param summits
     *            list of {@link Summit}
     */
    public Labelizer(ContinuousElevationModel MNT, List<Summit> summits) {
        this.mnt = MNT;
        this.summits = summits;
    }

    /**
     * Creates a list of {@link Node} that can be displayed to label all the
     * visible summits in the image
     * 
     * @param parameters
     *            {@link PanoramaParameters} of the panorama
     * @return list of {@link Node} that label the visible summits in the image
     */
    public List<Node> labelize(PanoramaParameters parameters) {
        List<RepresentableSummit> visibleSummits = visibleSummit(parameters);
        List<Node> nodes = new ArrayList<>();
        int lowestIndex = 0;

        if (visibleSummits.size() != 0) {
            lowestIndex = visibleSummits.get(0).Y();
        }

        Text summitText;
        Line line;
        BitSet invalidXPositions = new BitSet(parameters.width());

        for (RepresentableSummit summit : visibleSummits) {
            if (invalidXPositions.get(summit.X())) {
                continue;
            }

            summitText = new Text(summit.toString());
            summitText.setFont(new Font(FONT_SIZE));
            summitText.getTransforms().addAll(new Translate(summit.X(), lowestIndex - PADDING - 2),
                    new Rotate(TEXT_ORIENTATION, 0, 0));
            line = new Line(summit.X(), summit.Y(), summit.X(), lowestIndex - PADDING);
            nodes.add(summitText);
            nodes.add(line);

            invalidXPositions.set(summit.X() - PADDING + 1, summit.X() + PADDING);
        }
        return nodes;
    }

    /**
     * Filters the summits that are visible in the image
     * 
     * @param parameters
     *            {@link PanoramaParameters} of the panorama
     * @return a list of {@link RepresentableSummit} that are visible in the
     *         image
     */
    public List<RepresentableSummit> visibleSummit(PanoramaParameters parameters) {
        List<RepresentableSummit> result = new ArrayList<RepresentableSummit>();

        ElevationProfile profile;
        DoubleUnaryOperator rayToSummit;
        DoubleUnaryOperator groundDistance;
        double summitAzimuth;
        double summitDistance;
        double angularDifference;
        double slope;
        double yIndex;
        double xIndex;

        for (Summit summit : summits) {
            summitAzimuth = parameters.observerPosition().azimuthTo(summit.position());
            summitDistance = parameters.observerPosition().distanceTo(summit.position());
            angularDifference = Math2.angularDistance(parameters.centerAzimuth(), summitAzimuth);

            if (Math.abs(angularDifference) > parameters.horizontalFieldOfView() / 2d
                    || summitDistance > parameters.maxDistance()) {
                continue;
            }

            profile = new ElevationProfile(mnt, parameters.observerPosition(), summitAzimuth,
                    summitDistance);
            groundDistance = PanoramaComputer.rayToGroundDistance(profile,
                    parameters.observerElevation(), 0);

            slope = Math.atan2(-groundDistance.applyAsDouble(summitDistance), summitDistance);

            if (Math.abs(slope) > parameters.verticalFieldOfView() / 2d) {
                continue;
            }

            rayToSummit = PanoramaComputer.rayToGroundDistance(profile,
                    parameters.observerElevation(), slope);

            if (Math2.firstIntervalContainingRoot(rayToSummit, 0, summitDistance,
                    64) < summitDistance - 200) {
                continue;
            }

            xIndex = parameters.xForAzimuth(summitAzimuth);
            yIndex = parameters.yForAltitude(slope);

            if (yIndex < UPPER_BOUND || xIndex < SIDE_BOUND
                    || xIndex > parameters.width() - SIDE_BOUND - 1) {
                continue;
            }

            result.add(
                    new RepresentableSummit(summit, parameters.xForAzimuth(summitAzimuth), yIndex));
        }

        result.sort((s1, s2) -> {
            int compare = Integer.compare(s1.Y(), s2.Y());
            if (compare == 0) {
                return Integer.compare(s2.summit().elevation(), s1.summit().elevation());
            }
            return compare;
        });
        return result;
    }

    /**
     * Class that is used to represent a summit and its corresponding pixel
     * coordinate on the image
     */
    private final static class RepresentableSummit {
        private final Summit summit;
        private final double indexX;
        private final double indexY;

        /**
         * Constructs a RepresentableSummit
         * 
         * @param summit
         *            {@link Summit} that is represented
         * @param indexX
         *            horizontal pixel index
         * @param indexY
         *            vertical pixel index
         */
        public RepresentableSummit(Summit summit, double indexX, double indexY) {
            this.summit = summit;
            this.indexX = indexX;
            this.indexY = indexY;
        }

        /**
         * Gets the {@link Summit} in the RepresentableSummit
         * 
         * @return the {@link Summit}
         */
        public Summit summit() {
            return summit;
        }

        /**
         * Gets the closest whole horizontal index of the RepresentableSummit
         * 
         * @return the whole horizontal pixel index of the RepresentableSummit
         */
        public int X() {
            return (int) Math.round(indexX);
        }

        /**
         * Gets the closest whole vertical index of the RepresentableSummit
         * 
         * @return the whole vertical pixel index of the RepresentableSummit
         */
        public int Y() {
            return (int) Math.round(indexY);
        }

        @Override
        public String toString() {
            return String.format("%s (%d m)", summit().name(), summit().elevation());
        }
    }
}
