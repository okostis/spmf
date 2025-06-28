package ca.pfv.spmf.algorithms.timeseries.distances.ERPDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;
import ca.pfv.spmf.algorithms.timeseries.distances.erpDistance.AlgoERPDistance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlgoERPDistanceTest {

    private AlgoERPDistance algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new AlgoERPDistance();
    }

    @Test
    void testKnownDistance() throws Exception {

        double[] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double[] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");
        algorithm.setG(0.1);
        double limit = 500;
        // Applying the algorithm
        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2, limit);

        double expected = 14.0; // Example expected value
        assertEquals(expected, distance, 0.0001, "Distance calculation should match expected value");
    }
}
