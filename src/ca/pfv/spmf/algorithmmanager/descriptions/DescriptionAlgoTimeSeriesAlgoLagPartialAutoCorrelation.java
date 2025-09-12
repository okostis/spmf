package ca.pfv.spmf.algorithmmanager.descriptions;

import ca.pfv.spmf.algorithmmanager.AlgorithmType;
import ca.pfv.spmf.algorithmmanager.DescriptionOfAlgorithm;
import ca.pfv.spmf.algorithmmanager.DescriptionOfParameter;
import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.AlgoLagAutoCorrelation;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.partialautocorrelation.AlgoLagPartialAutoCorrelation;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DescriptionAlgoTimeSeriesAlgoLagPartialAutoCorrelation extends DescriptionOfAlgorithm {

    /**
     * Default constructor
     */
    public DescriptionAlgoTimeSeriesAlgoLagPartialAutoCorrelation(){
    }

    @Override
    public String getName() {
        return "Calculate_partial_autocorrelation_of_time_series";
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
        int maxlag = getParamAsInteger(parameters[0]);

        String separator;
        if (parameters.length > 1 && "".equals(parameters[1]) == false) {
            separator = getParamAsString(parameters[1]);
        }else{
            separator = " ";
        }


        //Read the time series
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> multipleTimeSeries = reader.runAlgorithm(inputFile, separator);

        long endTimestamp = 0;
        long startTimestamp = System.currentTimeMillis();
        //  Calculate the exponential smoothing of each time series
        List<TimeSeries> resultMultipleTimeSeries = new ArrayList<TimeSeries>();
        for(TimeSeries timeSeries : multipleTimeSeries){
            AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();
            TimeSeries result = algorithm.runAlgorithm(timeSeries, maxlag,null);
            resultMultipleTimeSeries.add(result);
            algorithm.printStats();
        }
        endTimestamp = System.currentTimeMillis();
        System.out.println(" Total time for " + multipleTimeSeries.size() + "timeseries: " + (endTimestamp - startTimestamp) + " ms");


        // write the time series to a file
        AlgoTimeSeriesWriter algorithm2 = new AlgoTimeSeriesWriter();
        algorithm2.runAlgorithm(outputFile, resultMultipleTimeSeries, separator);
        algorithm2.printStats();
    }

    @Override
    public DescriptionOfParameter[] getParametersDescription() {

        DescriptionOfParameter[] parameters = new DescriptionOfParameter[2];
        parameters[0] = new DescriptionOfParameter("Max lag", "(e.g. 15)", Integer.class, false);
        parameters[1] = new DescriptionOfParameter("separator", "(e.g. ',' , default: ' ')", String.class, true);

        return parameters;
    }

    @Override
    public String getImplementationAuthorNames() {
        return "";
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
