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
 * Note that this query is idempotent: the updates do not affect the columns being
 * filtered or added.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class PredicatedUpdateBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;
    int t1;

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
        t1 = 10;

        rt = new RowTable();
        ct = new ColumnTable();
        it = new IndexedRowTable(0);
        rt.load(dl);
        ct.load(dl);
        it.load(dl);
    }

    @Benchmark
    public long testRowTable() {
        return rt.predicatedUpdate(t1);
    }

    @Benchmark
    public long testColumnTable() {
        return ct.predicatedUpdate(t1);
    }

    @Benchmark
    public long testIndexedTable() {
        return it.predicatedUpdate(t1);
    }
}
