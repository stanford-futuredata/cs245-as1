package memstore.table;

import memstore.data.CSVLoader;
import memstore.data.DataLoader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the correctness of the different table implementations for
 * the `predicatedUpdate` query.
 */
public class PredicatedUpdateTest {
    DataLoader dl;

    public PredicatedUpdateTest() {
        dl = new CSVLoader(
                "src/main/resources/test.csv",
                5
        );
    }

    @Test
    public void testRowTable() throws IOException {
        RowTable rt = new RowTable();
        rt.load(dl);
        assertEquals(9, rt.predicatedUpdate(3));
    }

    @Test
    public void testColumnTable() throws IOException {
        ColumnTable ct = new ColumnTable();
        ct.load(dl);
        assertEquals(9, ct.predicatedUpdate(3));
    }

    @Test
    public void testIndexedTable() throws IOException {
        IndexedRowTable it = new IndexedRowTable(0);
        it.load(dl);
        assertEquals(9, it.predicatedUpdate(3));
    }
}
