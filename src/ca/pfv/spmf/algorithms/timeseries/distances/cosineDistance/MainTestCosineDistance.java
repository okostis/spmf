package ca.pfv.spmf.algorithms.timeseries.distances.cosineDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestCosineDistance {

    public static void main(String [] arg) throws Exception {

        // the smoothing constant (a double representing a percentage between 0 and 1)
        double alpha = 1;

        // Create a time series
        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};;
        double [] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};;
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        // Applying the  algorithm
        AlgoCosineDistance algorithm = new AlgoCosineDistance();
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
        algorithm.printStats();

        // Print the moving average
        System.out.println(" Cosine Distance: ");
        System.out.println(distance);

    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestCosineDistance.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
