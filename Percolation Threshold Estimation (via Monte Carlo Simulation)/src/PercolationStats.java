/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final int gridsize;
    private final int numTrials;
    private final double[] fractions;
    private final double confidence95;
    private double meanThreshold;
    private double stddevThreshold;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException("Invalid values of N and T!");
        gridsize = n;
        numTrials = trials;
        confidence95 = 1.96;
        fractions = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                openAtRandom(perc);
            }
            fractions[i] = (double) perc.numberOfOpenSites() / (n * n);
        }
        calc();
    }

    private void openAtRandom(Percolation p) {
        int row = StdRandom.uniform(1, gridsize + 1);
        int col = StdRandom.uniform(1, gridsize + 1);
        p.open(row, col);
    }

    private void calc() {
        meanThreshold = StdStats.mean(fractions);
        stddevThreshold = StdStats.stddev(fractions);
    }

    public double mean() {
        return meanThreshold;
    }

    public double stddev() {
        return stddevThreshold;
    }

    public double confidenceLo() {
        return (meanThreshold - ((confidence95 * stddev()) / (Math.sqrt(numTrials))));
    }

    public double confidenceHi() {
        return (meanThreshold + ((confidence95 * stddev()) / (Math.sqrt(numTrials))));
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats pers = new PercolationStats(n, t);
        System.out.println("mean \t\t\t\t\t = " + pers.mean());
        System.out.println("stddev \t\t\t\t\t = " + pers.stddev());
        System.out.println(
                "95% confidence interval \t\t = " + "[" + pers.confidenceLo() + ", " + pers
                        .confidenceHi() + "]");
    }
}
