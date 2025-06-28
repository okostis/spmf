package ca.pfv.spmf.algorithms.timeseries.distances.ManhattanDistance;

import ca.pfv.spmf.algorithms.timeseries.TimeSeries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlgoManhattanDistanceTest {

    private AlgoManhattanDistance algorithm;

    @BeforeEach
    void setUp() {
        algorithm = new AlgoManhattanDistance();
    }



    @Test
    void testKnownDistance() throws Exception {

        double [] dataPoints1 = new double[]{1.0, 4.5, 6.0, 4.0, 3.0, 4.0, 5.0, 4.0, 3.0, 2.0};
        double [] dataPoints2 = new double[]{3.0, 4.5, 5.0, 6.0, 3.0, 4.0, 8.0, 4.0, 3.0, 2.0};
        TimeSeries timeSeries1 = new TimeSeries(dataPoints1, "SERIES1");
        TimeSeries timeSeries2 = new TimeSeries(dataPoints2, "SERIES2");

        double distance = algorithm.runAlgorithm(timeSeries1, timeSeries2);
        double expected = 8.0;
        assertEquals(expected, distance, 0.0001, "Distance calculation should match expected value");
    }



}
