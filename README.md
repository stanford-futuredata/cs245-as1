# CS245 Assignment 1, MemStore

## Running JUnit tests

The starter code has JUnit tests to sanity check the correctness of implementations.
These can be run using your favorite IDE's "Run All Tests" functionality. 
Only the `CSVLoaderTest` and `RandomizedLoaderTest` will pass with the starter code.
Alternatively the following commands can be used to run all unit tests or run a specific unit test:
```bash
mvn test
mvn -Dtest=memstore.table.ColumnSumTest test
```

## Replicating Gradescope tests

The code can be compiled using Maven; the following command generates a jar file
for the entire project,

```bash
mvn package
```

You can simulate what the grader machines will do by running the autograder script.
The script has command line arguments you can see here:

```bash
java -jar target/benchmarks.jar -h
```

The complete command looks like

```bash
java -Xmx1328m -Xms500m -jar target/benchmarks.jar -m [mode] -i [num_iter] -ci [num_custom_iter] -o ./out.json
```

- [num_iter] is the number of times the timing benchmarks (excluding those for the
  custom table) are run. A higher `num_iter` value gives more accurate timing
  estimates but takes longer to run.
- [num_custom_iter] is the number of times the timing benchmarks for the custom
  table are run.
- [mode] can be one of `all|timings|correctness|custom`. This is used to
  determine which tests to run.
  Timings just runs the timing tests, correctness just runs the correctness
  tests, and custom runs the custom query workload on the CustomTable.
- Xmx, Xms control the  memory allocated to java

On Gradescope, we will run the following command,

```bash
bash run_autograder.sh
```

Gradescope runs will take around 10 minutes to complete, so make sure to try
submitting well before the deadline: you will see your final grade on the
standard tests when the grader completes, allowing you to iterate and fix issues.

## Submission

Prepare ColumnTable.java, CustomTable.java, IndexedTable.java, RowTable.java.
Then, submit a zip archive with just these files. You can do this using the
following bash script:

```bash
bash create_submission.sh
```
