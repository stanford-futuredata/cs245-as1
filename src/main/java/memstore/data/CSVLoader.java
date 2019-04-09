package memstore.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CSVLoader implements DataLoader {
    public String pathToCSV;
    private int numCols;

    public CSVLoader(String pathToCSV, int numCols) {
        this.pathToCSV = pathToCSV;
        this.numCols = numCols;
    }

    @Override
    public int getNumCols() {
        return numCols;
    }

    public List<ByteBuffer> getRows() throws IOException {
        Reader in = new FileReader(pathToCSV);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);

        ArrayList<ByteBuffer> rowBytes = new ArrayList<>();
        for (CSVRecord record : records) {
            ByteBuffer curRowBuffer = ByteBuffer.allocate(ByteFormat.FIELD_LEN*numCols);
            for (int i = 0; i < numCols; i++) {
                String curField = record.get(i);
                    int val = Integer.parseInt(curField);
                    curRowBuffer.putInt(val);
            }
            curRowBuffer.rewind();
            rowBytes.add(curRowBuffer);
        }
        return rowBytes;
    }
}
