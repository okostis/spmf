package ca.pfv.spmf.algorithms.timeseries.paa;

public class SegmentStats {
    public double mean;
    public double min;
    public double max;
    public int posMean; // midpoint index of the segment
    public int posMin;  // index of min value in the segment
    public int posMax;  // index of max value in the segment

    public SegmentStats(double mean, double min, double max,
                        int posMean, int posMin, int posMax) {
        this.mean = mean;
        this.min = min;
        this.max = max;
        this.posMean = posMean;
        this.posMin = posMin;
        this.posMax = posMax;
    }
}
