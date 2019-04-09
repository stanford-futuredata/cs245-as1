package memstore.table;

import memstore.data.CSVLoader;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the `load` and the `getIntField` of different table implementations.
 * Loads a CSV or Randomized DataLoader, then checks int fields at passed-in
 * row and column IDs.
 */

public class TableLoadTest {
    @Test
    public void testLoadCSV() throws IOException {
        DataLoader dl = new CSVLoader("src/main/resources/test.csv", 3);
        List<Table> tables = Arrays.asList(
                new ColumnTable(),
                new RowTable()
        );
        for (Table t : tables) {
            String tableType = t.getClass().getSimpleName();
            t.load(dl);
            assertEquals(tableType, 1, t.getIntField(2, 0));
            assertEquals(tableType, 5, t.getIntField(1, 1));
        }
    }

    @Test
    public void testLoadRandom() throws IOException {
        int numIntCols = 100;
        DataLoader dl = new RandomizedLoader(0, 1000, numIntCols);

        List<Table> tables = Arrays.asList(
                new ColumnTable(),
                new RowTable()
        );
        for (Table t : tables) {
            String tableType = t.getClass().getSimpleName();
            t.load(dl);
            assertEquals(tableType, 375, t.getIntField(2, 0));
            assertEquals(tableType, 141, t.getIntField(46, 92));
        }
    }
}
