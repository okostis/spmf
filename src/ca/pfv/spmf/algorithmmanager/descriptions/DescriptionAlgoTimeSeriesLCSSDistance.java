package ca.pfv.spmf.algorithmmanager.descriptions;

import ca.pfv.spmf.algorithmmanager.AlgorithmType;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance.AlgoDTWDistance;
import ca.pfv.spmf.algorithms.timeseries.distances.LCSSDistance.AlgoLCSSDistance;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;

import java.util.List;

public class DescriptionAlgoTimeSeriesLCSSDistance extends DescriptionOfAlgorithm {

    @Override
    public String getImplementationAuthorNames() {
        return "Konstantinos Evangelou";
    }

    @Override
    public String getName() {
        return "LCSS_Distance";
    }

    @Override
    public String getAlgorithmCategory() {
        return "Distance";
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
        double limit = getParamAsDouble(parameters[2]);
        float window = getParamAsFloat(parameters[1]);
        double epsilon = getParamAsDouble(parameters[3]);

        String separator = parameters[0];
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> multipleTimeSeries = reader.runAlgorithm(inputFile, separator);

        AlgoLCSSDistance algorithm = new AlgoLCSSDistance();
        algorithm.setWindow(window);
        algorithm.setEpsilon(epsilon);

        long endTimestamp = 0;
        long startTimestamp = System.currentTimeMillis();
        for (int i = 0; i < multipleTimeSeries.size(); i++) {
            for (int j = i + 1; j < multipleTimeSeries.size(); j++) {
                double distance = algorithm.runAlgorithm(multipleTimeSeries.get(i), multipleTimeSeries.get(j), limit);
                System.out.println("LCSS distance: " + distance);
                algorithm.printStats();
            }
        }
        endTimestamp = System.currentTimeMillis();
        System.out.println(" Total time for " + multipleTimeSeries.size() + "timeseries: " + (endTimestamp - startTimestamp) + " ms");

    }

    @Override
    public DescriptionOfParameter[] getParametersDescription() {
        DescriptionOfParameter[] parameters = new DescriptionOfParameter[4];
        parameters[0] = new DescriptionOfParameter("seperator ", "(e.g. ',')", String.class, false);
        parameters[1] = new DescriptionOfParameter("window (%)", "(e.g. 0.2)", Double.class, false);
        parameters[2] = new DescriptionOfParameter("limit", "(e.g. 0.2 (0-1))", Double.class, false);
        parameters[3] = new DescriptionOfParameter("epsilon", "(e.g. 0.5)", Double.class, false);
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