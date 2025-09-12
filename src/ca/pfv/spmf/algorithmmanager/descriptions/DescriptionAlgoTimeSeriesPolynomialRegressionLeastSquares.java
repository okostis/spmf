package ca.pfv.spmf.algorithmmanager.descriptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.pfv.spmf.algorithmmanager.AlgorithmType;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesWriter;
import ca.pfv.spmf.algorithms.timeseries.polynomialregression.AlgoTimeSeriesPolynomialRegressionLeastSquare;

/**
 * This class describes the algorithm to calculate the polynomial regression of time series
 *
 * @see AlgoTimeSeriesPolynomialRegressionLeastSquare
 * @author Konstantinos Evangelou
 */
public class DescriptionAlgoTimeSeriesPolynomialRegressionLeastSquares extends DescriptionOfAlgorithm {

    /**
     * Default constructor
     */
    public DescriptionAlgoTimeSeriesPolynomialRegressionLeastSquares(){
    }

    @Override
    public String getName() {
        return "Calculate_polynomial_regression_of_time_series_(least_squares)";
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

        String separator;
        if (parameters.length > 1 && "".equals(parameters[0]) == false) {
            separator = getParamAsString(parameters[0]);
        }else{
            separator = " ";
        }

        // Get the polynomial degree
        int degree = 2; // default degree
        if (parameters.length > 1 && "".equals(parameters[1]) == false) {
            degree = getParamAsInteger(parameters[1]);
        }

        // Get the text encoding
//		Charset charset = PreferencesManager.getInstance().getPreferedCharset();

        // (1) Read the time series
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> multipleTimeSeries = reader.runAlgorithm(inputFile, separator);


        // (2) Calculate the polynomial regression of each time series
        List<TimeSeries> regressionLines = new ArrayList<TimeSeries>();
        long endTimestamp = 0;
        long startTimestamp = System.currentTimeMillis();
        for(TimeSeries timeSeries : multipleTimeSeries){
            AlgoTimeSeriesPolynomialRegressionLeastSquare algorithm = new AlgoTimeSeriesPolynomialRegressionLeastSquare();
            algorithm.trainModel(timeSeries, degree);
            TimeSeries regressionLine = algorithm.calculateRegressionLine(timeSeries);
            regressionLines.add(regressionLine);
            algorithm.printStats();
        }
        endTimestamp = System.currentTimeMillis();
        System.out.println(" Total time for " + multipleTimeSeries.size() + "timeseries: " + (endTimestamp - startTimestamp) + " ms");


        // (3) write the time series to a file
        AlgoTimeSeriesWriter algorithm2 = new AlgoTimeSeriesWriter();
        algorithm2.runAlgorithm(outputFile, regressionLines, separator);
        algorithm2.printStats();

    }

    @Override
    public DescriptionOfParameter[] getParametersDescription() {

        DescriptionOfParameter[] parameters = new DescriptionOfParameter[2];
        parameters[0] = new DescriptionOfParameter("separator", "(e.g. ',' , default: ' ')", String.class, true);
        parameters[1] = new DescriptionOfParameter("degree", "(e.g. 2, 3, 4, default: 2)", Integer.class, true);

        return parameters;
    }

    @Override
    public String getImplementationAuthorNames() {
        return "Philippe Fournier-Viger";
    }

    @Override
    public String[] getInputFileTypes() {
        return new String[]{"Time series database"};
    }

    @Override
    public String[] getOutputFileTypes() {
        return new String[]{"Time series database"};
    }

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.DATA_PROCESSOR;
    }

}