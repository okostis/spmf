package ca.pfv.spmf.algorithms.timeseries.distances.ManhattanDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AlgoManhattanDistanceTest {

    private AlgoManhattanDistance algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new AlgoManhattanDistance();
    }



    @Test
    void testKnownDistance() throws Exception {

        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double [] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
        double expected = 8.0;
        assertEquals(expected, distance, 0.0001, "Distance calculation should match expected value");
    }

    @Test
    void testKnownUnequalLengthDistance() throws Exception {
        double[] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0};
        double[] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> algorithm.runAlgorithm(timeSeries1, timeSeries2)
        );

        assertEquals("The two time series must have the same size.", exception.getMessage());
    }

    @Test
    void testNullTimeSeriesInput() throws Exception {
        double[] dataPoints2 = {3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

        TimeSeries timeSeries1 = null;
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        Exception exception = assertThrows(
                Exception.class,
                () -> algorithm.runAlgorithm(timeSeries1, timeSeries2)
        );

        assertEquals("TimeSeries cannot be null", exception.getMessage());
    }

    @Test
    void testNegativeValues() throws Exception {
        double[] dataPoints1 = new double[]{-1.0, -2.0, -3.0};
        double[] dataPoints2 = new double[]{-2.0, -3.0, -4.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
        assertTrue(distance >= 0, "Distance should be non-negative even with negative inputs");
        assertFalse(Double.isNaN(distance), "Distance should be a valid number");
    }

    @Test
    void testDistanceSymmetry() throws Exception {
        double[] dataPoints1 = new double[]{1.0, 2.0, 3.0};
        double[] dataPoints2 = new double[]{4.0, 5.0, 6.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance1to2 = algorithm.runAlgorithm(timeSeries1, timeSeries2);
        double distance2to1 = algorithm.runAlgorithm(timeSeries2, timeSeries1);

        assertEquals(distance1to2, distance2to1, 0.0001,
                "Distance should be symmetric: d(A,B) = d(B,A)");
    }

    @Test
    void testVerySmallDifferences() throws Exception {
        double[] dataPoints1 = new double[]{1.0000001, 2.0000001, 3.0000001};
        double[] dataPoints2 = new double[]{1.0000002, 2.0000002, 3.0000002};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
        assertTrue(distance > 0, "Algorithm should detect very small differences");
        assertTrue(distance < 0.01, "Distance should be appropriately small for tiny differences");
    }
}
