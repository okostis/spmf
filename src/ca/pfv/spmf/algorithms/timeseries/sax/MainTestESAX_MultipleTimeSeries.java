package ca.pfv.spmf.algorithms.timeseries.sax;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTestESAX_MultipleTimeSeries {

    public static void main(String [] arg) throws IOException {

        int numberOfSegments = 2;
        int numberOfSymbols = 3;

        // Create several time series
        List<TimeSeries> timeSeries = new ArrayList<TimeSeries>();
        TimeSeries timeSeries1 = new TimeSeries(
                new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0},
                "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(
                new double[]{10,9,8,7,6,5},
                "SERIES2");
        TimeSeries timeSeries3 = new TimeSeries(
                new double[]{-1,-2,-3,-4,-5},
                "SERIES3");
        TimeSeries timeSeries4 = new TimeSeries(
                new double[]{-1.0,-2.0,-3.0,-4.0,-5.0},
                "SERIES4");

        timeSeries.add(timeSeries1);
        timeSeries.add(timeSeries2);
        timeSeries.add(timeSeries3);
        timeSeries.add(timeSeries4);

        // Applying the algorithm
        AlgoESAX algorithm = new AlgoESAX();
        SAXSymbol[][][] esaxSequences = algorithm.runAlgorithm(timeSeries, numberOfSegments, numberOfSymbols);
        algorithm.printStats();

        // Print the list of ESAX symbols
        SAXSymbol[] symbols = algorithm.symbols;
        System.out.println(" ESAX SYMBOLS: ");
        System.out.println(" Symbols : " + Arrays.toString(symbols) + System.lineSeparator());

        // Print the ESAX sequences
        System.out.println(" ESAX SEQUENCES : ");
        for (int i = 0; i < esaxSequences.length; i++) {
            System.out.println(" Time Series: " + timeSeries.get(i).getName());
            for (int j = 0; j < esaxSequences[i].length; j++) {
                System.out.println("  Segment " + (j+1) + " : " + Arrays.toString(esaxSequences[i][j]));
            }
            System.out.println();
        }
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestESAX_MultipleTimeSeries.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}