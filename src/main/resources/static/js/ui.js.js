const UI = {
    chatHistory: document.getElementById('chat-history'),
    startScreen: document.getElementById('start-screen'),
    chatActions: document.getElementById('chat-actions'),

    showChatScreen() {
        this.startScreen.classList.add('hidden');
        this.chatActions.classList.remove('hidden');
    },

    appendMessage(sender, text, options = []) {
        const msgDiv = document.createElement('div');
        msgDiv.className = `msg ${sender}`;

        let bubbleContent = `<div class="msg-bubble">${text}</div>`;
        
        // 선택지가 있는 경우 버튼 생성
        if (options && options.length > 0) {
            bubbleContent += `<div class="options">`;
            options.forEach(opt => {
                bubbleContent += `<button class="option-btn" data-value="${opt.value}">${opt.label}</button>`;
            });
            bubbleContent += `</div>`;
        }

        msgDiv.innerHTML = bubbleContent;
        this.chatHistory.appendChild(msgDiv);
        this.chatHistory.scrollTop = this.chatHistory.scrollHeight;
        return msgDiv;
    },

    appendRecommendations(programs) {
        const msgDiv = document.createElement('div');
        msgDiv.className = 'msg bot';

        let html = `<div class="msg-bubble">`;
        html += `<p><strong>🎯 조건에 맞는 추천 프로그램입니다!</strong></p><br>`;
        
        if (programs.length === 0) {
            html += `<p>조건에 일치하는 프로그램이 없습니다. 조건을 다시 선택해 주세요.</p>`;
        } else {
            programs.forEach((p, index) => {
                html += `
                    <div class="recommendation-card">
                        <h3>${index + 1}. ${p.programName}</h3>
                        <p><strong>분류:</strong> ${p.category} | <strong>장소:</strong> ${p.location}</p>
                        <p><strong>운영요일:</strong> ${p.operatingDay} | <strong>비용:</strong> ${p.fee}</p>
                    </div>
                `;
            });
        }
        html += `</div>`;

        msgDiv.innerHTML = html;
        this.chatHistory.appendChild(msgDiv);
        this.chatHistory.scrollTop = this.chatHistory.scrollHeight;
    },

    clearChat() {
        this.chatHistory.innerHTML = '';
        this.chatActions.classList.add('hidden');
        this.startScreen.classList.remove('hidden');
    }
};