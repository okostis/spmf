package ca.pfv.spmf.algorithms.timeseries.autocorrelation.confidenceband;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * An algorithm to calculate the confidence bands for a given time series.
 * This is typically used for plotting confidence bounds on a correlogram (ACF/PACF plot).
 *
 */
public class AlgoConfidenceBand {

    /** the time the algorithm started */
    long startTimestamp = 0;
    /** the time the algorithm terminated */
    long endTimestamp = 0;

    /**
     * Default constructor
     */
    public AlgoConfidenceBand() {
    }

    /**
     * Calculate the confidence bands for a time series at a given confidence level.
     *
     * @param timeSeries a time series
     * @param confidenceLevel the desired confidence level (e.g., 0.90, 0.95, 0.99)
     * @return a two-element array containing the [lower bound, upper bound]
     */
    public double[] runAlgorithm(TimeSeries timeSeries, double confidenceLevel) {
        // record the start time of the algorithm
        startTimestamp = System.currentTimeMillis();
        MemoryLogger.getInstance().reset();

        int n = timeSeries.size();
        if (n <= 0) {
            throw new IllegalArgumentException("Time series must contain at least one data point.");
        }

        double zScore = getZScore(confidenceLevel);

        // Calculate the standard error assuming white noise
        double standardError = 1 / Math.sqrt(n);

        // Calculate the confidence interval bounds
        double upperBound = zScore * standardError;
        double lowerBound = -upperBound;

        // record end time
        endTimestamp = System.currentTimeMillis();
        MemoryLogger.getInstance().checkMemory();

        return new double[]{lowerBound, upperBound};
    }

    /**
     * Returns the Z-score for a given two-sided confidence level.
     *
     * @param confidenceLevel the confidence level (e.g., 0.95 for 95%)
     * @return the corresponding Z-score
     */
    private double getZScore(double confidenceLevel) {
        if (confidenceLevel == 0.90) {
            return 1.645; // Corresponds to alpha = 0.10
        } else if (confidenceLevel == 0.95) {
            return 1.96;  // Corresponds to alpha = 0.05
        } else if (confidenceLevel == 0.99) {
            return 2.576; // Corresponds to alpha = 0.01
        } else {
            throw new IllegalArgumentException("Unsupported confidence level. Please use 0.90, 0.95, or 0.99.");
        }
    }

    public void printStats() {
        System.out.println("=============  Confidence Band Calculation STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }

}