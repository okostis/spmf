package ca.pfv.spmf.algorithms.timeseries.autocorrelation.confidenceband;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTestConfidenceBand {

    public static void main(String[] args) {

        double[] data = new double[]{3.0,2.0,8.0,9.0,8.0,9.0,8.0,7.0,6.0,7.0,5.0,4.0,2.0,7.0,9.0,8.0,5.0};

        TimeSeries ts = new TimeSeries(data, "Sample TS");


        AlgoConfidenceBand algo = new AlgoConfidenceBand();

        // Calculate the 95% confidence bands
        double[] bands95 = algo.runAlgorithm(ts, 0.95);
        System.out.println("95% Confidence Bands for a series of length " + ts.size() + ": [" + bands95[0] + ", " + bands95[1] + "]");
        algo.printStats();

        // Calculate the 99% confidence bands
        double[] bands99 = algo.runAlgorithm(ts, 0.99);
        System.out.println("99% Confidence Bands for a series of length " + ts.size() + ": [" + bands99[0] + ", " + bands99[1] + "]");
        algo.printStats();
    }


    @Test
    void testKnownValues() {
        // Create a time series with 100 data points. sqrt(100) = 10.
        TimeSeries ts = new TimeSeries(new double[100], "Test Series");
        AlgoConfidenceBand bandAlgo = new AlgoConfidenceBand();

        double[] bands95 = bandAlgo.runAlgorithm(ts, 0.95);
        assertEquals(-0.196, bands95[0], 0.001); // Lower bound
        assertEquals(0.196, bands95[1], 0.001);  // Upper bound


        double[] bands99 = bandAlgo.runAlgorithm(ts, 0.99);
        assertEquals(-0.2576, bands99[0], 0.001);
        assertEquals(0.2576, bands99[1], 0.001);


        double[] bands90 = bandAlgo.runAlgorithm(ts, 0.90);
        assertEquals(-0.1645, bands90[0], 0.001);
        assertEquals(0.1645, bands90[1], 0.001);

    }

    @Test
    void testSymmetry() {
        TimeSeries ts = new TimeSeries(new double[49], "Symmetry Test Series"); // Use n=49 so sqrt(n)=7
        AlgoConfidenceBand bandAlgo = new AlgoConfidenceBand();
        double[] bands = bandAlgo.runAlgorithm(ts, 0.95);

        assertEquals(bands[1], -bands[0], 0.001);

    }

    @Test
    void testInvalidConfidenceLevel() {
        TimeSeries dummySeries = new TimeSeries(new double[]{1, 2, 3}, "Dummy Series");
        AlgoConfidenceBand bandAlgo = new AlgoConfidenceBand();
        assertThrows(IllegalArgumentException.class, () -> {
            bandAlgo.runAlgorithm(dummySeries, 0.85);
        });


        assertThrows(IllegalArgumentException.class, () -> {
            bandAlgo.runAlgorithm(dummySeries, 1.5);
        });

    }

    @Test
    void testEmptyTimeSeries() {
        TimeSeries emptySeries = new TimeSeries(new double[0], "Empty Series");
        AlgoConfidenceBand bandAlgo = new AlgoConfidenceBand();
        assertThrows(IllegalArgumentException.class, () -> {
            bandAlgo.runAlgorithm(emptySeries, 0.95);
        });

    }
}
