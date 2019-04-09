package memstore.grader;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradescopeOutput {
    public int executionTime;
    public String stdout_visibility = "visible";
    public List<GradedTestResult> tests;
    public List<LeaderBoardValue> leaderboard;
    public GradescopeOutput(
            int eTime,
            List<GradedTestResult> results,
            List<LeaderBoardValue> leaderboard
            ) {
        this.executionTime = eTime;
        this.tests = results;
        this.leaderboard = leaderboard;
    }
}
