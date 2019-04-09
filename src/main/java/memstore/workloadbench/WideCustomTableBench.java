package memstore.workloadbench;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Class for custom table benchmark, with a specific initial seed
 * for random number generation.
 * Creates a RandomizedDataLoader with 1000 columns and 37,500 rows,
 * then runs predicatedUpdate(), columnSum(), predicatedColumnSum(),
 * predicatedAllColumnsSum() `numQueries` times.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class WideCustomTableBench extends CustomTableBenchAbstract {
    public double getExpectedTime() {
        return 2600;
    }

    @Override
    public DataLoader getRandomLoader() {
        int numCols = 1000;
        int numRows = 27_500_000 / numCols;

        numQueries = 100;
        return new RandomizedLoader(seed, numRows, numCols);
    }

    @Setup
    public void prepare() throws IOException {
        super.prepare();
    }

    @Benchmark
    public long testQueries() {
        return super.testQueries();
    }
}
