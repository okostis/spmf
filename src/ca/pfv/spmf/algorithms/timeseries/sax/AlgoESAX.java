package ca.pfv.spmf.algorithms.timeseries.sax;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.paa.AlgoExtendedPiecewiseAggregateApproximation;
import ca.pfv.spmf.algorithms.timeseries.paa.AlgoExtendedPiecewiseAggregateApproximation.SegmentStats;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * Implementation of Extended SAX (ESAX) as described in:
 * Lkhagva et al., "Extended SAX: Extension of Symbolic Aggregate Approximation
 * for Financial Time Series Data Representation" (2006)
 *
 * This version uses mean, min, and max values per segment,
 * orders them according to their temporal position,
 * and maps them to symbols using SAX breakpoints.
 *
 * @author Konstantinos Evangelou
 */
public class AlgoESAX {

    long startTimestamp = 0;
    long endTimestamp = 0;
    boolean DEBUG_MODE = false;

    SAXSymbol[] symbols; // Breakpoint-based symbols
    int lastSymbol = 0;
    int timeSeriesCount = 0;

    public static final int MIN_NUMBER_OF_SYMBOLS = 2;
    public static final int MAX_NUMBER_OF_SYMBOLS = 30;

    public AlgoESAX() {}

    /**
     * Run Extended SAX on a single time series
     */
    public SAXSymbol[][] runAlgorithm(TimeSeries timeSeries, int numberOfSegments, int numberOfSymbols) throws IOException {
        if (timeSeries.data.length < numberOfSegments) {
            throw new IllegalArgumentException("Number of segments must be <= number of data points");
        }
        if (numberOfSegments < 2) {
            throw new IllegalArgumentException("Number of segments must be > 1");
        }
        if (numberOfSymbols < MIN_NUMBER_OF_SYMBOLS || numberOfSymbols > MAX_NUMBER_OF_SYMBOLS) {
            throw new IllegalArgumentException("Symbols must be between " + MIN_NUMBER_OF_SYMBOLS + " and " + MAX_NUMBER_OF_SYMBOLS);
        }

        MemoryLogger.getInstance().reset();
        startTimestamp = System.currentTimeMillis();
        lastSymbol = 0;

        // Compute statistics for normalization
        double avg = 0, var = 0;
        for (double v : timeSeries.data) avg += v;
        avg /= timeSeries.data.length;
        for (double v : timeSeries.data) var += Math.pow(avg - v, 2);
        double stdev = Math.sqrt(var / timeSeries.data.length);

        symbols = createSAXSymbols(numberOfSymbols, avg, stdev);

        // Extended PAA
        AlgoExtendedPiecewiseAggregateApproximation paa = new AlgoExtendedPiecewiseAggregateApproximation();
        SegmentStats[] segments = paa.runAlgorithm(timeSeries, numberOfSegments);

        // Transform to ESAX sequence
        SAXSymbol[][] esaxSeq = new SAXSymbol[segments.length][3];
        for (int i = 0; i < segments.length; i++) {
            SAXSymbol sMean = mapValueToSymbol(segments[i].mean);
            SAXSymbol sMin  = mapValueToSymbol(segments[i].min);
            SAXSymbol sMax  = mapValueToSymbol(segments[i].max);

            esaxSeq[i] = orderTriple(sMax, sMean, sMin,
                    segments[i].posMax, segments[i].posMean, segments[i].posMin);
        }

        timeSeriesCount = 1;
        MemoryLogger.getInstance().checkMemory();
        endTimestamp = System.currentTimeMillis();
        return esaxSeq;
    }

    /**
     * Map a numeric value to a SAX symbol
     */
    private SAXSymbol mapValueToSymbol(double value) {
        for (SAXSymbol symbol : symbols) {
            if (value >= symbol.lowerBound && value < symbol.upperBound) {
                return symbol;
            }
        }
        return symbols[symbols.length - 1];
    }

    /**
     * Run Extended SAX on multiple time series
     */
    public SAXSymbol[][][] runAlgorithm(List<TimeSeries> multipleTimeSeries, int numberOfSegments,
                                        int numberOfSymbols) throws IOException {
        if (numberOfSegments < 2) {
            throw new IllegalArgumentException("Number of segments must be > 1");
        }
        if (numberOfSymbols < MIN_NUMBER_OF_SYMBOLS || numberOfSymbols > MAX_NUMBER_OF_SYMBOLS) {
            throw new IllegalArgumentException("Symbols must be between " + MIN_NUMBER_OF_SYMBOLS + " and " + MAX_NUMBER_OF_SYMBOLS);
        }

        MemoryLogger.getInstance().reset();
        startTimestamp = System.currentTimeMillis();
        lastSymbol = 0;

        // Compute global avg and stdev
        double avg = 0, var = 0;
        double count = 0;
        for (TimeSeries ts : multipleTimeSeries) {
            for (double v : ts.data) {
                avg += v;
                count++;
            }
        }
        avg /= count;
        for (TimeSeries ts : multipleTimeSeries) {
            for (double v : ts.data) {
                var += Math.pow(avg - v, 2);
            }
        }
        double stdev = Math.sqrt(var / count);

        symbols = createSAXSymbols(numberOfSymbols, avg, stdev);

        timeSeriesCount = multipleTimeSeries.size();
        SAXSymbol[][][] results = new SAXSymbol[timeSeriesCount][][];

        AlgoExtendedPiecewiseAggregateApproximation paa = new AlgoExtendedPiecewiseAggregateApproximation();

        for (int i = 0; i < multipleTimeSeries.size(); i++) {
            TimeSeries ts = multipleTimeSeries.get(i);

            if (DEBUG_MODE) {
                System.out.println("Processing time series " + i + ": " + ts);
            }

            SegmentStats[] segments = paa.runAlgorithm(ts, numberOfSegments);

            SAXSymbol[][] esaxSeq = new SAXSymbol[segments.length][3];
            for (int seg = 0; seg < segments.length; seg++) {
                SAXSymbol sMean = mapValueToSymbol(segments[seg].mean);
                SAXSymbol sMin  = mapValueToSymbol(segments[seg].min);
                SAXSymbol sMax  = mapValueToSymbol(segments[seg].max);

                esaxSeq[seg] = orderTriple(sMax, sMean, sMin,
                        segments[seg].posMax, segments[seg].posMean, segments[seg].posMin);
            }

            results[i] = esaxSeq;
        }

        MemoryLogger.getInstance().checkMemory();
        endTimestamp = System.currentTimeMillis();
        return results;
    }



    /**
     * Order triple according to Eq. 3 in the ESAX paper
     */
    private SAXSymbol[] orderTriple(SAXSymbol sMax, SAXSymbol sMean, SAXSymbol sMin,
                                    int pMax, int pMean, int pMin) {
        if (pMax < pMean && pMean < pMin) return new SAXSymbol[]{sMax, sMean, sMin};
        if (pMin < pMean && pMean < pMax) return new SAXSymbol[]{sMin, sMean, sMax};
        if (pMin < pMax && pMax < pMean) return new SAXSymbol[]{sMin, sMax, sMean};
        if (pMax < pMin && pMin < pMean) return new SAXSymbol[]{sMax, sMin, sMean};
        if (pMean < pMax && pMax < pMin) return new SAXSymbol[]{sMean, sMax, sMin};
        return new SAXSymbol[]{sMean, sMin, sMax};
    }

    /**
     * Create SAX symbols based on mean and stdev
     */
    private SAXSymbol[] createSAXSymbols(int numberOfSymbols, double average, double stdev) {
        SAXSymbol[] symbols = new SAXSymbol[numberOfSymbols];
        double[] breakpoints = getBreakpoints(numberOfSymbols);

        for (int i = 0; i < numberOfSymbols; i++) {
            double lowerBound = average + (stdev * breakpoints[i]);
            double upperBound = average + (stdev * breakpoints[i + 1]);
            int symbolName = ++lastSymbol;
            symbols[i] = new SAXSymbol(symbolName, lowerBound, upperBound);
        }

        if (DEBUG_MODE) {
            System.out.println("Symbols: " + Arrays.toString(symbols));
        }
        return symbols;
    }

    public SAXSymbol[] getSymbols() {
        return symbols;
    }

    /**
     * Breakpoints table from SAX paper
     */
    private double[] getBreakpoints(int numberOfSymbols) {
        AlgoSAX sax = new AlgoSAX();
        return sax.getBreakpoints(numberOfSymbols);
    }

    public void printStats() {
        System.out.println("=============  Extended SAX  - STATS =============");
        System.out.println(" Number of time series processed: " + timeSeriesCount);
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }
}
