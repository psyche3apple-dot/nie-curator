// src/main/java/com/nie/curator/service/RecommendationService.java
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ExcelProgramLoader loader;

    public List<Program> filterAndScore(List<AnswerDto> answers) {
        List<Program> candidates = loader.getAllPrograms();

        // 1. 필수조건 필터링 (Hard Filter)
        candidates = candidates.stream()
                .filter(p -> checkMandatoryConditions(p, answers))
                .collect(Collectors.toList());

        // 2. 선호조건 스코어링 및 정렬 (Soft Score)
        Map<Program, Integer> scores = new HashMap<>();
        for (Program p : candidates) {
            scores.put(p, calculateScore(p, answers));
        }

        return candidates.stream()
                .sorted((p1, p2) -> scores.get(p2).compareTo(scores.get(p1))) // 내림차순 정렬
                .collect(Collectors.toList());
    }

    private boolean checkMandatoryConditions(Program p, List<AnswerDto> answers) {
        for (AnswerDto answer : answers) {
            // "ANY" 또는 "UNKNOWN"인 경우 필터링 생략
            if ("ANY".equals(answer.getValue()) || "UNKNOWN".equals(answer.getValue())) continue;

            switch (answer.getQuestionId()) {
                case "context": // 첫 질문 라우팅 룰 적용
                    if (!matchContext(p, answer.getValue())) return false;
                    break;
                case "grade":
                    if (!p.getGradeRange().isEligible(Integer.parseInt(answer.getValue()))) return false;
                    break;
                case "occupation":
                    if (!p.getOccupations().contains("전체") && !p.getOccupations().contains(answer.getValue())) return false;
                    break;
                // 참여 장소를 명확히 제한한 경우 (온라인 / 국립생태원 / 해당교)
                case "location":
                    if (!matchLocationFilter(p, answer.getValue())) return false;
                    break;
            }
        }
        return true;
    }

    private int calculateScore(Program p, List<AnswerDto> answers) {
        int score = 0;
        for (AnswerDto answer : answers) {
            if ("ANY".equals(answer.getValue())) continue;
            // 주제, 경험, 비용, 시간에 따른 가중치 부여 (+3, +2, +1)
            if ("experience".equals(answer.getQuestionId()) && matchExperience(p, answer.getValue())) score += 3;
            if ("topic".equals(answer.getQuestionId()) && matchTopic(p, answer.getValue())) score += 3;
            // ... 추가 선호 점수 로직
        }
        return score;
    }
}