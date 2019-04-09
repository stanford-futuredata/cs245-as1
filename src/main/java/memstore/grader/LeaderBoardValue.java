package memstore.grader;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaderBoardValue {
    public String name;
    public double value;
    public String order;
    public LeaderBoardValue(String name, double value, String order) {
        this.name = name;
        this.value = value;
        this.order = order;
    }
    public static List<LeaderBoardValue> getLeaderBoardBatch(
            double time
    ) {
        return Arrays.asList(
                new LeaderBoardValue("Time", time, "asc")
        );
    }
}
