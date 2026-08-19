// src/main/java/com/nie/curator/model/Program.java
@Getter @Builder
public class Program {
    private int id;
    private String category;           // 대분류 (교육, 해설 등)
    private Set<String> targetGroups;  // 대상그룹 (개인, 단체)
    private String programName;        // 프로그램명
    private String operatingDay;       // 운영요일
    private String targetLevel;        // 대상단계 (유아, 초등, 성인, 전체 등)
    private GradeRange gradeRange;     // 학년 (1~3, 전체 등)
    private Set<String> occupations;   // 직군 (전체, 교사, 공무원 등)
    private DurationInfo duration;     // 시간 (분, 자율)
    private String fee;                // 비용
    private String channel;            // 참여채널 (현장, 온라인)
    private String location;           // 장소태그 (해당교, 서천 국립생태원 등)
    private Set<String> topicTags;     // 주제태그
    private Set<String> activityTags;  // 활동태그
    private String progressType;       // 진행형태
    private String preparation;        // 준비물
}
