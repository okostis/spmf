package ca.pfv.spmf.algorithms.timeseries.sax;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

public class MainTestESAX_SingleTimeSeries {

    public static void main(String [] arg) throws Exception {

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

        System.out.println("--------Running Tests---------");
        testESAXFunctionality();
        testKnownSequence();
        testRunAlgorithmSimpleCase();
        testTooManySegments();
        testTooFewSegments();
        testInvalidSymbolsTooFew();
        testInvalidSymbolsTooMany();
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTestESAX_SingleTimeSeries.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }

    private static AlgoESAX algo= new AlgoESAX();


    public static void testESAXFunctionality() throws Exception {
        double[] data = {1, 7, 3, 4, 9, 8, 2, 6, 5, 10};
        TimeSeries timeSeries = new TimeSeries(data, "Test Series");

        int numberOfSegments = 2;
        int numberOfSymbols = 4;

        SAXSymbol[][] result = algo.runAlgorithm(timeSeries, numberOfSegments, numberOfSymbols);


        if(result.length != 2){
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Should be 2 segments in the result.");
            return;
        }

        if(result[0].length != 3){
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Each segment should have a triplet of 3 symbols.");
            return;
        }

        if (result[0][0].symbol != 1) {
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Segment 1, Symbol 1 should be '1'.");
            return;
        }
        if (result[0][1].symbol != 2) {
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Segment 1, Symbol 2 should be '2'.");
            return;
        }
        if (result[0][2].symbol != 4) {
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Segment 1, Symbol 3 should be '4'.");
            return;
        }


        if (result[1][0].symbol != 1) {
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Segment 2, Symbol 1 should be '1'.");
            return;
        }
        if (result[1][1].symbol != 3) {
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Segment 2, Symbol 2 should be '3'.");
            return;
        }
        if (result[1][2].symbol != 4) {
            System.out.println("testESAXFunctionality(1) Failed");
            System.out.println("Segment 2, Symbol 3 should be '4'.");
            return;
        }


        System.out.println("testESAXFunctionality(1) Passed");
    }

    public static void testKnownSequence() throws Exception {


        double[] data = {0, 0, 0, 10, 10, 10};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        SAXSymbol[][] result = algo.runAlgorithm(ts, 2, 2);

        if (result.length != 2) {
            System.out.println("testKnownSequence(2) Failed");
            System.out.println("Result length should be 2.");
            return;
        }

        for (SAXSymbol sym : result[0]) {
            if (sym.symbol != 1) {
                System.out.println("testKnownSequence(2) Failed");
                System.out.println("Expected lowest symbol(2) for first segment.");
                return;
            }
        }

        for (SAXSymbol sym : result[1]) {
            if (sym.symbol != 2) {
                System.out.println("testKnownSequence(2) Failed");
                System.out.println("Expected highest symbol (2) for second segment.");
                return;
            }
        }

        System.out.println("testKnownSequence(2) Passed");
    }


    public static void testRunAlgorithmSimpleCase() throws Exception {
        double[] data = {1, 2, 3, 4, 5, 6};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        SAXSymbol[][] result = algo.runAlgorithm(ts, 3, 5);

        // Expect 3 segments, each with 3 symbols (mean, min, max)
        if (result.length != 3) {
            System.out.println("Did not found the expected length");
            System.out.println("testRunAlgorithmSimpleCase(3) Failed");
            return;
        }

        for (SAXSymbol[] segment : result) {
            if (segment.length != 3) {
                System.out.println("testRunAlgorithmSimpleCase(3) Failed");
                return;
            }
            for (SAXSymbol sym : segment) {
                if (sym == null) {
                    System.out.println("testRunAlgorithmSimpleCase(3) Failed");
                    return;
                }
            }
        }
        System.out.println("testRunAlgorithmSimpleCase(3) Passed");

    }


    public static void testTooManySegments() throws  IOException {
        double[] data = {1, 2, 3};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        try {
            algo.runAlgorithm(ts, 5, 4);
        }
        catch (IllegalArgumentException e) {
            System.out.println("testTooManySegments(4) Passed");
            assert(e.getMessage().equals("Number of segments must be <= number of data points"));
            return;
        }
        System.out.println("testTooManySegments(4) Failed");
    }


    public static void testTooFewSegments() throws IOException {
        double[] data = {1, 2, 3, 4};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        try {
            algo.runAlgorithm(ts, 1, 4);
        }
        catch(IllegalArgumentException e){
            System.out.println("testTooFewSegments(5) Passed");
            return;
        }
        System.out.println("testTooFewSegments(5) Failed");
    }

    public static void testInvalidSymbolsTooFew() throws IOException {
        double[] data = {1, 2, 3, 4};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");


         try{
             algo.runAlgorithm(ts, 2, 1);
         }
         catch (IllegalArgumentException e) {
             assert(e.getMessage().equals("Symbols must be between 2 and 30"));
             System.out.println("testInvalidSymbolsTooFew(6) Passed");
             return;
         }
         System.out.println("testInvalidSymbolsTooFew(6) Failed");
    }


    public static void testInvalidSymbolsTooMany() throws IOException {
        double[] data = {1, 2, 3, 4};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        try {
            algo.runAlgorithm(ts, 2, 50);
        }
        catch (IllegalArgumentException e){
            assert(e.getMessage().equals("Symbols must be between 2 and 30")): e.getMessage();
            System.out.println("testInvalidSymbolsTooMany(7) Passed");
            return;
        }
        System.out.println("testInvalidSymbolsTooMany(7) Failed");

    }


}
