package ca.pfv.spmf.algorithms.timeseries.distances.ManhattanDistance;

import ca.pfv.spmf.algorithms.clustering.distanceFunctions.DistanceManathan;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.tools.MemoryLogger;

import java.io.IOException;

public class AlgoManhattanDistance {

    /** the time the algorithm started */
    long startTimestamp = 0;
    /** the time the algorithm terminated */
    long endTimestamp = 0;

    DistanceManathan _distanceFunction;
    public AlgoManhattanDistance() {
        _distanceFunction = new DistanceManathan();
    }


    /**
     * Calculate the Manhattan distance between two time series.
     * @param timeSeries1 the first time series
     * @param timeSeries2 the second time series
     * @return the Manhattan distance
     */


    public double runAlgorithm(TimeSeries timeSeries1, TimeSeries timeSeries2) throws IOException {

        startTimestamp = System.currentTimeMillis();
        if (timeSeries1.size() != timeSeries2.size()) {
            throw new IllegalArgumentException("The two time series must have the same size.");
        }


        double result = _distanceFunction.calculateDistance(timeSeries1, timeSeries2);

        endTimestamp = System.currentTimeMillis();
        return  result;
    }


    public void printStats() {
        System.out.println("=============  Transform to Exponential Smoothing v2.21- STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }


}
