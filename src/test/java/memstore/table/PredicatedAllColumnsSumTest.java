package memstore.table;

import memstore.data.CSVLoader;
import memstore.data.DataLoader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the correctness of the different table implementations for
 * the `predicatedAllColumnsSum` query.
 */
public class PredicatedAllColumnsSumTest {
    DataLoader dl;

    public PredicatedAllColumnsSumTest() {
        dl = new CSVLoader(
                "src/main/resources/test.csv",
                5
        );
    }

    @Test
    public void testRowTable() throws IOException {
        RowTable rt = new RowTable();
        rt.load(dl);
        assertEquals(166, rt.predicatedAllColumnsSum(3));
    }

    @Test
    public void testColumnTable() throws IOException {
        ColumnTable ct = new ColumnTable();
        ct.load(dl);
        assertEquals(166, ct.predicatedAllColumnsSum(3));
    }

    @Test
    public void testIndexedTable() throws IOException {
        IndexedRowTable it = new IndexedRowTable(0);
        it.load(dl);
        assertEquals(166, it.predicatedAllColumnsSum(3));
    }
}
