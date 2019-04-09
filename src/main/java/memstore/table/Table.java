package memstore.table;

import memstore.data.DataLoader;

import java.io.IOException;

/**
 * Table interface, with one method for each query we wish to support.
 * Tables with specific storage formats should implement this interface.
 */
public interface Table {
    /**
     * Loads data into the table through passed-in data loader. Is not timed.
     *
     * @param loader Loader to load data from.
     * @throws IOException
     */
    void load(DataLoader loader) throws IOException;

    /**
     * Returns the int field at row `rowId` and column `colId`.
     */
    int getIntField(int rowId, int colId);

    /**
     * Inserts the passed-in int field at row `rowId` and column `colId`.
     */
    void putIntField(int rowId, int colId, int field);

    /**
     * Implements the query
     *  SELECT SUM(col0) FROM table;
     *
     *  Returns the sum of all elements in the first column of the table.
     */
    long columnSum();

    /**
     * Implements the query
     *  SELECT SUM(col0) FROM table WHERE col1 > threshold1 AND col2 < threshold2;
     *
     *  Returns the sum of all elements in the first column of the table,
     *  subject to the passed-in predicates.
     */
    long predicatedColumnSum(int threshold1, int threshold2);

    /**
     * Implements the query
     *  SELECT SUM(col0) + SUM(col1) + ... + SUM(coln) FROM table WHERE col0 > threshold;
     *
     *  Returns the sum of all elements in the rows which pass the predicate.
     */
    long predicatedAllColumnsSum(int threshold);

    /**
     * Implements the query
     *   UPDATE(col3 = col1 + col2) WHERE col0 < threshold;
     *
     *   Returns the number of rows updated.
     */
    int predicatedUpdate(int threshold);
}
