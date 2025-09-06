package ca.pfv.spmf.algorithms.timeseries.autocorrelation.partialautocorrelation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.AlgoLagAutoCorrelation;
import ca.pfv.spmf.algorithms.timeseries.autocorrelation.partialautocorrelation.AlgoLagPartialAutoCorrelation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Example of how to calculate the  autocorrelation function of a time series, using
 * the source code of SPMF
 *
 * @author Philippe Fournier-Viger, 2018.
 */
public class MainTestAlgoLagAutoCorrelation {

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

        // Print the autocorrelation time series
        System.out.println(" Auto-correlation for lag: 1 to : " + maxlag);
        System.out.println(aSeries.toString());

    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException{
        URL url = MainTestAlgoLagAutoCorrelation.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }

    @Test
    void testInvalidLagParameter() {
        TimeSeries dummySeries = new TimeSeries(new double[]{1, 2, 3, 4, 5}, "TEST_PACF");

        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();
        // Test with maxlag = 0 (should be >= 1)
        assertThrows(IllegalArgumentException.class, () -> {
            algorithm.runAlgorithm(dummySeries, 0, null);
        });

        // Test with maxlag > series length
        assertThrows(IllegalArgumentException.class, () -> {
            algorithm.runAlgorithm(dummySeries, 6, null);
        });
    }

    @Test
    void testPrecomputedACF() throws IOException {
        TimeSeries series = new TimeSeries(new double[]{1, 5, 2, 8, 4, 6, 3, 7}, "Test Series");
        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();
        int maxLag = 5;

        // 1. Calculate PACF by letting the algorithm compute ACF internally
        TimeSeries pacfInternalACF = algorithm.runAlgorithm(series, maxLag, null);

        // 2. Pre-compute the ACF first
        AlgoLagAutoCorrelation acfAlgo = new AlgoLagAutoCorrelation();
        TimeSeries acfSeries = acfAlgo.runAlgorithm(series, maxLag);

        // 3. Calculate PACF using the pre-computed ACF
        TimeSeries pacfExternalACF = algorithm.runAlgorithm(series, maxLag, acfSeries.data);

        // 4. Compare the results. They should be identical.
        assertArrayEquals(pacfInternalACF.data, pacfExternalACF.data, 0.001);
    }

    @Test
    void testKnownAutoregressiveProcess() throws IOException {
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

        // 1. Check if the result has the correct length
        assertEquals(maxLag, pacfSeries.size());

        // 2. The first PACF value should be high (close to the AR coefficient 0.7)
        assertTrue(pacfSeries.data[0] > 0.5, "PACF at lag 1 should be high for an AR(1) process.");

        // 3. Subsequent PACF values should be small (close to zero)
        for (int i = 1; i < maxLag; i++) {
            assertTrue(Math.abs(pacfSeries.data[i]) < 0.2, "PACF at lag > 1 should be small for an AR(1) process.");
        }
    }

    @Test
    void testKnownPrecalculatedValues() throws IOException {
        // 1. The simple time series
        TimeSeries series = new TimeSeries(new double[]{2, 4, 6, 8}, "Simple Linear Series");
        int maxLag = 2;
        AlgoLagPartialAutoCorrelation algorithm = new AlgoLagPartialAutoCorrelation();

        // 2. The expected PACF values that we calculated manually
        double[] expectedPacf = {0.25, -0.3866};

        // 3. Run the algorithm
        TimeSeries actualPacf = algorithm.runAlgorithm(series, maxLag, null);

        // 4. Assert that the actual results match the expected results within a tolerance
        assertArrayEquals(expectedPacf, actualPacf.data, 0.001);

    }
}
