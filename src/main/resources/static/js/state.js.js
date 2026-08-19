// 현재 대화 상태를 관리하는 객체
const ChatState = {
    answered: [], // 사용자가 선택한 답변 목록 [{questionId: '...', value: '...'}]
    
    addAnswer(questionId, value) {
        // 이미 답변한 항목이면 갱신, 아니면 추가
        const existingIndex = this.findIndex(questionId);
        if (existingIndex > -1) {
            this.answered[existingIndex].value = value;
            // 이후에 답변한 내용들은 초기화 (선택 변경 시 처리)
            this.answered = this.answered.slice(0, existingIndex + 1);
        } else {
            this.answered.push({ questionId, value });
        }
    },
    
    findIndex(questionId) {
        return this.answered.findIndex(a => a.questionId === questionId);
    },

    reset() {
        this.answered = [];
    }
};