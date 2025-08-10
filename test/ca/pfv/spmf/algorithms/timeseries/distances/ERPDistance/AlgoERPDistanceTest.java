package ca.pfv.spmf.algorithms.timeseries.distances.ERPDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.erpDistance.AlgoERPDistance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AlgoERPDistanceTest {

    private AlgoERPDistance algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new AlgoERPDistance();
        algorithm.setG(0.1);
    }

    @Test
    void testKnownDistance() throws Exception {

        double[] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double[] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double limit = 500;
        // Applying the algorithm
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, limit);

        double expected = 14.0; // Example expected value
        assertEquals(expected, distance, 0.0001, "Distance calculation should match expected value");
    }
    @Test
    void testEmptyTimeSeriesInput() throws Exception {
        double[] dataPoints2 = {3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

        TimeSeries timeSeries1 = null;
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        Exception exception = assertThrows(
                Exception.class,
                () -> algorithm.runAlgorithm(timeSeries1, timeSeries2, 0.1)
        );

        assertEquals("TimeSeries cannot be null", exception.getMessage());
    }

    @Test
    void testNegativeValues() throws Exception {
        double[] dataPoints1 = new double[]{-1.0, -2.0, -3.0};
        double[] dataPoints2 = new double[]{-2.0, -3.0, -4.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,0.1);
        assertTrue(distance >= 0, "Distance should be non-negative even with negative inputs");
        assertFalse(Double.isNaN(distance), "Distance should be a valid number");
    }

    @Test
    void testDistanceSymmetry() throws Exception {
        double[] dataPoints1 = new double[]{1.0, 2.0, 3.0};
        double[] dataPoints2 = new double[]{4.0, 5.0, 6.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance1to2 = algorithm.runAlgorithm(timeSeries1, timeSeries2,0.1);
        double distance2to1 = algorithm.runAlgorithm(timeSeries2, timeSeries1,0.1);

        assertEquals(distance1to2, distance2to1, 0.0001,
                "Distance should be symmetric: d(A,B) = d(B,A)");
    }

    @Test
    void testVerySmallDifferences() throws Exception {
        double[] dataPoints1 = new double[]{5.0000001, 2.0000001, 3.0000001};
        double[] dataPoints2 = new double[]{5.0000002, 2.0000002, 3.0000002};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,0.1);
        assertTrue(distance > 0, "Algorithm should detect very small differences");
        assertTrue(distance < 0.01, "Distance should be appropriately small for tiny differences");
    }
}
