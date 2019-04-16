package memstore.table;

import memstore.data.CSVLoader;
import memstore.data.DataLoader;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CustomTableTest {
    DataLoader dl;

    public CustomTableTest() {
        dl = new CSVLoader(
                "src/main/resources/test.csv",
                5
        );
    }

    @Test
    public void testQueries() throws IOException {
        Table ct = new CustomTable();
        ct.load(dl);
        assertEquals(68, ct.columnSum());
        assertEquals(166, ct.predicatedAllColumnsSum(3));
        assertEquals(342, ct.predicatedAllColumnsSum(-1));
        assertEquals(49, ct.predicatedColumnSum(3, 5));
        assertEquals(9, ct.predicatedUpdate(3));
        assertEquals(375, ct.predicatedAllColumnsSum(-1));
    }
}