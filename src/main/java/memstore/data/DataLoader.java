package memstore.data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public interface DataLoader {
    int getNumCols();
    List<ByteBuffer> getRows() throws IOException;
}
