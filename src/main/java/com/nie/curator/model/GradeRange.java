// src/main/java/com/nie/curator/model/GradeRange.java
@Getter @AllArgsConstructor
public class GradeRange {
    private Integer min;
    private Integer max;
    private boolean all;

    public boolean isEligible(int userGrade) {
        if (all) return true;
        return userGrade >= min && userGrade <= max;
    }
}