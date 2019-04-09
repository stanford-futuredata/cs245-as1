package memstore.benchmarks;

/**
 * Queries that will be benchmarked for both correctness and runtime.
 */
public interface TableBenchmark {
    void prepare() throws Exception;
    long testRowTable();
    long testColumnTable();
    long testIndexedTable();

    /**
     * @return absolute timing thresholds we expect on gradescope machine
     */
    double[] getThresholds();
}
