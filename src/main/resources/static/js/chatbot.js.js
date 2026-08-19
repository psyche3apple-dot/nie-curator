document.addEventListener('DOMContentLoaded', () => {
    const startBtn = document.getElementById('start-btn');
    const restartBtn = document.getElementById('restart-btn');

    // 시작하기 버튼 클릭
    startBtn.addEventListener('click', async () => {
        UI.showChatScreen();
        ChatState.reset();
        await fetchNextQuestion();
    });

    // 처음부터 다시하기 버튼 클릭
    restartBtn.addEventListener('click', () => {
        ChatState.reset();
        UI.clearChat();
    });

    // 다음 질문 요청 또는 추천 결과 요청
    async function fetchNextQuestion() {
        const result = await Api.post('/api/curator/next', ChatState.answered);
        
        if (!result) {
            UI.appendMessage('bot', '서버 통신 중 오류가 발생했습니다.');
            return;
        }

        // 반환된 데이터가 질문 형태인지, 추천 프로그램 리스트인지 판별
        if (result.type === 'RECOMMENDATION' || Array.isArray(result)) {
            // 추천 결과인 경우
            const programs = Array.isArray(result) ? result : result.programs;
            UI.appendRecommendations(programs);
        } else if (result.question) {
            // 다음 질문인 경우
            const msgNode = UI.appendMessage('bot', result.question, result.options);
            
            // 옵션 버튼 클릭 이벤트 바인딩
            msgNode.querySelectorAll('.option-btn').forEach(btn => {
                btn.addEventListener('click', async (e) => {
                    const selectedValue = e.target.getAttribute('data-value');
                    const selectedText = e.target.textContent;

                    // 사용자가 선택한 답변 화면에 표시
                    UI.appendMessage('user', selectedText);

                    // 상태 저장 후 다음 단계 진행
                    ChatState.addAnswer(result.id, selectedValue);
                    
                    // 버튼 비활성화 (중복 클릭 방지)
                    msgNode.querySelectorAll('.option-btn').forEach(b => b.disabled = true);

                    await fetchNextQuestion();
                });
            });
        }
    }
});