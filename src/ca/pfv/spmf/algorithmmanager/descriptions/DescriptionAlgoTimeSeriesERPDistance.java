package ca.pfv.spmf.algorithmmanager.descriptions;

import ca.pfv.spmf.algorithmmanager.AlgorithmType;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

import ca.pfv.spmf.algorithms.timeseries.distances.erpDistance.AlgoERPDistance;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;

import java.util.List;

public class DescriptionAlgoTimeSeriesERPDistance extends DescriptionOfAlgorithm {

    @Override
    public String getImplementationAuthorNames() {
        return "Konstantinos Evangelou";
    }

    @Override
    public String getName() {
        return "EDPistance";
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
        String separator = parameters[0];
        double gap = getParamAsDouble(parameters[3]);
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> multipleTimeSeries = reader.runAlgorithm(inputFile, separator);

        AlgoERPDistance algorithm = new AlgoERPDistance();
        algorithm.setG(gap);
        //algorithm.setWindow(window);
        double distance = algorithm.runAlgorithm(multipleTimeSeries.get(0), multipleTimeSeries.get(1), limit);
        System.out.println("EDP distance: " + distance);
        algorithm.printStats();
    }

    @Override
    public DescriptionOfParameter[] getParametersDescription() {
        DescriptionOfParameter[] parameters = new DescriptionOfParameter[4];
        parameters[0] = new DescriptionOfParameter("seperator ", "(e.g. ',')", String.class, false);
        parameters[1] = new DescriptionOfParameter("window (%)", "(e.g. 0.2)", Double.class, false);
        parameters[2] = new DescriptionOfParameter("limit", "(e.g. 20)", Double.class, false);
        parameters[3] = new DescriptionOfParameter("gap", "(e.g. 1)", Double.class, false);
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