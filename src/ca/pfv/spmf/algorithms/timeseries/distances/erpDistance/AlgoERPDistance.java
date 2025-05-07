package ca.pfv.spmf.algorithms.timeseries.distances.erpDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.MatrixBasedDistanceMeasure;
import ca.pfv.spmf.tools.MemoryLogger;

import java.util.Arrays;

/**
 *  ported from UEA Time Series Classification repository
 */

public class AlgoERPDistance extends MatrixBasedDistanceMeasure {

    /**
     * Calculate the ERP distance between two time series.
     * @param timeSeries1 the first time series
     * @param timeSeries2 the second time series
     * @param limit the limit for the distance
     * @return the DTW distance
     */




    public static final String WINDOW_FLAG = "w";
    public static final String G_FLAG = "g";


    /** the time the algorithm started */
    long startTimestamp = 0;
    /** the time the algorithm terminated */
    long endTimestamp = 0;
    private double g = 3;
    private double window = 1;

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double cost(final TimeSeries a, final int aIndex) {

            final double aValue = a.get(aIndex);
            final double sqDiff = StrictMath.pow(aValue - g, 2);


        return sqDiff;
    }

    public double cost(TimeSeries a, int aIndex, TimeSeries b, int bIndex) {


            final double aValue = a.get(aIndex);
            final double bValue = b.get(bIndex);
            final double sqDiff = StrictMath.pow(aValue - bValue, 2);


        return sqDiff;
    }


    public double runAlgorithm(TimeSeries a, TimeSeries b, final double limit) {
        startTimestamp = System.currentTimeMillis();


        // make a the longest time series
        if(a.size() < b.size()) {
            TimeSeries tmp = a;
            a = b;
            b = tmp;
        }

        final int aLength = a.size();
        final int bLength = b.size();
        setup(aLength, bLength, true);

        // step is the increment of the mid point for each row
        final double step = (double) (bLength - 1) / (aLength - 1);
        final double windowSize = this.window * bLength;

        // row index
        int i = 0;

        // start and end of window
        int start = 0;
        double mid = 0;
        int end = Math.min(bLength - 1, (int) Math.floor(windowSize));
        int prevEnd; // store end of window from previous row to fill in shifted space with inf
        double[] row = getRow(i);
        double[] prevRow;

        // col index
        int j = start;
        // process top left sqaure of mat
        double min = row[j++] = 0; // top left cell is always zero
        // compute the first row
        for(; j <= end; j++) {
            row[j] = row[j - 1] + cost(b, j);
            min = Math.min(min, row[j]);
        }
        if(min > limit) return Double.POSITIVE_INFINITY; // quit if beyond limit
        i++;

        // process remaining rows
        for(; i < aLength; i++) {
            // reset min for the row
            min = Double.POSITIVE_INFINITY;
            // change rows
            prevRow = row;
            row = getRow(i);

            // start, end and mid of window
            prevEnd = end;
            mid = i * step;
            // if using variable length time series and window size is fractional then the window may part cover an
            // element. Any part covered element is truncated from the window. I.e. mid point of 5.5 with window of 2.3
            // would produce a start point of 2.2. The window would start from index 3 as it does not fully cover index
            // 2. The same thing happens at the end, 5.5 + 2.3 = 7.8, so the end index is 7 as it does not fully cover 8
            start = Math.max(0, (int) Math.ceil(mid - windowSize));
            end = Math.min(bLength - 1, (int) Math.floor(mid + windowSize));
            j = start;

            // set the values above the current row and outside of previous window to inf
            Arrays.fill(prevRow, prevEnd + 1, end + 1, Double.POSITIVE_INFINITY);
            // set the value left of the window to inf
            if(j > 0) row[j - 1] = Double.POSITIVE_INFINITY;

            // if assessing the left most column then only mapping option is top - not left or topleft
            if(j == 0) {
                row[j] = prevRow[j] + cost(a, i);
                min = Math.min(min, row[j++]);
            }

            // compute the distance for each cell in the row
            for(; j <= end; j++) {
                final double topLeft = prevRow[j - 1] + cost(a, i, b, j);
                final double left = row[j - 1] + cost(b, j);
                final double top = prevRow[j] + cost(a, i);
                if(topLeft > left && left < top) {
                    // del
                    row[j] = left;
                } else if(topLeft > top && top < left) {
                    // ins
                    row[j] = top;
                } else {
                    // match
                    row[j] = topLeft;
                }
                min = Math.min(min, row[j]);
            }

            if(min > limit) return Double.POSITIVE_INFINITY; // quit if beyond limit
        }

        // last value in the current row is the distance
        final double distance = row[bLength - 1];

        endTimestamp = System.currentTimeMillis();
        teardown();
        return distance;
    }

    public double getWindow() {
        return window;
    }

    public void setWindow(final double window) {
        this.window = (window);
    }


    public void printStats() {
        System.out.println("=============  Transform to Exponential Smoothing v2.21- STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }
}