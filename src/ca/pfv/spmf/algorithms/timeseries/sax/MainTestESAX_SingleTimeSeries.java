package ca.pfv.spmf.algorithms.timeseries.sax;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

/**
 * Example of how to use ESAX algorithm for converting a time series
 * to the Extended SAX representation, in the source code.
 *
 * Based on:
 * Lkhagva et al. "Extended SAX: Extension of Symbolic Aggregate Approximation
 * for Financial Time Series Data Representation" (2006).
 *
 * @author Konstantinos Evangelou
 */
public class MainTestESAX_SingleTimeSeries {

    public static void main(String [] arg) throws IOException{

        int numberOfSegments = 3;
        int numberOfSymbols = 3;

        // Create a time series
        double [] timeSeriesData = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        TimeSeries timeSeries = new TimeSeries(timeSeriesData, "SERIES1");

        // Apply the ESAX algorithm
        AlgoESAX algorithm = new AlgoESAX();
        SAXSymbol[][] esaxSequence = algorithm.runAlgorithm(timeSeries, numberOfSegments, numberOfSymbols);
        algorithm.printStats();

        // Print the list of ESAX symbols
        SAXSymbol[] symbols = algorithm.symbols;
        System.out.println(" ESAX SYMBOLS: ");
        System.out.println(" Symbols : " + Arrays.toString(symbols) + System.lineSeparator());

        // Print the ESAX sequences
        System.out.println(" ESAX SEQUENCE : ");
        for (int i = 0; i < esaxSequence.length; i++) {
            System.out.println(" Segment " + (i+1) + " : " + Arrays.toString(esaxSequence[i]));
        }
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTestESAX_SingleTimeSeries.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
