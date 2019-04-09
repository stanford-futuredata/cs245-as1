package memstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import memstore.benchmarks.*;
import memstore.grader.GradedTestResult;
import memstore.grader.GradescopeOutput;
import memstore.grader.LeaderBoardValue;
import memstore.table.Table;
import memstore.workloadbench.NarrowCustomTableBench;
import memstore.workloadbench.WideCustomTableBench;
import memstore.workloadbench.CustomTableBenchAbstract;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;
import picocli.CommandLine;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class is the main entry point for the grader: it will check for correctness of the tabes,
 * check their performance, and run a custom random workload on the CustomTable.
 */
public class GraderRunner {
    public static List<Supplier<TableBenchmark>> getSimpleTests() {
        return Arrays.asList(
                () -> new ColumnSumNarrowBench(),
                () -> new ColumnSumWideBench(),
                () -> new PredicatedAllColumnsSumBench(),
                () -> new PredicatedColumnSumBench(),
                () -> new PredicatedUpdateBench()
        );
    }

    public static List<Supplier<CustomTableBenchAbstract>> getWorkloadTests() {
        return Arrays.asList(
                () -> new WideCustomTableBench(),
                () -> new NarrowCustomTableBench()
        );
    }

    static class CorrectnessCheck {
        public List<GradedTestResult> results;
        public boolean customTableIsCorrect = false;
        public Map<String, Boolean> testCorrect;
    }

    /**
     * @return Results checking for correctness
     * @throws Exception
     */
    public static CorrectnessCheck testCorrectness() throws Exception {
        ArrayList<GradedTestResult> results = new ArrayList<>();

        List<Supplier<TableBenchmark>> tests = getSimpleTests();
        Map<String, Long> trueResults = GraderConstants.getAnswers();
        Map<String, Boolean> testCorrect = new HashMap<>();

        for (Supplier<TableBenchmark> curTestConstructor : tests) {
            TableBenchmark curTest = curTestConstructor.get();
            String testName = curTest.getClass().getSimpleName();

            curTest.prepare();

            long trueResult = trueResults.get(testName);
            long rowResult = curTest.testRowTable();
            long colResult = curTest.testColumnTable();
            long idxResult = curTest.testIndexedTable();

            int score = 0;
            int maxScore = 3;

            score += (rowResult == trueResult) ? 1 : 0;
            score += (colResult == trueResult) ? 1 : 0;
            score += (idxResult == trueResult) ? 1 : 0;

            GradedTestResult gResult = new GradedTestResult(
                    testName+":correctness",
                    score,
                    maxScore
            );
            gResult.output = String.format("Results: %d,%d,%d", rowResult, colResult, idxResult);
            System.out.println(testName+":correctness");
            System.out.println(gResult.output);
            results.add(gResult);
            testCorrect.put(testName, score == maxScore);
        }

        for (Supplier<CustomTableBenchAbstract> curTestConstructor : getWorkloadTests()) {
            CustomTableBenchAbstract curTest = curTestConstructor.get();
            String testName = curTest.getClass().getSimpleName();
            curTest.prepare();

            long trueResult = trueResults.get(testName);
            long testResult = curTest.testQueries();

            int maxScore = 3;
            int score = (testResult == trueResult) ? maxScore : 0;

            GradedTestResult gResult = new GradedTestResult(
                    testName+":correctness",
                    score,
                    maxScore
            );
            gResult.output = String.format("Result: %d", testResult);
            System.out.println(testName+":correctness");
            System.out.println(gResult.output);
            results.add(gResult);
            testCorrect.put(testName, score == maxScore);
        }

        CorrectnessCheck cc = new CorrectnessCheck();
        cc.results = results;
        cc.testCorrect = testCorrect;
        cc.customTableIsCorrect = testCorrect.get("NarrowCustomTableBench")
                && testCorrect.get("WideCustomTableBench");

        return cc;
    }

    /**
     * @return Results checking for relative timings
     * @throws RunnerException
     */
    public static List<GradedTestResult> testTimings(int numIter) throws RunnerException {
        int numForks = 4;
        if (numIter < 4) {
            numForks = 1;
        }
        int numIterPerFork = numIter/numForks;
        // Comment out one or more of these if you want to focus on testing a specific benchmark
        // locally!
        Options opt = new OptionsBuilder()
                .include(ColumnSumNarrowBench.class.getSimpleName())
                .include(ColumnSumWideBench.class.getSimpleName())
                .include(PredicatedAllColumnsSumBench.class.getSimpleName())
                .include(PredicatedColumnSumBench.class.getSimpleName())
                .include(PredicatedUpdateBench.class.getSimpleName())
                .verbosity(VerboseMode.NORMAL)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(1))
                .measurementIterations(numIterPerFork)
                .measurementTime(TimeValue.milliseconds(300))
                .forks(numForks)
                .build();

        Collection<RunResult> runResults = new Runner(opt).run();

        // benchmark -> method -> timing
        Map<String, Map<String, Double>> runTimes = new HashMap<>();
        for (RunResult curRunResult : runResults) {

            // Get simple benchmark class name from fully qualified name
            String benchName = curRunResult.getParams().getBenchmark();
            benchName = benchName.substring(0, benchName.lastIndexOf('.'));
            benchName = benchName.substring(benchName.lastIndexOf('.')+1);

            if (!runTimes.containsKey(benchName)) {
                runTimes.put(benchName, new HashMap<>());
            }

            Result primaryResult = curRunResult.getPrimaryResult();
            String methodName = primaryResult.getLabel();
            double avgTime = primaryResult.getScore();
            double bestTime = avgTime;

            // Save the fastest iteration as the official timing
            Collection<IterationResult> iResults = curRunResult.getAggregatedResult().getIterationResults();
            for (IterationResult iR : iResults) {
                double iterTime = iR.getPrimaryResult().getScore();
                if (iterTime < bestTime) {
                    bestTime = iterTime;
                }
            }
            runTimes.get(benchName).put(methodName, bestTime);
        }

        Map<String, Map<String, Double>> timeFactors = new HashMap<>();
        List<Supplier<TableBenchmark>> tests = getSimpleTests();
        for (Supplier<TableBenchmark> tbc : tests) {
            TableBenchmark tb = tbc.get();
            String benchName = tb.getClass().getSimpleName();
            HashMap<String, Double> curTimeFactors = new HashMap<>();
            double[] factors = tb.getThresholds();
            curTimeFactors.put("testRowTable", factors[0]);
            curTimeFactors.put("testColumnTable", factors[1]);
            curTimeFactors.put("testIndexedTable", factors[2]);
            timeFactors.put(benchName, curTimeFactors);
        }

        ArrayList<GradedTestResult> results = new ArrayList<>();
        for (String benchmarkName : runTimes.keySet()) {
            Map<String, Double> bRunTimes = runTimes.get(benchmarkName);
            Map<String, Double> bTimeFactors = timeFactors.get(benchmarkName);

            double rowTime = bRunTimes.get("testRowTable");
            int score = 0;
            int maxScore = 0;
            StringBuilder sb = new StringBuilder();
            for (String methodToCheck : bRunTimes.keySet()) {
                double time = bRunTimes.get(methodToCheck);
                double timeFactor = bTimeFactors.get(methodToCheck);

                sb.append(String.format("Timing %s: %.3f < %.3f\n", methodToCheck, time, timeFactor));
                boolean isLess = time < timeFactor;
                if (isLess) {
                    score += 2;
                }
                maxScore += 2;
            }
            GradedTestResult gResult = new GradedTestResult(
                    benchmarkName + ":timing",
                    score,
                    maxScore
            );
            gResult.output = sb.toString();
            results.add(gResult);
        }
        return results;
    }

    static class CustomTiming {
        double time;
        List<GradedTestResult> baselineResults;
        public CustomTiming(double t, List<GradedTestResult> bResults) {
            this.time = t;
            this.baselineResults = bResults;
        }
    }

    /**
     * Custom workload benchmarks
     */
    public static CustomTiming benchCustom(int numIter) throws RunnerException {
        int numForks = 2;
        int numIterPerFork = numIter;
        if (numIter < numForks)  {
            numForks = 1;
        } else {
            numIterPerFork = numIter / numForks;
        }

        Options opt = new OptionsBuilder()
                .include(WideCustomTableBench.class.getSimpleName())
                .include(NarrowCustomTableBench.class.getSimpleName())
                .verbosity(VerboseMode.NORMAL)
                .warmupIterations(1)
                .warmupTime(TimeValue.seconds(4))
                .measurementIterations(numIterPerFork)
                .measurementTime(TimeValue.seconds(8))
                .forks(numForks)
                .build();

        Collection<RunResult> runResults = new Runner(opt).run();

        double totalQueryTime = 0;
        List<Double> queryTimes = new ArrayList<>();
        List<Supplier<CustomTableBenchAbstract>> workloadTests = getWorkloadTests();
        Map<String, Double> expectedWorkloadTimes = new HashMap<>();
        for (Supplier<CustomTableBenchAbstract> ctbc : workloadTests) {
            CustomTableBenchAbstract ctb = ctbc.get();
            String bName = ctb.getClass().getSimpleName();
            expectedWorkloadTimes.put(bName, ctb.getExpectedTime());
        }
        ArrayList<GradedTestResult> results = new ArrayList<>();

        for (RunResult curRunResult : runResults) {
            // Get simple benchmark class name from fully qualified name
            String benchName = curRunResult.getParams().getBenchmark();
            benchName = benchName.substring(0, benchName.lastIndexOf('.'));
            benchName = benchName.substring(benchName.lastIndexOf('.')+1);

            Result primaryResult = curRunResult.getPrimaryResult();
            double avgTime = primaryResult.getScore();
            double bestTime = avgTime;

            // Save the fastest iteration as the official timing
            Collection<IterationResult> iResults = curRunResult.getAggregatedResult().getIterationResults();
            for (IterationResult iR : iResults) {
                double iterTime = iR.getPrimaryResult().getScore();
                if (iterTime < bestTime) {
                    bestTime = iterTime;
                }
            }
            queryTimes.add(bestTime);
            totalQueryTime += avgTime;

            double expectedTime = expectedWorkloadTimes.get(benchName);
            double maxScore = 2.0;
            double score = bestTime < expectedTime ? maxScore : 0.0;
            GradedTestResult gResult = new GradedTestResult(
                    benchName+ ":timing",
                    score,
                    maxScore
            );
            gResult.output = String.format(
                    "Best Timing %s: %.3f < %.3f, Avg Time: %.3f\n",
                    benchName,
                    bestTime,
                    expectedTime,
                    avgTime
                    );
            results.add(gResult);
        }
        return new CustomTiming(totalQueryTime, results);
    }

    /**
     * Runs the main grading benchmarks and tests.
     * @param args commandline args, see GraderArgs
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        GraderArgs appArgs = new GraderArgs();
        CommandLine commandLine = new CommandLine(appArgs);
        commandLine.parse(args);
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            System.exit(0);
        }

        List<GradedTestResult> testResults = new ArrayList<>();
        List<LeaderBoardValue> leaderBoardValues = null;
        boolean runAll = appArgs.mode.contains("all");
        long startTime = System.currentTimeMillis();
        CorrectnessCheck cc = new CorrectnessCheck();
        if (runAll || appArgs.mode.contains("correct"))  {
            cc = testCorrectness();
            testResults.addAll(cc.results);
        }
        if (runAll || appArgs.mode.contains("timing")) {
            testResults.addAll(testTimings(appArgs.numIter));
        }
        if (runAll || appArgs.mode.contains("custom")) {
            if ((runAll && cc.customTableIsCorrect) || (!runAll)) {
                CustomTiming customTiming = benchCustom(appArgs.numCustomIter);
                leaderBoardValues = LeaderBoardValue.getLeaderBoardBatch(customTiming.time);
                testResults.addAll(customTiming.baselineResults);
            }
            else {
                System.out.println("Custom Table Results incorrect, not eligible for leaderboard");
            }
        }
        long endtime = System.currentTimeMillis();
        GradescopeOutput output = new GradescopeOutput(
                (int) ((endtime - startTime) * 1.0e-3),
                testResults,
                leaderBoardValues
        );

        ObjectMapper objectMapper = new ObjectMapper();
        if (appArgs.outFile == null) {
            String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
            System.out.println(jsonOutput);
        } else {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(
                    appArgs.outFile,
                    output
            );
        }
    }
}
