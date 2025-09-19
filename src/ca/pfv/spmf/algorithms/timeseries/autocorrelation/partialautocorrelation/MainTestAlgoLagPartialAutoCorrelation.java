package ca.pfv.spmf.algorithms.timeseries.autocorrelation.partialautocorrelation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.AlgoLagAutoCorrelation;


public class MainTestAlgoLagPartialAutoCorrelation {

    public static void main(String [] arg) throws IOException{

        // the maximum lag to be used to generate the k-lag autocorrelation plot of a time series.
        int maxlag = 15;

        // Create a time series
        double [] dataPoints = new double[]{3.0,2.0,8.0,9.0,8.0,9.0,8.0,7.0,6.0,7.0,5.0,4.0,2.0,7.0,9.0,8.0,5.0};
        TimeSeries timeSeries = new TimeSeries(dataPoints, "SERIES1");

        // Applying the  algorithm
        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();
        TimeSeries aSeries = algorithm.runAlgorithm(timeSeries,maxlag,null);
        algorithm.printStats();

        testInvalidLagParameter();
        testPrecomputedACF();
        testKnownAutoregressiveProcess();
        testKnownPrecalculatedValues();

        // Print the autocorrelation time series
        System.out.println(" Auto-correlation for lag: 1 to : " + maxlag);
        System.out.println(aSeries.toString());

    }

    public static void testInvalidLagParameter(){
        TimeSeries dummySeries = new TimeSeries(new double[]{1, 2, 3, 4, 5}, "TEST_PACF");

        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();

        try {

            TimeSeries timeSeries = algorithm.runAlgorithm(dummySeries, 0, null);


            algorithm.runAlgorithm(dummySeries, 6, null);

        } catch (Exception e) {
            System.out.println("TestInvalidLagParameter(1) Passed");
            return;
        }
        System.out.println("TestInvalidLagParameter(1) failed");
    }

    public static void testPrecomputedACF() throws IOException {
        TimeSeries series = new TimeSeries(new double[]{1, 5, 2, 8, 4, 6, 3, 7}, "Test Series");
        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();
        int maxLag = 5;

        // 1. Calculate PACF by letting the algorithm compute ACF internally
        TimeSeries pacfInternalACF = algorithm.runAlgorithm(series, maxLag, null);

        AlgoLagAutoCorrelation acfAlgo = new AlgoLagAutoCorrelation();
        TimeSeries acfSeries = acfAlgo.runAlgorithm(series, maxLag);

        TimeSeries pacfDirect = algorithm.runAlgorithm(series, maxLag, null);

        TimeSeries pacfExternalACF = algorithm.runAlgorithm(series, maxLag, acfSeries.data);

        boolean equal = true;
        for (int i = 0; i < pacfDirect.data.length; i++) {
            if (Math.abs(pacfDirect.data[i] - pacfExternalACF.data[i]) > 1e-6) {
                equal = false;
                break;
            }
        }

        if (equal) {
            System.out.println("TestPrecomputedACF(1) passed");
        } else {
            System.out.println("TestPrecomputedACF(1) failed");
        }

    }

    public static void testKnownAutoregressiveProcess() throws  IOException{
        // Generate a simple AR(1) series: x_t = 0.7 * x_{t-1} + noise
        double[] ar1Data = new double[200];
        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();
        ar1Data[0] = 0;
        for (int i = 1; i < ar1Data.length; i++) {
            ar1Data[i] = 0.7 * ar1Data[i-1] + (Math.random() - 0.5);
        }
        TimeSeries ar1Series = new TimeSeries(ar1Data, "AR1 Series");
        int maxLag = 10;
        TimeSeries pacfSeries = algorithm.runAlgorithm(ar1Series, maxLag, null);

        if(maxLag != pacfSeries.size()){
            System.out.println("TestKnownAutoregressiveProcess(1) failed");
            return;
        }


        if(pacfSeries.data[0] <= 0.5){
            System.out.println("TestKnownAutoregressiveProcess(1) Failed");
        }

        for (int i = 1; i < maxLag; i++) {
            if(Math.abs(pacfSeries.data[i]) >= 0.2){
                System.out.println("TestKnownAutoregressiveProcess(1) Failed");
                return;
            }
        }
        System.out.println("TestKnownAutoregressiveProcess(1) passed");

    }

    public static  void testKnownPrecalculatedValues() throws  IOException{

        TimeSeries series = new TimeSeries(new double[]{2, 4, 6, 8}, "Simple Linear Series");
        int maxLag = 2;
        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();

        double[] expectedPacf = {0.25, -0.38666666666666666};

        TimeSeries actualPacf = algorithm.runAlgorithm(series, maxLag, null);



        boolean equal = true;
        for (int i = 0; i < expectedPacf.length; i++) {
            if (Math.abs(expectedPacf[i] - actualPacf.data[i]) > 1e-6) {
                equal = false;
                break;
            }
        }

        if (equal) {
            System.out.println("TestKnownPrecalculatedValues(1) Passed");
        } else {
            System.out.println("TestKnownPrecalculatedValues(1) Failed");
        }

    }
    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTestAlgoLagPartialAutoCorrelation.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }

}
