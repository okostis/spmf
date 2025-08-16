package ca.pfv.spmf.algorithms.timeseries.sax;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * This class reads a time series file and converts it to a sequence of symbols
 * using the Extended SAX (ESAX) algorithm.
 *
 * Format of output is the standard SPMF transactional format:
 * each time series → @NAME, then a sequence of integer symbols separated by -1 ... -2.
 *
 * Based on:
 * Lkhagva et al., "Extended SAX: Extension of Symbolic Aggregate Approximation
 * for Financial Time Series Data Representation" (2006)
 *
 * @author Konstantinos Evangelou
 * @see AlgoESAX
 * @see SAXSymbol
 */
public class AlgoConvertTimeSeriesFileToSequencesWithESAX {

    /** the time the algorithm started */
    long startTimestamp = 0;

    /** the time the algorithm terminated */
    long endTimestamp = 0;

    /** writer to write the output file **/
    BufferedWriter writer = null;

    /** This program will execute in DEBUG MODE if this variable is true */
    boolean DEBUG_MODE = false;

    /** The number of time series in the last file that was read */
    int timeSeriesCount = 0;

    /** The symbols created for the last converted time series */
    SAXSymbol[] symbols;

    /**
     * Default constructor
     */
    public AlgoConvertTimeSeriesFileToSequencesWithESAX() {
    }

    /**
     * Run the algorithm
     * @param multipleTimeSeries list of time series
     * @param output the output file path
     * @param numberOfSegments the number of segments (> 1)
     * @param numberOfSymbols  the number of symbols
     * @throws IOException exception if error while writing the file
     */
    public void runAlgorithm(List<TimeSeries> multipleTimeSeries, String output,
                             int numberOfSegments, int numberOfSymbols) throws IOException {

        MemoryLogger.getInstance().reset();
        startTimestamp = System.currentTimeMillis();

        // open output writer
        writer = new BufferedWriter(new FileWriter(output));
        writer.write("@CONVERTED_FROM_TIME_SERIES");

        timeSeriesCount = multipleTimeSeries.size();

        if (DEBUG_MODE) {
            System.out.println("======= APPLYING ESAX ========");
        }

        // Apply ESAX
        AlgoESAX algo = new AlgoESAX();
        SAXSymbol[][][] esaxSequences = algo.runAlgorithm(multipleTimeSeries, numberOfSegments, numberOfSymbols);

        if (DEBUG_MODE) {
            System.out.println("======= WRITING OUTPUT ========");
        }

        // Save symbol definitions
        symbols = algo.symbols;
        writeSAXSymbolsToOutputFile(symbols);

        // Save each series as a sequence of symbols
        for (int i = 0; i < multipleTimeSeries.size(); i++) {
            TimeSeries timeSeries = multipleTimeSeries.get(i);
            SAXSymbol[][] esaxSequence = esaxSequences[i];
            writeESAXRepresentationToOutputFile(esaxSequence, timeSeries.getName());
        }

        timeSeriesCount = multipleTimeSeries.size();
        MemoryLogger.getInstance().checkMemory();
        writer.close();
        endTimestamp = System.currentTimeMillis();
    }

    /**
     * Get the list of all symbols used for converting the time series.
     * @return the list of symbols or null if no time series has been converted yet.
     */
    public SAXSymbol[] getSymbols() {
        return symbols;
    }

    /**
     * Write ESAX representation of a single time series
     * @param esaxRepresentation the ESAX representation (segments x 3 symbols)
     * @param name the name of the time series
     */
    private void writeESAXRepresentationToOutputFile(SAXSymbol[][] esaxRepresentation, String name) throws IOException {
        writer.newLine();
        writer.write("@NAME=" + name);
        writer.newLine();

        // Flatten triples to one linear sequence
        for (SAXSymbol[] triple : esaxRepresentation) {
            for (SAXSymbol s : triple) {
                writer.write(s.symbol + " -1 ");
            }
        }
        writer.write("-2");
    }

    /**
     * Write the SAX symbols definitions to the output file
     */
    private void writeSAXSymbolsToOutputFile(SAXSymbol[] symbols) throws IOException {
        for (SAXSymbol symbol : symbols) {
            writer.newLine();
            writer.append("@ITEM=" + symbol.symbol + "=[" + symbol.lowerBound + "," + symbol.upperBound + "]");
        }
    }

    /**
     * Print statistics
     */
    public void printStats() {
        System.out.println("======= CONVERT TIME SERIES TO SEQUENCES WITH ESAX - STATS =======");
        System.out.println(" Number of time series processed: " + timeSeriesCount);
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("==================================================================");
    }
}
