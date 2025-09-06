package ca.pfv.spmf.algorithms.timeseries.autocorrelation.correlogramModule;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.AlgoLagAutoCorrelation;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.confidenceband.AlgoConfidenceBand;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.partialautocorrelation.AlgoLagPartialAutoCorrelation;
import ca.pfv.spmf.tools.MemoryLogger;

import java.io.IOException;
import java.util.Arrays;

public class AlgoCorrelogramModule  {

    /** the time the algorithm started */
    long startTimestamp = 0;
    /** the time the algorithm terminated */
    long endTimestamp = 0;

    // To store the results
    private TimeSeries acf;
    private TimeSeries pacf;
    private double[] confidenceBands;

    /**
     * Default constructor
     */
    public AlgoCorrelogramModule() {
    }

    /**
     * Run the correlogram analysis.
     *
     * @param timeSeries      The input time series.
     * @param maxlag          The maximum lag for ACF and PACF calculation.
     * @param confidenceLevel The confidence level for the bands (e.g., 0.95).
     * @throws IOException if an I/O error occurs.
     */
    public void runAlgorithm(TimeSeries timeSeries, int maxlag, double confidenceLevel) throws IOException {
        // record the start time of the algorithm
        startTimestamp = System.currentTimeMillis();
        MemoryLogger.getInstance().reset();

        // 1. Calculate Autocorrelation (ACF)
        AlgoLagAutoCorrelation algoACF = new AlgoLagAutoCorrelation();
        this.acf = algoACF.runAlgorithm(timeSeries, maxlag);

        // 2. Calculate Partial Autocorrelation (PACF)
        // We pass the pre-computed ACF for efficiency
        AlgoLagPartialAutoCorrelation algoPACF = new AlgoLagPartialAutoCorrelation();
        this.pacf = algoPACF.runAlgorithm(timeSeries, maxlag, acf.data);

        // 3. Calculate Confidence Bands
        AlgoConfidenceBand algoBands = new AlgoConfidenceBand();
        this.confidenceBands = algoBands.runAlgorithm(timeSeries, confidenceLevel);

        // record end time
        endTimestamp = System.currentTimeMillis();
        MemoryLogger.getInstance().checkMemory();
    }

    /**
     * Get the resulting Autocorrelation Function (ACF) TimeSeries.
     * @return the ACF as a TimeSeries object.
     */
    public TimeSeries getACF() {
        return acf;
    }

    /**
     * Get the resulting Partial Autocorrelation Function (PACF) TimeSeries.
     * @return the PACF as a TimeSeries object.
     */
    public TimeSeries getPACF() {
        return pacf;
    }

    /**
     * Get the calculated confidence bands.
     * @return a two-element array: [lower bound, upper bound].
     */
    public double[] getConfidenceBands() {
        return confidenceBands;
    }

    /**
     * Print statistics about the latest execution to System.out.
     */
    public void printStats() {
        System.out.println("=============  Correlogram Module STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }
}