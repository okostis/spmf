package ca.pfv.spmf.algorithms.timeseries.distances.LCSSDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance.MainTestDTWDistance;

import java.io.UnsupportedEncodingException;
import java.net.URL;


public class MainTestLCSSDistance  {

    private static AlgoLCSSDistance algorithm;

//    }

    public static void main(String [] arg) throws Exception {



        // Create a time series
        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double [] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 0.1   ;
        // Applying the  algorithm
        algorithm = new AlgoLCSSDistance();
        algorithm.setEpsilon(0.5);
        algorithm.setWindow(0.25);
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, 0.1);
        algorithm.printStats();

        // Print the moving average
        System.out.println(" LCSS Distance: ");
        System.out.println(distance);

        System.out.println("\nRunning tests...\n");
        testKnownDistance();
        testEmptyTimeSeriesInput();
        testNegativeValues();
        testDistanceSymmetry();
        testVerySmallDifferences();

    }

    public static void testKnownDistance() {
        try{
        double[] dataPoints1 = {1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double[] dataPoints2 = {3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, 0.1);

            double expected = 0.3;
            if(Math.abs(distance - expected) > 0.0001){
                throw new Exception("Distance calculation did not match expected value");
            }
            System.out.println("Test Known Distance passed(1)");
        } catch (Exception e) {
            System.out.println("Test Known Distance failed(1)");
        }
    }

    public static void testEmptyTimeSeriesInput() {
        try{
        double[] dataPoints2 = {3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};

        TimeSeries timeSeries1 = null;
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

            algorithm.runAlgorithm(timeSeries1, timeSeries2, 0.1);
            System.out.println("Test Empty Time Series Input failed(2)");
        } catch (Exception e) {
            if(e.getMessage().equals("TimeSeries cannot be null")){
                System.out.println("Test Empty Time Series Input passed(2)");
            } else {
                System.out.println("Test Empty Time Series Input failed(2)");
            }
        }
    }

    public static void testNegativeValues() {
        try{
        double[] dataPoints1 = new double[]{-1.0, -2.0, -3.0};
        double[] dataPoints2 = new double[]{-2.0, -3.0, -4.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,0.1);
            if(distance < 0 || Double.isNaN(distance)){
                throw new Exception("Distance should be non-negative even with negative inputs");
            }
            System.out.println("Test Negative Values passed(3)");
        } catch (Exception e) {
            System.out.println("Test Negative Values failed(3)");
        }
    }

    public static void testDistanceSymmetry() {
        try{
        double[] dataPoints1 = new double[]{1.0, 2.0, 3.0};
        double[] dataPoints2 = new double[]{4.0, 5.0, 6.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance1to2 = algorithm.runAlgorithm(timeSeries1, timeSeries2,0.1);
        double distance2to1 = algorithm.runAlgorithm(timeSeries2, timeSeries1,0.1);

            if(Math.abs(distance1to2 - distance2to1) > 0.0001){
                throw new Exception("Distance should be symmetric: d(A,B) = d(B,A)");
            }
            System.out.println("Test Distance Symmetry passed(4)");
        } catch (Exception e) {
            System.out.println("Test Distance Symmetry failed(4)");
        }
    }

    public static void testVerySmallDifferences() {
        try{
        double[] dataPoints1 = new double[]{1.0000001, 2.0000001, 3.0000001};
        double[] dataPoints2 = new double[]{1.0000002, 2.0000002, 3.0000002};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        algorithm.setEpsilon(0.00000001);
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2,0.1);
            if(!(distance > 0)){
                throw new Exception("Algorithm should detect very small differences");
            }
            System.out.println("Test Very Small Differences passed(5)");
        } catch (Exception e) {
            System.out.println("Test Very Small Differences failed(5)");
        }
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestDTWDistance.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}