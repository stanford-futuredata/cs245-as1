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
public class PutGetTest {
    DataLoader dl;

    public PutGetTest() {
        dl = new CSVLoader(
                "src/main/resources/test.csv",
                5
        );
    }

    @Test
    public void testRowTable() throws IOException {
        RowTable rt = new RowTable();
        rt.load(dl);
        int rowId = 4;
        int colId = 0;
        assertEquals(8, rt.getIntField(rowId, colId));
        rt.putIntField(rowId, colId, 10);
        assertEquals(10, rt.getIntField(rowId, colId));
    }

    @Test
    public void testColumnTable() throws IOException {
        ColumnTable ct = new ColumnTable();
        ct.load(dl);
        int rowId = 4;
        int colId = 0;
        assertEquals(8, ct.getIntField(rowId, colId));
        ct.putIntField(rowId, colId, 10);
        assertEquals(10, ct.getIntField(rowId, colId));
    }

    @Test
    public void testIndexedTable() throws IOException {
        IndexedRowTable it = new IndexedRowTable(0);
        it.load(dl);
        int rowId = 4;
        int colId = 0;
        assertEquals(8, it.getIntField(rowId, colId));
        it.putIntField(rowId, colId, 10);
        assertEquals(10, it.getIntField(rowId, colId));
    }
}
