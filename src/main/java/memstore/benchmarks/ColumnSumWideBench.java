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
 * Column scans on tables with many columns should do better on column stores where
 * the values will be stored in contiguous memory.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ColumnSumWideBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;

    public double[] getThresholds() {
        return new double[]{12.0, 3.0, 12.0};
    }

    @Setup
    public void prepare() throws IOException {
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                1_000_000,
                20
        );
        rt = new RowTable();
        ct = new ColumnTable();
        it = new IndexedRowTable(0);
        rt.load(dl);
        ct.load(dl);
        it.load(dl);
    }

    @Benchmark
    public long testRowTable() {
        return rt.columnSum();
    }

    @Benchmark
    public long testColumnTable() {
        return ct.columnSum();
    }

    @Benchmark
    public long testIndexedTable() {
        return it.columnSum();
    }
}
