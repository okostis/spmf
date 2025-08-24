package ca.pfv.spmf.algorithms.timeseries.polynomialregression;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.simplelinearregression.AlgoTimeSeriesLinearRegressionLeastSquare;
import ca.pfv.spmf.algorithms.timeseries.simplelinearregression.MainTestSimpleRegressionLeastSquare;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTestPolynomialRegressionLeastSquare {
    public static void main(String [] arg) throws IOException {

        // Create a time series
        double [] dataPoints = new double[]{-1, -2, -3};
        TimeSeries timeSeries = new TimeSeries(dataPoints, "SERIES1");

        // Print the input data
        System.out.println("The input data is: ");
        System.out.println(" " + timeSeries.toString());
        System.out.println();

        // Train the regression model
        AlgoTimeSeriesPolynomialRegressionLeastSquare algorithm = new AlgoTimeSeriesPolynomialRegressionLeastSquare();
        int degree = 4;
        algorithm.trainModel(timeSeries, degree);

        // Print statistics about model training
        algorithm.printStats();
        System.out.println();

        // Print the regression equation
        System.out.println("The following regression model is obtained: ");

        StringBuilder equation = new StringBuilder();
        for (int i = 0; i < algorithm.coefficients.length; i++) {
            double coeff = algorithm.getCoefficient(i);

            if (i == 0) {
                equation.append(coeff);
            } else {
                equation.append(" + ").append(coeff).append(" * x");
                if (i > 1) {
                    equation.append("^").append(i);
                }
            }
        }

        System.out.println("  Y(x) = " + equation.toString());
        System.out.println();

        // Print the regression line
        TimeSeries regressionLine = algorithm.calculateRegressionLine(timeSeries);
        System.out.println("The regression line for the input data is: ");
        System.out.println(" " + regressionLine.toString());
        System.out.println();

        // Perform a prediction using the regression model
        System.out.println("We can use the model to make a prediction for a new value of x. \nFor example:");
        double prediction = algorithm.performPrediction(11d);
        System.out.println(" The prediction for x = 11 is  y = " + prediction);
    }

    public static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestSimpleRegressionLeastSquare.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
    }


    @Test
    public void testQuadraticFit() {
        double[] data = new double[]{0, 1, 4, 9}; // y = x^2
        TimeSeries ts = new TimeSeries(data, "quadratic");

        AlgoTimeSeriesPolynomialRegressionLeastSquare algo = new AlgoTimeSeriesPolynomialRegressionLeastSquare();
        algo.trainModel(ts, 2);

        assertEquals(0.0, algo.getCoefficient(0), 1e-6); // intercept
        assertEquals(0.0, algo.getCoefficient(1), 1e-6); // linear term
        assertEquals(1.0, algo.getCoefficient(2), 1e-6); // quadratic term

        assertEquals(16.0, algo.performPrediction(4), 1e-6);
        assertEquals(64.0, algo.performPrediction(8), 1e-6);
    }

    @Test
    public void testNegativeDegree(){
        double[] data = new double[]{0, 1, 4, 9}; // y = x^2
        TimeSeries ts = new TimeSeries(data, "quadratic");

        AlgoTimeSeriesPolynomialRegressionLeastSquare algo = new AlgoTimeSeriesPolynomialRegressionLeastSquare();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> algo.trainModel(ts, -2)
        );
    }

    @Test
    public void testLinnearcFit() {
        double[] data = new double[]{0, 1, 2, 3,4,5,6,7}; // y = x
        TimeSeries ts = new TimeSeries(data, "linear");

        AlgoTimeSeriesPolynomialRegressionLeastSquare algo = new AlgoTimeSeriesPolynomialRegressionLeastSquare();
        algo.trainModel(ts, 3);


        assertEquals(18, algo.performPrediction(18), 1e-6);
    }

    @Test
    public void testCubicFit() {
        double[] data = new double[]{0, 1, 8, 27}; // y= x^3
        TimeSeries ts = new TimeSeries(data, "linear");

        AlgoTimeSeriesPolynomialRegressionLeastSquare algo = new AlgoTimeSeriesPolynomialRegressionLeastSquare();
        algo.trainModel(ts, 3);


        assertEquals(125, algo.performPrediction(5), 1e-6);
    }

    @Test
    public void testPolyonimicFit() {
        double[] data = new double[]{0, 5, 36, 117,272}; // y= x^2 +4x3
        TimeSeries ts = new TimeSeries(data, "linear");

        AlgoTimeSeriesPolynomialRegressionLeastSquare algo = new AlgoTimeSeriesPolynomialRegressionLeastSquare();
        algo.trainModel(ts, 4);


        assertEquals(525, algo.performPrediction(5), 1e-6);
    }

    @Test
    public void testConstantFit() {
        double[] data = new double[]{4, 4}; // y= 4
        TimeSeries ts = new TimeSeries(data, "linear");

        AlgoTimeSeriesPolynomialRegressionLeastSquare algo = new AlgoTimeSeriesPolynomialRegressionLeastSquare();
        algo.trainModel(ts, 4);


        assertEquals(4, algo.performPrediction(5), 1e-6);
    }







}
