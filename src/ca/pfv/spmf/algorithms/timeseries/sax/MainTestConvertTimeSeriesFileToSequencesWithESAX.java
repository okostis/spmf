package ca.pfv.spmf.algorithms.timeseries.sax;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;




/**
 * Example of how to use the class "AlgoConvertTimeSeriesFileToSequencesWithESAX"
 * to convert a set of time series into ESAX sequences and write them to a file
 * in the SPMF format.
 *
 * @author Konstantinos Evangelou
 */
public class MainTestConvertTimeSeriesFileToSequencesWithESAX {

    public static void main(String [] arg) throws IOException {

        int numberOfSegments = 3;
        int numberOfSymbols = 4;

        // Create a few toy time series
        List<TimeSeries> timeSeriesList = new ArrayList<TimeSeries>();
        TimeSeries ts1 = new TimeSeries(
                new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0},
                "SERIES1");
        TimeSeries ts2 = new TimeSeries(
                new double[]{10,9,8,7,6,5},
                "SERIES2");
        TimeSeries ts3 = new TimeSeries(
                new double[]{-1,-2,-3,-4,-5},
                "SERIES3");
        timeSeriesList.add(ts1);
        timeSeriesList.add(ts2);
        timeSeriesList.add(ts3);

        // Path to save the converted file
        String output = "output_esax.txt";

        // Run the converter
        AlgoConvertTimeSeriesFileToSequencesWithESAX converter = new AlgoConvertTimeSeriesFileToSequencesWithESAX();
        converter.runAlgorithm(timeSeriesList, output, numberOfSegments, numberOfSymbols);

        // Print stats
        converter.printStats();

        System.out.println("ESAX sequences have been written to: " + output);
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestConvertTimeSeriesFileToSequencesWithESAX.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
