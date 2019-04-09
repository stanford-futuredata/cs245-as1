package memstore.benchmarks;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import memstore.table.ColumnTable;
import memstore.table.IndexedRowTable;
import memstore.table.RowTable;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Highly selective predicates should allow indexes to perform very well.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class PredicatedColumnSumBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;
    int t1, t2;

    public double[] getThresholds() {
        return new double[]{10.0, 10.0, 1.0};
    }

    @Setup
    public void prepare() throws IOException {
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                1_000_000,
                4
        );
        t1 = 500;
        t2 = 10;

        rt = new RowTable();
        ct = new ColumnTable();
        it = new IndexedRowTable(2);
        rt.load(dl);
        ct.load(dl);
        it.load(dl);
    }

    @Benchmark
    public long testRowTable() {
        return rt.predicatedColumnSum(t1, t2);
    }

    @Benchmark
    public long testColumnTable() {
        return ct.predicatedColumnSum(t1, t2);
    }

    @Benchmark
    public long testIndexedTable() {
        return it.predicatedColumnSum(t1, t2);
    }
}
