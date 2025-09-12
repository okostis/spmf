package ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestDTWDistance {

    private static AlgoDTWDistance algorithm;


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


        System.out.println("Running tests...");
        testKnownDistance();
        testNullTimeSeriesInput();
        testVerySmallDifferences();
        testNegativeValues();
        testDistanceSymmetry();

    }

    public static void testKnownDistance() {
        try{
        double[] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double[] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double limit = 500   ;
        // Applying the  algorithm
        AlgoDTWDistance algorithm = new AlgoDTWDistance();
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, limit);

        double expected = 14.25; // Example expected value
            if(Math.abs(expected - distance) > 0.0001){
                throw new Exception("Distance calculation does not match expected value");
            }
            System.out.println("Test Known Distance passed(1)");
        } catch (Exception e) {
            System.out.println("Test Known Distance failed(1)");
            System.out.println(e.getMessage());
        }
    }

    public static void testNullTimeSeriesInput() {
        try{
        double[] dataPoints2 = {3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

        TimeSeries timeSeries1 = null;
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double limit = 500   ;
        Exception exception = null;
            try {
                algorithm.runAlgorithm(timeSeries1, timeSeries2,limit);
            } catch (Exception e) {
                exception = e;
            }

        if(exception == null || !exception.getMessage().equals("TimeSeries cannot be null")){
            throw new Exception("Expected exception for null TimeSeries input");
        }
        System.out.println("Test Null TimeSeries Input passed(2)");
    } catch (Exception e) {
        System.out.println("Test Null TimeSeries Input failed(2)");
        System.out.println(e.getMessage());
    }
    }

    public static void testVerySmallDifferences() {
        try{
        double[] dataPoints1 = new double[]{1.0000001, 2.0000001, 3.0000001};
        double[] dataPoints2 = new double[]{1.0000002, 2.0000002, 3.0000002};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double limit = 500   ;
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,limit);
        if(!(distance > 0 && distance < 0.0001)){
            throw new Exception("Distance should be appropriately small for tiny differences");
        }
        System.out.println("Test Very Small Differences passed(3)");
    } catch (Exception e) {
        System.out.println("Test Very Small Differences failed(3)");
        System.out.println(e.getMessage());
    }
    }

    public static void testNegativeValues() {
        try{
        double[] dataPoints1 = new double[]{-1.0, -2.0, -3.0};
        double[] dataPoints2 = new double[]{-2.0, -3.0, -4.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 500   ;
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,limit);
        if(!(distance >= 0) || Double.isNaN(distance)){
            throw new Exception("Distance should be non-negative and a valid number even with negative inputs");
        }
        System.out.println("Test Negative Values passed(4)");
    } catch (Exception e) {
        System.out.println("Test Negative Values failed(4)");
        System.out.println(e.getMessage());
    }
    }

    public static void testDistanceSymmetry() {
        try{
        double[] dataPoints1 = new double[]{1.0, 2.0, 3.0};
        double[] dataPoints2 = new double[]{4.0, 5.0, 6.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 500   ;
        double distance1to2 = algorithm.runAlgorithm(timeSeries1, timeSeries2,limit);
        double distance2to1 = algorithm.runAlgorithm(timeSeries2, timeSeries1,limit);

        if(Math.abs(distance1to2 - distance2to1) > 0.0001){
            throw new Exception("Distance is not symmetric: d(A,B) != d(B,A)");
        }
        System.out.println("Test Distance Symmetry passed(5)");
    } catch (Exception e) {
        System.out.println("Test Distance Symmetry failed(5)");
        System.out.println(e.getMessage());
    }
    }


    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestDTWDistance.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
