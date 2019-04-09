package memstore.grader;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradedTestResult {
    /** Visible to the student always. */
    public static final String VISIBLE = "visible";
    /** Never visible to the student. */
    public static final String HIDDEN = "hidden";
    /** Visible to the student only after the due date. */
    public static final String AFTER_DUE_DATE = "after_due_date";
    /** Visible to the student only after grades have been released. */
    public static final String AFTER_PUBLISHED = "after_published";

    // GradedTest annotation defaults
    static final String DEFAULT_NAME = "Unnamed Test";
    static final String DEFAULT_NUMBER = "";
    static final double DEFAULT_POINTS = 1.0;
    static final String DEFAULT_VISIBILITY = VISIBLE;

    public String name;
    public String number;
    public double score;
    public double max_score;

    public String visibility = DEFAULT_VISIBILITY;
    public String output;

    public GradedTestResult(
            String name, double score, double max_score
    ) throws IllegalArgumentException {
        this.name = name;
        this.score = score;
        this.max_score = max_score;
    }
}
