package memstore.table;

import memstore.data.ByteFormat;
import memstore.data.DataLoader;
import memstore.data.RandomizedLoader;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests the correctness of the Randomized loader, which given an input
 * seed, generates a table with integer fields of the desired number of
 * rows and columns.
 */
public class RandomizedLoaderTest {
    @Test
    public void test() throws IOException {
        int numIntCols = 100;
        DataLoader dl = new RandomizedLoader(0, 1000, numIntCols);
        List<ByteBuffer> rows = dl.getRows();
        assertTrue(rows.get(0).getInt(50* ByteFormat.FIELD_LEN) < 2000);
    }

}