package ca.pfv.spmf.algorithmmanager.descriptions;

import ca.pfv.spmf.algorithmmanager.AlgorithmType;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.correlogramModule.AlgoCorrelogramModule;
import ca.pfv.spmf.algorithms.timeseries.distances.cosineDistance.AlgoCosineDistance;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;

import java.util.Arrays;
import java.util.List;

public class DescriptionAlgoTimeSeriesCorrelogramModule extends DescriptionOfAlgorithm {

    @Override
    public String getImplementationAuthorNames() {
        return "Konstantinos Evangelou";
    }

    @Override
    public String getName() {
        return "Correlogram";
    }

    @Override
    public String getAlgorithmCategory() {
        return "TimeSeries";
    }

    @Override
    public String getURLOfDocumentation() {
        return null;
    }

    @Override
    public AlgorithmType getAlgorithmType() {
        return null;
    }

    @Override
    public void runAlgorithm(String[] parameters, String inputFile, String outputFile) throws Exception {
        String separator = parameters[0];
        int maxLag = getParamAsInteger(parameters[1]);
        double confidenceLevel = getParamAsDouble(parameters[2]);
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> multipleTimeSeries = reader.runAlgorithm(inputFile, separator);

        AlgoCorrelogramModule algorithm = new AlgoCorrelogramModule();
        long endTimestamp = 0;
        long startTimestamp = System.currentTimeMillis();
        for (int i = 0; i < multipleTimeSeries.size(); i++) {

            algorithm.runAlgorithm(multipleTimeSeries.get(i),maxLag,confidenceLevel);
            System.out.println("Confidence Bands at " + (confidenceLevel * 100) + "%: " + Arrays.toString(algorithm.getConfidenceBands()));
            System.out.println("\nACF Values: " + Arrays.toString(algorithm.getACF().data));
            System.out.println("\nPACF Values: " + Arrays.toString(algorithm.getPACF().data));
            algorithm.printStats();

        }
        endTimestamp = System.currentTimeMillis();
        System.out.println(" Total time for " + multipleTimeSeries.size() + "timeseries: " + (endTimestamp - startTimestamp) + " ms");

    }

    @Override
    public DescriptionOfParameter[] getParametersDescription() {
        DescriptionOfParameter[] parameters = new DescriptionOfParameter[3];
        parameters[0] = new DescriptionOfParameter("seperator ", "(e.g. ',')", String.class, false);
        parameters[1] = new DescriptionOfParameter("max lag ", "(e.g. '10')", Integer.class, false);
        parameters[2] = new DescriptionOfParameter("confidence level ", "0.95", Double.class, false);
        return parameters;
    }

    @Override
    public String[] getInputFileTypes() {
        return new String[]{"Time series database"};
    }

    @Override
    public String[] getOutputFileTypes() {
        return null;
    }
}