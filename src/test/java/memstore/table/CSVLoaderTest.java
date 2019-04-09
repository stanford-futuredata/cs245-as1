package memstore.table;

import memstore.data.CSVLoader;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the correctness of the CSV loader.
 */
public class CSVLoaderTest {
    @Test
    public void test() throws IOException {
        CSVLoader loader = new CSVLoader(
                "src/main/resources/test.csv", 5
        );
        List<ByteBuffer> rowBuffers = loader.getRows();
        ByteBuffer firstRow = rowBuffers.get(0);
        int[] expectedFirstRow = {0, 2, 2, 3, 4};
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedFirstRow[i], firstRow.getInt(4*i));
        }
    }

}