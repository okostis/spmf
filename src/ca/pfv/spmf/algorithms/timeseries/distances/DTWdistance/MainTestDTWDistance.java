package ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTestDTWDistance {

    private static AlgoDTWDistance algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new AlgoDTWDistance();
    }

    @Test
    void testKnownDistance() throws Exception {

        double[] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double[] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double limit = 500   ;
        // Applying the  algorithm
        AlgoDTWDistance algorithm = new AlgoDTWDistance();
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, limit);

        double expected = 14.25; // Example expected value
        assertEquals(expected, distance, 0.0001, "Distance calculation should match expected value");
    }
    @Test
    void testNullTimeSeriesInput() throws Exception {
        double[] dataPoints2 = {3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

        TimeSeries timeSeries1 = null;
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double limit = 500   ;
        Exception exception = assertThrows(
                Exception.class,
                () -> algorithm.runAlgorithm(timeSeries1, timeSeries2,limit)
        );

        assertEquals("TimeSeries cannot be null", exception.getMessage());
    }

    @Test
    void testVerySmallDifferences() throws Exception {
        double[] dataPoints1 = new double[]{1.0000001, 2.0000001, 3.0000001};
        double[] dataPoints2 = new double[]{1.0000002, 2.0000002, 3.0000002};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double limit = 500   ;
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,limit);
        assertTrue(distance > 0, "Algorithm should detect very small differences");
        assertTrue(distance < 0.01, "Distance should be appropriately small for tiny differences");
    }

    @Test
    void testNegativeValues() throws Exception {
        double[] dataPoints1 = new double[]{-1.0, -2.0, -3.0};
        double[] dataPoints2 = new double[]{-2.0, -3.0, -4.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 500   ;
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,limit);
        assertTrue(distance >= 0, "Distance should be non-negative even with negative inputs");
        assertFalse(Double.isNaN(distance), "Distance should be a valid number");
    }

    @Test
    void testDistanceSymmetry() throws Exception {
        double[] dataPoints1 = new double[]{1.0, 2.0, 3.0};
        double[] dataPoints2 = new double[]{4.0, 5.0, 6.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 500   ;
        double distance1to2 = algorithm.runAlgorithm(timeSeries1, timeSeries2,limit);
        double distance2to1 = algorithm.runAlgorithm(timeSeries2, timeSeries1,limit);

        assertEquals(distance1to2, distance2to1, 0.0001,
                "Distance should be symmetric: d(A,B) = d(B,A)");
    }


    public static void main(String [] arg) throws Exception {



        // Create a time series
        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0};
        double [] dataPoints2 = new double[]{1.0, 4.5, 5.0, 6.0, 3.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 500   ;
        // Applying the  algorithm
        algorithm = new AlgoDTWDistance();
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, limit);
        algorithm.printStats();

        // Print the moving average
        System.out.println(" DTW Distance: ");
        System.out.println(distance);

    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestDTWDistance.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
