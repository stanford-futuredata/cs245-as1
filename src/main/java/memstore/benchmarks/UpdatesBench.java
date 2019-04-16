package memstore.benchmarks;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import memstore.table.ColumnTable;
import memstore.table.IndexedRowTable;
import memstore.table.RowTable;
import memstore.table.Table;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * Test that checks that tables / assorted metadata are update correctly by putIntField
 * queries.
 *
 * Applies 10,000 updates to pre-loaded tables; runs predicatedAllColumnsSum and
 * predicatedColumnSum periodically to make sure queries return the right answers
 * in the face of updates.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class UpdatesBench implements TableBenchmark{
    DataLoader dl;
    RowTable rt;
    ColumnTable ct;
    IndexedRowTable it;
    int t1;

    int numUpdates;
    List<Integer> rowIds;
    List<Integer> colIds;
    List<Integer> values;

    public double[] getThresholds() {
        return new double[]{24.0, 45.0, 24.0};
    }

    @Setup
    public void prepare() throws IOException {
        int numRows = 10_000;
        int numCols = 100;
        numUpdates = 100_000;
        dl = new RandomizedLoader(
                GraderConstants.getSeed(),
                numRows,
                numCols
        );
        rowIds = new ArrayList<Integer>();
        colIds = new ArrayList<Integer>();
        values = new ArrayList<Integer>();
        Random random = new Random(GraderConstants.getSeed());
        for (int i = 0; i < numUpdates; i++) {
            rowIds.add(random.nextInt(numRows));
            colIds.add(random.nextInt(numCols));
            values.add(random.nextInt(1024));
        }

        t1 = 50;

        rt = new RowTable();
        ct = new ColumnTable();
        it = new IndexedRowTable(1);
        rt.load(dl);
        ct.load(dl);
        it.load(dl);
    }

    public long testTable(Table t) {
        long result = 0L;
        for (int i = 0; i < numUpdates; i++) {
            t.putIntField(rowIds.get(i), colIds.get(i), values.get(i));
            if (i % 1000 == 0) {
                result += t.predicatedAllColumnsSum(50);
                result += t.predicatedColumnSum(50, 950);
            }
        }
        return result;
    }

    @Benchmark
    public long testRowTable() { return testTable(rt); }

    @Benchmark
    public long testColumnTable() { return testTable(ct); }

    @Benchmark
    public long testIndexedTable() { return testTable(it); }
}
