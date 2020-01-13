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
 * Full column scans on a narrow table should exhibit similar performance
 * on all table types: there is no way to get around a large fraction of the data.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class ColumnSumNarrowBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;

    public double[] getThresholds() {
        return new double[]{2.6, 2.5, 2.6};
    }

    @Setup
    public void prepare() throws IOException {
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                1_000_000,
                3
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
