
package ca.pfv.spmf.algorithms.timeseries.autocorrelation.partialautocorrelation;

/**
 *  ported from UEA Time Series Classification repository with minor modifications
 */

import java.io.IOException;
import java.util.Arrays;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.AlgoLagAutoCorrelation;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * An algorithm to calculate the partial autocorrelation time series of a time series
 * The resulting time series contains values in the [-1,1] interval.
 * This implementation uses the Durbin-Levinson recursion algorithm.
 *
 *
 */
public class AlgoLagPartialAutoCorrelation {

    /** the time the algorithm started */
    long startTimestamp = 0;
    /** the time the algorithm terminated */
    long endTimestamp = 0;

    /** This program will execute in DEBUG MODE if this variable is true */
    boolean DEBUG_MODE = false;

    protected double[] autos;
    /** Partial auto correlations, calculated by */
    protected double[][] partials;

    public AlgoLagAutoCorrelation algoACF = new AlgoLagAutoCorrelation();

    /**
     * Default constructor
     */
    public AlgoLagPartialAutoCorrelation() {
    }

    /**
     * Generate the partial autocorrelation time series of a time series for a lag 1 to a lag "maxlag"
     * @param timeSeries a time series
     * @param maxlag a maximum lag
     * @param acf optional precomputed ACF values, if null the ACF will be computed internally
     * @return the resulting partial autocorrelation time series
     * @throws IOException exception if error while writing the file
     */
    public TimeSeries runAlgorithm(TimeSeries timeSeries,  int maxlag,double[] acf) throws IOException {
        // check some error for parameters
        if( maxlag < 1 || maxlag > timeSeries.size()){
            throw new IllegalArgumentException(" The maxlag parameter must be set as follows:  1 <= maxlag <= timeSeries.length");
        }

        // reset memory logger
        MemoryLogger.getInstance().reset();

        // record the start time of the algorithm
        startTimestamp = System.currentTimeMillis();

        // IF in debug mode
        if(DEBUG_MODE){
            // Print the time series
            System.out.println(" Time series: " + Arrays.toString(timeSeries.data));
        }

        // Create an array to store the partial autocorrelation values
        double[] resultingTimeSeriesArray =(acf != null) ? convertInstance(timeSeries.data, maxlag,acf) :
                convertInstance(timeSeries.data, maxlag,null);
        TimeSeries resultingTimeSeries = new TimeSeries(resultingTimeSeriesArray,  timeSeries.getName() + "_PACF");

        // check the memory usage again and close the file.
        MemoryLogger.getInstance().checkMemory();
        // record end time
        endTimestamp = System.currentTimeMillis();

        return resultingTimeSeries;
    }

    private double[] convertInstance(double[] d,int maxlag,double[] acf) throws IOException {
        // 2. Fit Autocorrelations, if not already set externally
        if(acf!=null){
            autos = acf;

        }else {
            autos = algoACF.runAlgorithm(new TimeSeries(d, "TS"), maxlag).data;
        }
        // 3. Form Partials
        partials = formPartials(autos);

        // 5. Find parameters
        double[] pi = new double[maxlag];
        for (int k = 0; k < maxlag; k++) { // Set NANs to zero
            if (Double.isNaN(partials[k][k]) || Double.isInfinite(partials[k][k])) {
                pi[k] = 0;
            } else
                pi[k] = partials[k][k];
        }
        return pi;
    }

    /**
     * Finds partial autocorrelation function using Durban-Leverson recursions
     *
     * @param acf the ACF
     * @return
     */
    public static double[][] formPartials(double[] acf) {
        // Using the Durban-Leverson
        int p = acf.length - 1;
        double[][] phi = new double[p][p];
        double numerator, denominator;
        // We do not need lag 0
        acf =Arrays.copyOfRange(acf, 1, acf.length);
        phi[0][0] = acf[0];

        for (int k = 1; k < p; k++) {
            // Find diagonal k,k
            // Naive implementation, should be able to do with running sums?
            numerator = acf[k];
            for (int i = 0; i < k; i++)
                numerator -= phi[i][k - 1] * acf[k - 1 - i];
            denominator = 1;
            for (int i = 0; i < k; i++)
                denominator -= phi[k - 1 - i][k - 1] * acf[k - 1 - i];
            if (denominator != 0)// What to do otherwise?
                phi[k][k] = numerator / denominator;
            // Find terms 1 to k-1
            for (int i = 0; i < k; i++)
                phi[i][k] = phi[i][k - 1] - phi[k][k] * phi[k - 1 - i][k - 1];
        }
        return phi;
    }

    /**
     * Print statistics about the latest execution to System.out.
     */
    public void printStats() {
        System.out.println("=============  Transform to lag k partial autocorrelation time series v2.21- STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }
}