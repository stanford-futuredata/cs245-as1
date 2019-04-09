package memstore;

import java.util.HashMap;
import java.util.Map;

public class GraderConstants {
    public static int[] seeds = {0, 1, 2, 3, 4};
    public static int getSeed() {
        return seeds[0];
    }
    public static int getSeed(int seedId) {
        return seeds[seedId];
    }
    public static Map<String, Long> getAnswers() {
        Map<String, Long> answers = new HashMap<>();
        answers.put("ColumnSumNarrowBench", 511316437L);
        answers.put("ColumnSumWideBench", 510987351L);
        answers.put("PredicatedAllColumnsSumBench", 4861027493L);
        answers.put("PredicatedColumnSumBench", 2606019L);
        answers.put("PredicatedUpdateBench", 9820L);
        answers.put("NarrowCustomTableBench", 147213000497L);
        answers.put("WideCustomTableBench", 785748420116L);
        return answers;
    }
}
