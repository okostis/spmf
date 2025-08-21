package ca.pfv.spmf.algorithms.timeseries.polynomialregression;

import java.util.Arrays;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.tools.MemoryLogger;

/**
 * An algorithm to calculate the polynomial regression of a time series
 * using the least squares method.
 * This generates a model of the form y(x) = coefficient[0] + coefficient[1] * x + coefficient[2] * x^2 + ... + coefficient[degree] * x^degree.
 *
 * @author Konstantinos Evangelou
 */
public class AlgoTimeSeriesPolynomialRegressionLeastSquare {

    /** the time the algorithm started */
    long startTimestamp = 0;
    /** the time the algorithm terminated */
    long endTimestamp = 0;

    /** This program will execute in DEBUG MODE if this variable is true */
    boolean DEBUG_MODE = false;

    /** The coefficients of the equation y(x) = coefficient[0] + coefficient[1] * x + coefficient[2] * x^2 + ... */
    double[] coefficients;

    /** The degree of the polynomial */
    int degree = 1;

    /**
     * Default constructor
     */
    public AlgoTimeSeriesPolynomialRegressionLeastSquare() {

    }

    /**
     * Train a polynomial regression model for a given time series using the least squares method.
     * @param timeSeries a time series
     * @param degree the degree of the polynomial (must be >= 1)
     */
    public void trainModel(TimeSeries timeSeries, int degree) {

        // reset memory logger
        MemoryLogger.getInstance().reset();

        // record the start time of the algorithm
        startTimestamp = System.currentTimeMillis();

        // Set the degree
        if (degree < 1) {
            throw new IllegalArgumentException("Degree must be >= 1");
        }
        this.degree = degree;

        // IF in debug mode
        if(DEBUG_MODE){
            // Print the time series
            System.out.println(" Time series: " + Arrays.toString(timeSeries.data));
            System.out.println(" Polynomial degree: " + degree);
        }

        // Train the model
        trainRegressionModel(timeSeries.data, degree);

        // check the memory usage again and close the file.
        MemoryLogger.getInstance().checkMemory();

        // record end time
        endTimestamp = System.currentTimeMillis();
    }

    /**
     * Calculate the regression line corresponding to a time series
     * @param series a time series
     * @return the regression line corresponding to the time series (a TimeSeries object)
     */
    public TimeSeries calculateRegressionLine(TimeSeries series){
        // Obtain the data
        double[] timeSeries = series.data;

        // Create an array to store the regression line
        double[] regressionLine = new double[timeSeries.length];

        // Calculate the regression line based on the input data
        for (int i = 0; i < timeSeries.length; i++) {
            regressionLine[i] = performPrediction(i);
        }

        if(DEBUG_MODE){
            System.out.println(" Time-series obtained by the polynomial regression: " + Arrays.toString(regressionLine));
        }

        // Return the result as a TimeSeries object
        return new TimeSeries(regressionLine, series.getName() + "_PR_" + degree);
    }

    /**
     * Generate a polynomial regression of a time series (an equation of the form
     *   y = coefficient[0] + coefficient[1]*x + coefficient[2]*x^2 + ... + coefficient[degree]*x^degree
     *
     * @param timeSeries a time series represented by a double array
     * @param degree the degree of the polynomial
     */
    private void trainRegressionModel(double[] timeSeries, int degree) {

        int n = timeSeries.length;
        int d = degree;

        // Initialize coefficients array
        coefficients = new double[d + 1];

        // Build normal equations matrices: A * coefficients = B
        double[][] A = new double[d + 1][d + 1];
        double[] B = new double[d + 1];

        // Precompute powers of x sums for efficiency
        // x values are the indices: 0, 1, 2, ..., n-1
        double[] sumXPowers = new double[2 * d + 1];
        for (int k = 0; k < sumXPowers.length; k++) {
            sumXPowers[k] = 0d;
            for (int i = 0; i < n; i++) {
                sumXPowers[k] += Math.pow(i, k);
            }
        }

        // Fill A matrix (Vandermonde-like matrix for normal equations)
        for (int row = 0; row <= d; row++) {
            for (int col = 0; col <= d; col++) {
                A[row][col] = sumXPowers[row + col];
            }
        }

        // Fill B vector
        for (int row = 0; row <= d; row++) {
            B[row] = 0d;
            for (int i = 0; i < n; i++) {
                B[row] += timeSeries[i] * Math.pow(i, row);
            }
        }

        // Solve linear system A * coefficients = B using Gaussian elimination
        coefficients = solveLinearSystem(A, B);

        // print results
        if(DEBUG_MODE){
            System.out.println(" Number of data points = " + n);
            System.out.println(" Polynomial degree = " + degree);
            System.out.println(" Regression line is: ");
            StringBuilder equation = new StringBuilder("  Y(x) = " + coefficients[0]);
            for (int i = 1; i <= degree; i++) {
                equation.append(" + ").append(coefficients[i]).append(" * x^").append(i);
            }
            System.out.println(equation.toString());
        }
    }

    /**
     * Solve linear system Ax = b using Gaussian elimination with partial pivoting
     * @param A coefficient matrix
     * @param B right-hand side vector
     * @return solution vector x
     */
    private double[] solveLinearSystem(double[][] A, double[] B) {
        int n = B.length;

        // Forward elimination with partial pivoting
        for (int i = 0; i < n; i++) {
            // Pivot search - find row with largest absolute value in column i
            int maxRow = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[maxRow][i])) {
                    maxRow = j;
                }
            }

            // Swap rows in A and B
            double[] tempRow = A[i];
            A[i] = A[maxRow];
            A[maxRow] = tempRow;

            double tempB = B[i];
            B[i] = B[maxRow];
            B[maxRow] = tempB;

            // Make all rows below this one 0 in current column
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[i][i]) > 1e-10) { // avoid division by zero
                    double factor = A[j][i] / A[i][i];
                    B[j] -= factor * B[i];
                    for (int k = i; k < n; k++) {
                        A[j][k] -= factor * A[i][k];
                    }
                }
            }
        }

        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0d;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            if (Math.abs(A[i][i]) > 1e-10) { // avoid division by zero
                x[i] = (B[i] - sum) / A[i][i];
            }
        }
        return x;
    }

    /**
     * Perform a prediction using the current regression model.
     * This assumes that the model has been trained using the
     * trainModel() method.
     * @param x a double value
     * @return a prediction calculating using the polynomial model, or 0 if the model has not been trained.
     */
    public double performPrediction(double x){
        if (coefficients == null) {
            return 0;
        }

        double result = 0d;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * Math.pow(x, i);
        }
        return result;
    }

    /**
     * Get the coefficients of the trained model.
     * This assumes that the model has been trained using the
     * trainModel() method.
     * @return the coefficients array of the polynomial equation, or null if the model has not been trained.
     */
    public double[] getCoefficients() {
        return coefficients;
    }

    /**
     * Get a specific coefficient of the trained model.
     * This assumes that the model has been trained using the
     * trainModel() method.
     * @param index the index of the coefficient (0 for constant term, 1 for linear term, etc.)
     * @return the coefficient at the specified index, or 0 if the model has not been trained or index is invalid.
     */
    public double getCoefficient(int index) {
        if (coefficients == null || index < 0 || index >= coefficients.length) {
            return 0;
        }
        return coefficients[index];
    }

    /**
     * Get the degree of the trained polynomial model.
     * @return the degree of the polynomial, or 0 if the model has not been trained.
     */
    public int getDegree() {
        return degree;
    }

    /**
     * Print statistics about the latest execution to System.out.
     */
    public void printStats() {
        System.out.println("=============  Polynomial regression (least squares) v2.19- STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Max Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println("===================================================");
    }
}