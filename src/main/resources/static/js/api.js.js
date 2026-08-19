const Api = {
    async post(url, data) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            return null; // 실제 API 연결 전 시뮬레이션을 위해 예외 처리를 이와 같이 구성
        }
    }
};