package ca.pfv.spmf.algorithms.timeseries.distances.EuclidianDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.differencing.AlgoFirstOrderDifferencing;
import ca.pfv.spmf.algorithms.timeseries.differencing.MainTestFirstOrderDifferencingFileToFile;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesReader;
import ca.pfv.spmf.algorithms.timeseries.reader_writer.AlgoTimeSeriesWriter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainTestEuclidianDistanceFileToFile {
    public static void main(String [] arg) throws Exception {

        // the input file
        String input = fileToPath("contextMovingAverage.txt");
        // the output file
        String output = "./output.txt";

        // The separator to be used for reading/writting the input/output file
        String separator = ",";

        // (1) Read the time series
        AlgoTimeSeriesReader reader = new AlgoTimeSeriesReader();
        List<TimeSeries> multipleTimeSeries = reader.runAlgorithm(input, separator);


        AlgoEuclidianDistance algorithm = new AlgoEuclidianDistance();
        double distance = algorithm.runAlgorithm(multipleTimeSeries.get(0), multipleTimeSeries.get(1));
        System.out.println(" Euclidian Distance: ");
        System.out.println(distance);


    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTestEuclidianDistanceFileToFile.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }
}
