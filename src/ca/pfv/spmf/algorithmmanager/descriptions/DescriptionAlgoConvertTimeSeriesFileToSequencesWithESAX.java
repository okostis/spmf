package ca.pfv.spmf.algorithmmanager.descriptions;

import ca.pfv.spmf.algorithmmanager.AlgorithmType;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;
import ca.pfv.spmf.algorithms.timeseries.sax.AlgoConvertTimeSeriesFileToSequencesWithESAX;
import ca.pfv.spmf.algorithms.timeseries.sax.AlgoConvertTimeSeriesFileToSequencesWithSAX;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.tools.dataset_converter.SequenceDatabaseConverter;

import java.io.IOException;
import java.util.List;

/**
 * This class describes the algorithm to convert a timeseries to a sequence
 * database using the SAX algorithm
 *
 * @author  Konstantinos Evangelou
 */
public class DescriptionAlgoConvertTimeSeriesFileToSequencesWithESAX extends DescriptionOfAlgorithm {

    /**
     * Default constructor
     */
    public DescriptionAlgoConvertTimeSeriesFileToSequencesWithESAX() {
    }

    @Override
    public String getName() {
        return "Convert_time_series_to_sequence_database_using_ESAX";
    }

    @Override
    public String getAlgorithmCategory() {
        return "TIME SERIES MINING";
    }

    @Override
    public String getURLOfDocumentation() {
        return "";
    }

    @Override
    public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws IOException {
        int numberOfSegments = getParamAsInteger(parameters[0]);
        int numberOfSymbols = getParamAsInteger(parameters[1]);
        String separator = getParamAsString(parameters[2]);

        // Get the text encoding
//		Charset charset = PreferencesManager.getInstance().getPreferedCharset();

        // Applying the algorithm
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> timeSeries = reader.runAlgorithm(inputFile, separator);
        reader.printStats();


        // Applying the algorithm
        AlgoConvertTimeSeriesFileToSequencesWithESAX algorithm = new AlgoConvertTimeSeriesFileToSequencesWithESAX();

        long endTimestamp = 0;
        long startTimestamp = System.currentTimeMillis();
        algorithm.runAlgorithm(timeSeries, outputFile, numberOfSegments, numberOfSymbols);
        algorithm.printStats();

        endTimestamp = System.currentTimeMillis();
        System.out.println(" Total time for " + timeSeries.size() + "timeseries: " + (endTimestamp - startTimestamp) + " ms");


    }

    @Override
    public DescriptionOfParameter[] getParametersDescription() {

        DescriptionOfParameter[] parameters = new DescriptionOfParameter[3];
        parameters[0] = new DescriptionOfParameter("Number of segments", "(e.g. 3)", Integer.class, false);
        parameters[1] = new DescriptionOfParameter("Number of symbols", "(e.g. 3)", Integer.class, false);
        parameters[2] = new DescriptionOfParameter("Separator", "(e.g. , )", String.class, false);

        return parameters;
    }

    @Override
    public String getImplementationAuthorNames() {
        return "Konstantinos Evangelou";
    }

    @Override
    public String[] getInputFileTypes() {
        return new String[] { "Time series database" };
    }

    @Override
    public String[] getOutputFileTypes() {
        return new String[] { "Database of instances", "Sequence database", "Simple sequence database" };
    }

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.DATA_PROCESSOR;
    }
}
