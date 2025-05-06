package ca.pfv.spmf.algorithms.timeseries.distances.erpDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance.AlgoDTWDistance;
import ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance.MainTestDTWDistance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public class MainTestERPDistance  {
    public static void main(String [] arg) throws IOException {



        // Create a time series
        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0};
        double [] dataPoints2 = new double[]{1.0, 4.5, 5.0, 6.0, 3.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        double limit = 500   ;
        // Applying the  algorithm
        AlgoERPDistance algorithm = new AlgoERPDistance();

        algorithm.setG(0.1);
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, limit);
        algorithm.printStats();

        // Print the moving average
        System.out.println(" ERP Distance: ");
        System.out.println(distance);

    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestDTWDistance.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}