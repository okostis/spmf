package ca.pfv.spmf.algorithmmanager.descriptions;

import ca.pfv.spmf.algorithmmanager.AlgorithmType;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.DTWdistance.AlgoDTWDistance;
import ca.pfv.spmf.algorithms.timeseries.distances.ManhattanDistance.AlgoManhattanDistance;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;

import java.util.List;

public class DescriptionAlgoTimeSeriesManhattanDistance  extends DescriptionOfAlgorithm {

    @Override
    public String getImplementationAuthorNames() {
        return "Konstantinos Evangelou";
    }

    @Override
    public String getName() {
        return "Manhattan_Distance";
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
        String separator = parameters[0];
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> multipleTimeSeries = reader.runAlgorithm(inputFile, separator);

        AlgoManhattanDistance algorithm = new AlgoManhattanDistance();
        //algorithm.setWindow(window);
        double distance = algorithm.runAlgorithm(multipleTimeSeries.get(0), multipleTimeSeries.get(1));
        System.out.println("Manhattan distance: " + distance);
        algorithm.printStats();
    }

    @Override
    public DescriptionOfParameter[] getParametersDescription() {
        DescriptionOfParameter[] parameters = new DescriptionOfParameter[1];
        parameters[0] = new DescriptionOfParameter("seperator ", "(e.g. ',')", String.class, false);

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
