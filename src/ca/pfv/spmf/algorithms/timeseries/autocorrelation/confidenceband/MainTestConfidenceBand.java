package ca.pfv.spmf.algorithms.timeseries.autocorrelation.confidenceband;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

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
}
