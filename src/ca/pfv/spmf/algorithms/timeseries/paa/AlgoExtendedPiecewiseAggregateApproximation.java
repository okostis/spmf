package ca.pfv.spmf.algorithms.timeseries.paa;

import java.io.IOException;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * Extended PAA for Extended SAX (ESAX)
 * Computes mean, min, max, and their positions for each segment
 * according to the segmentation logic from the original PAA.
 *
 * Based on:
 * Lkhagva et al., "Extended SAX: Extension of Symbolic Aggregate Approximation
 * for Financial Time Series Data Representation" (2006)
 */
public class AlgoExtendedPiecewiseAggregateApproximation {

    /** Structure to store mean, min, max, and positions for a segment */
    public static class SegmentStats {
        public double mean;
        public double min;
        public double max;
        public int posMean; // midpoint index (approximate)
        public int posMin;  // index of min value
        public int posMax;  // index of max value

        public SegmentStats(double mean, double min, double max,
                            int posMean, int posMin, int posMax) {
            this.mean = mean;
            this.min = min;
            this.max = max;
            this.posMean = posMean;
            this.posMin = posMin;
            this.posMax = posMax;
        }
    }

    long startTimestamp = 0;
    long endTimestamp = 0;
    boolean DEBUG_MODE = false;

    public AlgoExtendedPiecewiseAggregateApproximation() {}

    /**
     * Run Extended PAA algorithm on a time series.
     * @param timeSeries a time series
     * @param numberOfSegments number of segments (>1)
     * @return array of SegmentStats objects
     */
    public SegmentStats[] runAlgorithm(TimeSeries timeSeries, int numberOfSegments) throws IOException {
        if (timeSeries.data.length < numberOfSegments) {
            throw new IllegalArgumentException(
                    "Number of segments must be <= number of data points in the time series"
            );
        }
        if (numberOfSegments < 2) {
            throw new IllegalArgumentException(
                    "Number of segments must be > 1"
            );
        }

        MemoryLogger.getInstance().reset();
        startTimestamp = System.currentTimeMillis();

        SegmentStats[] result = transformTimeSeriesToExtendedPAA(timeSeries.data, numberOfSegments);

        MemoryLogger.getInstance().checkMemory();
        endTimestamp = System.currentTimeMillis();
        return result;
    }

    /**
     * Extended PAA: computes mean, min, max, and their positions for each segment.
     */
    private SegmentStats[] transformTimeSeriesToExtendedPAA(double[] dataPoints, int numberOfSegments) {
        SegmentStats[] segments = new SegmentStats[numberOfSegments];

        double segmentSize = dataPoints.length / (double) numberOfSegments;
        double wholePartSegmentSize = Math.floor(segmentSize);
        boolean isExactlyDivisible = segmentSize == (int) segmentSize;

        double currentPoint = 0;

        // FIRST SEGMENT
        double sum = 0;
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        int posMin = 0, posMax = 0;

        for (; currentPoint < wholePartSegmentSize; currentPoint++) {
            double val = dataPoints[(int) currentPoint];
            sum += val;
            if (val < min) { min = val; posMin = (int) currentPoint; }
            if (val > max) { max = val; posMax = (int) currentPoint; }
        }

        if (!isExactlyDivisible) {
            double weight = segmentSize - Math.floor(segmentSize);
            double val = dataPoints[(int) currentPoint];
            sum += val * weight;
            if (val < min) { min = val; posMin = (int) currentPoint; }
            if (val > max) { max = val; posMax = (int) currentPoint; }
            currentPoint += weight;
        }

        double mean = sum / segmentSize;
        int posMean = (posMin + posMax) / 2; // approximate midpoint
        segments[0] = new SegmentStats(mean, min, max, posMean, posMin, posMax);

        // REMAINING SEGMENTS
        for (int currentSegment = 1; currentSegment < numberOfSegments; currentSegment++) {
            sum = 0;
            min = Double.POSITIVE_INFINITY;
            max = Double.NEGATIVE_INFINITY;
            posMin = (int) currentPoint;
            posMax = (int) currentPoint;

            double remainingSegmentSize = segmentSize;
            boolean currentPointIsExactlyDivisible = currentPoint == (int) currentPoint;

            if (!currentPointIsExactlyDivisible) {
                double weight = Math.ceil(currentPoint) - currentPoint;
                double val = dataPoints[(int) currentPoint];
                sum += val * weight;
                if (val < min) { min = val; posMin = (int) currentPoint; }
                if (val > max) { max = val; posMax = (int) currentPoint; }
                currentPoint += weight;
                remainingSegmentSize -= weight;
            }

            while (remainingSegmentSize >= 1 && currentPoint < dataPoints.length) {
                double val = dataPoints[(int) currentPoint];
                sum += val;
                if (val < min) { min = val; posMin = (int) currentPoint; }
                if (val > max) { max = val; posMax = (int) currentPoint; }
                currentPoint++;
                remainingSegmentSize--;
            }

            if (remainingSegmentSize > 0 && currentPoint < dataPoints.length) {
                double weight = remainingSegmentSize;
                double val = dataPoints[(int) currentPoint];
                sum += val * weight;
                if (val < min) { min = val; posMin = (int) currentPoint; }
                if (val > max) { max = val; posMax = (int) currentPoint; }
                currentPoint += weight;
            }

            mean = sum / segmentSize;
            posMean = (posMin + posMax) / 2; // approximate midpoint
            segments[currentSegment] = new SegmentStats(mean, min, max, posMean, posMin, posMax);
        }

        if (DEBUG_MODE) {
            for (int i = 0; i < segments.length; i++) {
                System.out.println("Segment " + i + ": mean=" + segments[i].mean +
                        ", min=" + segments[i].min + " (pos " + segments[i].posMin + ")" +
                        ", max=" + segments[i].max + " (pos " + segments[i].posMax + ")");
            }
        }
        return segments;
    }

    public void printStats() {
        System.out.println("=============  Extended PAA for ESAX - STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }
}
