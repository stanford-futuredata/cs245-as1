package memstore.workloadbench;

import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Class for custom table benchmark, with a specific initial seed
 * for random number generation.
 * Creates a RandomizedDataLoader with 5 columns and 7.5 million rows,
 * then runs predicatedUpdate(), columnSum(), predicatedColumnSum(),
 * predicatedAllColumnsSum() `numQueries` times.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class NarrowCustomTableBench extends CustomTableBenchAbstract {
    public double getExpectedTime() {
        return 1300.0;
    }

    @Override
    public DataLoader getRandomLoader() {
        int numCols = 5;
        numRows = 15_000_000 / numCols;

        numQueries = 20;
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
