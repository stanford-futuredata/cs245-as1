package memstore.workloadbench;

import memstore.GraderConstants;
import memstore.data.DataLoader;
import memstore.table.CustomTable;
import memstore.table.Table;

import java.io.IOException;
import java.util.Random;

/**
 * Abstract class for custom table benchmark.
 * Creates a RandomizedDataLoader with an unspecified number of rows
 * and columns, then runs predicatedUpdate(), columnSum(), predicatedColumnSum(),
 * predicatedAllColumnsSum() `numQueries` times.
 * Takes in a seed to seed random number generation.
 */
public abstract class CustomTableBenchAbstract {
    int numQueries;
    int upperBoundColumnValue = 1024;

    Table table;
    int numRows;
    Random random;
    int seed;

    public abstract DataLoader getRandomLoader();
    public abstract double getExpectedTime();

    public void prepare() throws IOException {
        this.seed = GraderConstants.getSeed(3);
        random = new Random(seed);
        DataLoader dataLoader = getRandomLoader();

        table = new CustomTable();
        table.load(dataLoader);
    }

    public long testQueries() {
        // Make sure testQueries uses the same seed across runs.
        random = new Random(seed);

        long finalResult = 0;
        for (int i = 0; i < numQueries; i++) {
            finalResult += table.columnSum();
            int randomThreshold = random.nextInt(upperBoundColumnValue);
            finalResult += table.predicatedUpdate(randomThreshold);
            finalResult += table.columnSum();
            int randomThreshold1 = random.nextInt(upperBoundColumnValue);
            int randomThreshold2 = random.nextInt(upperBoundColumnValue);
            finalResult += table.predicatedColumnSum(randomThreshold1, randomThreshold2);
            // run this expensive query less often
            if (i % 3 == 0) {
                int randomThreshold4 = random.nextInt(upperBoundColumnValue);
                finalResult += table.predicatedAllColumnsSum(randomThreshold4);
            }

            // add random puts to discourage precomputation during load
            for (int j = 0; j < 20; j++) {
                int randRowIdx = random.nextInt(numRows);
                int randVal = random.nextInt(upperBoundColumnValue);
                int colIdx = j % 5;
                table.putIntField(randRowIdx, colIdx, randVal);
            }
        }
        return finalResult;
    }
}
