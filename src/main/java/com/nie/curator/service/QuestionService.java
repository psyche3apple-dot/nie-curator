package com.nie.curator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nie.curator.dto.AnswerDto;
import com.nie.curator.dto.QuestionDto;
import com.nie.curator.model.Program;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionService {

    // 우선순위가 정의된 전체 질문 목록
    private List<QuestionDto> questionCatalog = new ArrayList<>();

    // 서버 구동 시 questions.json 파일 자동 로드
    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("config/questions.json").getInputStream()) {
            this.questionCatalog = mapper.readValue(is, new TypeReference<List<QuestionDto>>() {});
            log.info("questions.json 로드 완료: 총 {}개 질문 등록됨", questionCatalog.size());
        } catch (IOException e) {
            log.error("config/questions.json 파일 로드 실패", e);
        }
    }

    public QuestionDto getNextQuestion(List<Program> candidates, List<AnswerDto> answered) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        Set<String> answeredIds = (answered != null)
                ? answered.stream().map(AnswerDto::getQuestionId).collect(Collectors.toSet())
                : Collections.emptySet();

        for (QuestionDto question : questionCatalog) {
            if (answeredIds.contains(question.getId())) continue;

            // 해당 질문이 현재 후보군을 실제로 구분할 수 있는지(속성의 다양성이 있는지) 확인
            if (hasDiversity(candidates, question.getId())) {
                return question;
            }
        }
        return null; // 더 이상 물어볼 유의미한 질문이 없음
    }

    private boolean hasDiversity(List<Program> candidates, String questionId) {
        // 남은 후보 프로그램이 1개 이하면 더 이상 질문할 필요가 없음
        if (candidates.size() <= 1) {
            return false;
        }

        switch (questionId) {
            case "location":
                // 후보군 내 장소 종류가 2개 이상일 때만 장소 질문 수행
                long locationCount = candidates.stream()
                        .map(Program::getLocation)
                        .filter(Objects::nonNull)
                        .distinct()
                        .count();
                return locationCount > 1;

            case "topic":
                // 후보군 내 주제 태그 종류가 2개 이상일 때만 주제 질문 수행
                long topicCount = candidates.stream()
                        .filter(p -> p.getTopicTags() != null)
                        .flatMap(p -> p.getTopicTags().stream())
                        .distinct()
                        .count();
                return topicCount > 1;

            case "experience":
                // 후보군 내 활동 태그 종류가 2개 이상일 때만 질문 수행
                long experienceCount = candidates.stream()
                        .filter(p -> p.getActivityTags() != null)
                        .flatMap(p -> p.getActivityTags().stream())
                        .distinct()
                        .count();
                return experienceCount > 1;

            default:
                // context(방문목적), grade(연령/학년) 등 기본 질문은 계속 진행
                return true;
        }
    }
}