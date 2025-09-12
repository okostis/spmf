package ca.pfv.spmf.algorithms.timeseries.distances.ManhattanDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestManhattanDistance {

    private static AlgoManhattanDistance algorithm;


    public static void main(String [] arg) throws Exception {

        // the smoothing constant (a double representing a percentage between 0 and 1)
        double alpha = 1;

        // Create a time series
        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};;
        double [] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};;
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        // Applying the  algorithm
        algorithm = new AlgoManhattanDistance();
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
        algorithm.printStats();

        // Print the moving average
        System.out.println(" Manhattan Distance: ");
        System.out.println(distance);

        System.out.println("\nRunning tests...");
        testKnownDistance();
        testKnownUnequalLengthDistance();
        testEmptyTimeSeriesInput();
        testDistanceSymmetry();
        testNegativeValues();
        testVerySmallDifferences();

    }

    public static void testKnownDistance() {
        try{
            double[] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
            double[] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
            TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
            TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

            double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
            double expected = 8.0; // Example expected value
            if(Math.abs(expected - distance) > 0.0001){
                throw new Exception("Distance calculation did not match expected value");
            }
            System.out.println("Test Known Distance passed(1)");
        } catch (Exception e) {
            System.out.println("Test Known Distance failed(1)");
            System.out.println(e.getMessage());
        }
    }

    public static void testKnownUnequalLengthDistance() {
        try{
            double[] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0};
            double[] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

            TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
            TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

            Exception exception = null;
            try {
                algorithm.runAlgorithm(timeSeries1, timeSeries2);
            } catch (Exception e) {
                exception = e;
            }

            if (exception == null) {
                throw new Exception("Expected an exception for unequal length TimeSeries");
            }

            if (!"The two time series must have the same size.".equals(exception.getMessage())) {
                throw new Exception("Unexpected exception message: " + exception.getMessage());
            }
            System.out.println("Test Known Unequal Length Distance passed(2)");
        } catch (Exception e) {
            System.out.println("Test Known Unequal Length Distance failed(2)");
            System.out.println(e.getMessage());
        }
    }

    public static void testEmptyTimeSeriesInput() {
        try{
            double[] dataPoints2 = {3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

            TimeSeries timeSeries1 = null;
            TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

            Exception exception = null;
            try {
                algorithm.runAlgorithm(timeSeries1, timeSeries2);
            } catch (Exception e) {
                exception = e;
            }

            if (exception == null) {
                throw new Exception("Expected an exception for null TimeSeries input");
            }

            if (!"TimeSeries cannot be null".equals(exception.getMessage())) {
                throw new Exception("Unexpected exception message: " + exception.getMessage());
            }
            System.out.println("Test Empty TimeSeries Input passed(3)");
        } catch (Exception e) {
            System.out.println("Test Empty TimeSeries Input failed(3)");
            System.out.println(e.getMessage());
        }
    }

    public static void testDistanceSymmetry() {
        try{
            double[] dataPoints1 = new double[]{1.0, 2.0, 3.0};
            double[] dataPoints2 = new double[]{4.0, 5.0, 6.0};
            TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
            TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

            double distance1to2 = algorithm.runAlgorithm(timeSeries1, timeSeries2);
            double distance2to1 = algorithm.runAlgorithm(timeSeries2, timeSeries1);

            if(Math.abs(distance1to2 - distance2to1) > 0.0001){
                throw new Exception("Distance should be symmetric: d(A,B) = d(B,A)");
            }
            System.out.println("Test Distance Symmetry passed(4)");
        } catch (Exception e) {
            System.out.println("Test Distance Symmetry failed(4)");
            System.out.println(e.getMessage());
        }
    }

    public static void testNegativeValues() {
        try{
            double[] dataPoints1 = new double[]{-1.0, -2.0, -3.0};
            double[] dataPoints2 = new double[]{-2.0, -3.0, -4.0};
            TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
            TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

            double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
            if(distance < 0 || Double.isNaN(distance)){
                throw new Exception("Distance should be non-negative even with negative inputs");
            }
            System.out.println("Test Negative Values passed(5)");
        } catch (Exception e) {
            System.out.println("Test Negative Values failed(5)");
            System.out.println(e.getMessage());
        }
    }

    public static void testVerySmallDifferences() {
        try{
            double[] dataPoints1 = new double[]{1.0000001, 2.0000001, 3.0000001};
            double[] dataPoints2 = new double[]{1.0000002, 2.0000002, 3.0000002};
            TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
            TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

            double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
            if(!(distance > 0 && distance < 0.0001)){
                throw new Exception("Distance should be appropriately small for tiny differences");
            }
            System.out.println("Test Very Small Differences passed(6)");
        } catch (Exception e) {
            System.out.println("Test Very Small Differences failed(6)");
            System.out.println(e.getMessage());
        }
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestManhattanDistance.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
