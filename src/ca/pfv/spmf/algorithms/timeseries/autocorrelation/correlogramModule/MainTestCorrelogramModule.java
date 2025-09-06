package ca.pfv.spmf.algorithms.timeseries.autocorrelation.correlogramModule;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

import java.io.IOException;
import java.util.Arrays;

public class MainTestCorrelogramModule {

    public static void main(String[] args) throws IOException {
        // Create a sample time series (e.g., with 150 data points)
        double[] data = new double[]{3.0,2.0,8.0,9.0,8.0,9.0,8.0,7.0,6.0,7.0,5.0,4.0,2.0,7.0,9.0,8.0,5.0};
        for(int i=0; i<data.length; i++){
            data[i] = Math.random(); // Using random data for demonstration
        }
        TimeSeries ts = new TimeSeries(data, "Sample Random TS");

        AlgoCorrelogramModule correlogram = new AlgoCorrelogramModule();
        int maxLag = 15;
        double confidenceLevel = 0.95;
        correlogram.runAlgorithm(ts, maxLag, confidenceLevel);

        TimeSeries resultACF = correlogram.getACF();
        TimeSeries resultPACF = correlogram.getPACF();
        double[] bands = correlogram.getConfidenceBands();

        System.out.println("Correlogram Analysis Results for a series of length " + ts.size());
        System.out.println("-------------------------------------------------");
        System.out.println("Confidence Bands at " + (confidenceLevel * 100) + "%: " + Arrays.toString(bands));
        System.out.println("\nACF Values: " + Arrays.toString(resultACF.data));
        System.out.println("\nPACF Values: " + Arrays.toString(resultPACF.data));

        correlogram.printStats();
    }
}
