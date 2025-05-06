package ca.pfv.spmf.algorithms.timeseries.distances.LCSSDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance.AlgoDTWDistance;
import ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance.MainTestDTWDistance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestLCSSDistance  {
    public static void main(String [] arg) throws IOException {



        // Create a time series
        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double [] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 0.1   ;
        // Applying the  algorithm
        AlgoLCSSDistance algorithm = new AlgoLCSSDistance();
        algorithm.setEpsilon(0.5);
        algorithm.setWindow(0.25);
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, 0.1);
        algorithm.printStats();

        // Print the moving average
        System.out.println(" LCSS Distance: ");
        System.out.println(distance);

    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestDTWDistance.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}