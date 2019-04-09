package memstore.grader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class GradedTestResultTest {
    @Test
    public void testJSONSerialize() throws JsonProcessingException {
        List<GradedTestResult> grades = Arrays.asList(
                new GradedTestResult(
                        "r1", 5.0,10.0
                )
        );
        GradescopeOutput output = new GradescopeOutput(
            10,
                grades,
                LeaderBoardValue.getLeaderBoardBatch(12.0)
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
        assertTrue(jsonOutput.contains("5.0"));
    }

}