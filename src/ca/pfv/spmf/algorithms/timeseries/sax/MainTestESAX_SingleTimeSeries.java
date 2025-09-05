package ca.pfv.spmf.algorithms.timeseries.sax;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    private AlgoESAX algo;

    @BeforeEach
    void setUp() {
        algo = new AlgoESAX();
    }

    @Test
    @DisplayName("Test ESAX triplet generation against expected values")
    void testESAXFunctionality() throws Exception {

        double[] data = {1, 7, 3, 4, 9, 8, 2, 6, 5, 10};
        TimeSeries timeSeries = new TimeSeries(data, "Test Series");

        int numberOfSegments = 2;
        int numberOfSymbols = 4;




        SAXSymbol[][] result = algo.runAlgorithm(timeSeries, numberOfSegments, numberOfSymbols);


        assertEquals(2, result.length, "Should be 2 segments in the result.");
        assertEquals(3, result[0].length, "Each segment should have a triplet of 3 symbols.");

        // Assert Segment 1 triplet symbols
        assertEquals(1, result[0][0].symbol, "Segment 1, Symbol 1 should be '1'.");
        assertEquals(2, result[0][1].symbol, "Segment 1, Symbol 2 should be '2'.");
        assertEquals(4, result[0][2].symbol, "Segment 1, Symbol 3 should be '4'.");

        // Assert Segment 2 triplet symbols
        assertEquals(1, result[1][0].symbol, "Segment 2, Symbol 1 should be '1'.");
        assertEquals(3, result[1][1].symbol, "Segment 2, Symbol 2 should be '3'.");
        assertEquals(4, result[1][2].symbol, "Segment 2, Symbol 3 should be '4'.");

        System.out.println("Test completed successfully!");
    }
    @Test
    void testKnownSequence() throws Exception {
        // First half low values, second half high values
        double[] data = {0, 0, 0, 10, 10, 10};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        AlgoESAX algo = new AlgoESAX();
        SAXSymbol[][] result = algo.runAlgorithm(ts, 2, 2);

        assertEquals(2, result.length);

        // First segment should map to lowest symbol (0)
        for (SAXSymbol sym : result[0]) {
            assertEquals(1, sym.symbol, "Expected lowest symbol for first segment");
        }

        // Second segment should map to highest symbol (1)
        for (SAXSymbol sym : result[1]) {
            assertEquals(2, sym.symbol, "Expected highest symbol for second segment");
        }
    }

    @Test
    void testRunAlgorithmSimpleCase() throws Exception {
        double[] data = {1, 2, 3, 4, 5, 6};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        AlgoESAX algo = new AlgoESAX();
        SAXSymbol[][] result = algo.runAlgorithm(ts, 3, 5);

        // Expect 3 segments, each with 3 symbols (mean, min, max)
        assertEquals(3, result.length);
        for (SAXSymbol[] segment : result) {
            assertEquals(3, segment.length);
            for (SAXSymbol sym : segment) {
                assertNotNull(sym);
            }
        }
    }

    @Test
    void testTooManySegments() {
        double[] data = {1, 2, 3};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        AlgoESAX algo = new AlgoESAX();
        assertThrows(IllegalArgumentException.class,
                () -> algo.runAlgorithm(ts, 5, 4));
    }

    @Test
    void testTooFewSegments() {
        double[] data = {1, 2, 3, 4};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        AlgoESAX algo = new AlgoESAX();
        assertThrows(IllegalArgumentException.class,
                () -> algo.runAlgorithm(ts, 1, 4));
    }

    @Test
    void testInvalidSymbolsTooFew() {
        double[] data = {1, 2, 3, 4};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        AlgoESAX algo = new AlgoESAX();
        assertThrows(IllegalArgumentException.class,
                () -> algo.runAlgorithm(ts, 2, 1));
    }

    @Test
    void testInvalidSymbolsTooMany() {
        double[] data = {1, 2, 3, 4};
        TimeSeries ts = new TimeSeries(data,"TEST_SERIES");

        AlgoESAX algo = new AlgoESAX();
        assertThrows(IllegalArgumentException.class,
                () -> algo.runAlgorithm(ts, 2, 50));
    }


}
