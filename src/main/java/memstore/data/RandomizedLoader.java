package memstore.data;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizedLoader implements DataLoader {
    private int seed;
    private int numRows;
    private int numCols;

    public RandomizedLoader(int seed, int numRows, int numCols) {
        this.numCols = numCols;
        this.numRows = numRows;
        this.seed = seed;
    }

    @Override
    public int getNumCols() {
        return numCols;
    }

    public List<ByteBuffer> getRows() throws IOException {
        Random random = new Random(seed);

        ArrayList<ByteBuffer> rowBytes = new ArrayList<>();
        for (int rowId = 0; rowId < numRows; rowId++) {
            ByteBuffer curRowBuffer = ByteBuffer.allocate(ByteFormat.FIELD_LEN*numCols);
            for (int i = 0; i < numCols; i++) {
                int val = random.nextInt(1024);
                curRowBuffer.putInt(val);
            }
            curRowBuffer.rewind();
            rowBytes.add(curRowBuffer);
        }
        return rowBytes;
    }
}
