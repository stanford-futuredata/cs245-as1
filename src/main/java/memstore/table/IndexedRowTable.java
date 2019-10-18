package memstore.table;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import javafx.util.Pair;
import memstore.data.ByteFormat;
import memstore.data.DataLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * IndexedRowTable, which stores data in row-major format.
 * That is, data is laid out like
 *   row 1 | row 2 | ... | row n.
 *
 * Also has a tree index on column `indexColumn`, which points
 * to all row indices with the given value.
 */
public class IndexedRowTable implements Table {

    int numCols;
    int numRows;
    private TreeMap<Integer, IntArrayList> index;
    private ByteBuffer rows;
    private int indexColumn;

    /**
     * 用户可调用此函数以设置哪一列为 indexColumn
     * @param indexColumn
     */
    public IndexedRowTable(int indexColumn) {
        this.indexColumn = indexColumn;
    }

    /**
     * Loads data into the table through passed-in data loader. Is not timed.
     *
     * @param loader Loader to load data from.
     * @throws IOException
     */
    @Override
    public void load(DataLoader loader) throws IOException {
        numCols = loader.getNumCols();
        List<ByteBuffer> rows = loader.getRows();
        numRows = rows.size();
        this.rows = ByteBuffer.allocate(ByteFormat.FIELD_LEN * numRows * numCols);
        index = new TreeMap<>();

        for(int rowId = 0; rowId < numRows; rowId++) {
            ByteBuffer curRow = rows.get(rowId);
            for(int colId = 0; colId < numCols; colId++) {
                int offset = ByteFormat.FIELD_LEN * (rowId * numCols + colId);
                int value = curRow.getInt(ByteFormat.FIELD_LEN * colId);
                this.rows.putInt(offset, value);
                // 下面开始建立索引
                if(colId == indexColumn) {
                    if(!index.containsKey(value)) {
                        index.put(value, new IntArrayList());
                    }
                    index.get(value).add(rowId);
                }
            }
        }
    }

    /**
     * Returns the int field at row `rowId` and column `colId`.
     */
    @Override
    public int getIntField(int rowId, int colId) {
        int offset = ByteFormat.FIELD_LEN * (rowId * numCols + colId);
        return rows.getInt(offset);
    }

    /**
     * Inserts the passed-in int field at row `rowId` and column `colId`.
     */
    @Override
    public void putIntField(int rowId, int colId, int field) {
        int offset = ByteFormat.FIELD_LEN * (rowId * numCols + colId);
        rows.putInt(offset, field);
    }

    /**
     * Implements the query
     *  SELECT SUM(col0) FROM table;
     *
     *  Returns the sum of all elements in the first column of the table.
     */
    @Override
    public long columnSum() {
        long sum = 0;
        if(indexColumn == 0) {
            for(int value : index.keySet()) {
                IntArrayList cols = index.get(value);
                sum += value * cols.size();
            }
        }
        return sum;
    }

    /**
     * Implements the query
     *  SELECT SUM(col0) FROM table WHERE col1 > threshold1 AND col2 < threshold2;
     *
     *  Returns the sum of all elements in the first column of the table,
     *  subject to the passed-in predicates.
     */
    @Override
    public long predicatedColumnSum(int threshold1, int threshold2) {
        long sum = 0;
        return sum;
    }

    /**
     * Implements the query
     *  SELECT SUM(col0) + SUM(col1) + ... + SUM(coln) FROM table WHERE col0 > threshold;
     *
     *  Returns the sum of all elements in the rows which pass the predicate.
     */
    @Override
    public long predicatedAllColumnsSum(int threshold) {
        // TODO: Implement this!
        return 0;
    }

    /**
     * Implements the query
     *   UPDATE(col3 = col1 + col2) WHERE col0 < threshold;
     *
     *   Returns the number of rows updated.
     */
    @Override
    public int predicatedUpdate(int threshold) {
        // TODO: Implement this!
        return 0;
    }
}
