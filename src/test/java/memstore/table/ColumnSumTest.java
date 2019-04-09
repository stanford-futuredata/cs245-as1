package memstore.table;

import memstore.data.CSVLoader;
import memstore.data.DataLoader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the correctness of the different table implementations for
 * the `columnSum` query.
 */
public class ColumnSumTest {
    DataLoader dl;

    public ColumnSumTest() {
        dl = new CSVLoader(
                "src/main/resources/test.csv",
                5
        );
    }

    @Test
    public void testRowTable() throws IOException {
        RowTable rt = new RowTable();
        rt.load(dl);
        assertEquals(68, rt.columnSum());
    }

    @Test
    public void testColumnTable() throws IOException {
        ColumnTable ct = new ColumnTable();
        ct.load(dl);
        assertEquals(68, ct.columnSum());
    }

    @Test
    public void testIndexedTable() throws IOException {
        IndexedRowTable it = new IndexedRowTable(0);
        it.load(dl);
        assertEquals(68, it.columnSum());
    }
}
