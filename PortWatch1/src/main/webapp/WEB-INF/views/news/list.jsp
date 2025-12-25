<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>실시간 증권 뉴스 - PortWatch</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1400px;
        }
        
        .header {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        
        .header h1 {
            font-size: 2.5em;
            font-weight: 700;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin: 0 0 10px 0;
        }
        
        /* ✅ 필터 컨트롤 */
        .filter-controls {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }
        
        .filter-tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 15px;
            flex-wrap: wrap;
        }
        
        .filter-btn {
            padding: 10px 20px;
            border: none;
            background: #f3f4f6;
            color: #6b7280;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .filter-btn:hover {
            background: #e5e7eb;
        }
        
        .filter-btn.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .news-count {
            font-size: 1.1em;
            font-weight: 600;
            color: #1f2937;
            margin-top: 10px;
        }
        
        .news-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 25px;
        }
        
        /* ✅ 개선된 뉴스 카드 - 버퍼링 없음 */
        .news-card {
            background: white;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            cursor: pointer;
            position: relative;
        }
        
        .news-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 12px 30px rgba(102, 126, 234, 0.3);
        }
        
        /* ✅ 클릭 영역 전체 */
        .news-card a {
            text-decoration: none;
            color: inherit;
            display: block;
            padding: 20px;
        }
        
        .news-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .news-source {
            font-size: 0.85em;
            font-weight: 600;
            color: #667eea;
            background: rgba(102, 126, 234, 0.1);
            padding: 5px 12px;
            border-radius: 20px;
        }
        
        .country-badge {
            font-size: 1.2rem;
        }
        
        .news-title {
            font-size: 1.1em;
            font-weight: 600;
            color: #1f2937;
            line-height: 1.5;
            margin-bottom: 10px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .news-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 0.85em;
            color: #6b7280;
            margin-top: 15px;
        }
        
        .stock-badge {
            background: #f3f4f6;
            color: #374151;
            padding: 5px 10px;
            border-radius: 8px;
            font-weight: 600;
        }
        
        /* 로딩 애니메이션 */
        .loading {
            text-align: center;
            padding: 50px;
            color: white;
            font-size: 1.2em;
        }
        
        .loading::after {
            content: '...';
            animation: dots 1.5s steps(4, end) infinite;
        }
        
        @keyframes dots {
            0%, 20% { content: '.'; }
            40% { content: '..'; }
            60% { content: '...'; }
            80%, 100% { content: ''; }
        }
        
        /* 빈 상태 */
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 20px;
        }
        
        .empty-state i {
            font-size: 4rem;
            color: #d1d5db;
            margin-bottom: 20px;
        }
        
        .empty-state h3 {
            color: #6b7280;
            margin: 0;
        }
    </style>
</head>
<body>
    <div class="container">
        
        <!-- 헤더 -->
        <div class="header">
            <h1>📰 실시간 증권 뉴스</h1>
            <p>최신 주식 뉴스를 한눈에</p>
        </div>
        
        <!-- ✅ 필터 컨트롤 -->
        <div class="filter-controls">
            <div class="filter-tabs">
                <button class="filter-btn active" onclick="filterNews('all')">
                    🌐 전체
                </button>
                <button class="filter-btn" onclick="filterNews('KR')">
                    🇰🇷 한국
                </button>
                <button class="filter-btn" onclick="filterNews('US')">
                    🇺🇸 미국
                </button>
                <button class="filter-btn" onclick="filterNews('KOSPI')">
                    📊 KOSPI
                </button>
                <button class="filter-btn" onclick="filterNews('KOSDAQ')">
                    📈 KOSDAQ
                </button>
                <button class="filter-btn" onclick="filterNews('NASDAQ')">
                    🚀 NASDAQ
                </button>
                <button class="filter-btn" onclick="filterNews('NYSE')">
                    🏛️ NYSE
                </button>
            </div>
            <div class="news-count" id="newsCount">
                뉴스를 불러오는 중...
            </div>
        </div>
        
        <!-- 뉴스 그리드 -->
        <div class="news-grid" id="newsGrid">
            <div class="loading">뉴스를 불러오는 중입니다</div>
        </div>
        
    </div>

    <script>
        // ✅ 전역 변수
        let allNews = [];
        let currentFilter = 'all';
        
        // ✅ 뉴스 로드
        async function loadNews() {
            try {
                console.log('뉴스 로드 시작...');
                
                const response = await fetch('/portwatch/news/api/all');
                const data = await response.json();
                
                if (data.success && data.newsList) {
                    allNews = data.newsList;
                    console.log('뉴스 로드 완료:', allNews.length + '개');
                    displayNews(allNews);
                } else {
                    showEmptyState();
                }
                
            } catch (error) {
                console.error('뉴스 로드 실패:', error);
                showErrorState();
            }
        }
        
        // ✅ 뉴스 표시 (버퍼링 없음)
        function displayNews(newsList) {
            const grid = document.getElementById('newsGrid');
            const countElement = document.getElementById('newsCount');
            
            if (!newsList || newsList.length === 0) {
                showEmptyState();
                return;
            }
            
            grid.innerHTML = '';
            countElement.textContent = '총 ' + newsList.length + '개의 뉴스';
            
            newsList.forEach((news, index) => {
                const card = createNewsCard(news, index);
                grid.appendChild(card);
            });
        }
        
        // ✅ 뉴스 카드 생성 (버퍼링 없는 직접 링크)
        function createNewsCard(news, index) {
            const card = document.createElement('div');
            card.className = 'news-card';
            
            // ✅ 국가 판단
            const isKorean = !news.country || news.country === 'KR' || 
                            news.marketType === 'KOSPI' || news.marketType === 'KOSDAQ';
            const countryFlag = isKorean ? '🇰🇷' : '🇺🇸';
            
            // ✅ 직접 링크 (버퍼링 없음)
            card.innerHTML = `
                <a href="${news.link}" target="_blank" rel="noopener noreferrer">
                    <div class="news-header">
                        <span class="news-source">${news.source || '뉴스'}</span>
                        <span class="country-badge">${countryFlag}</span>
                    </div>
                    <h3 class="news-title">${news.title}</h3>
                    <div class="news-meta">
                        <span class="stock-badge">
                            ${news.stockCode || ''} ${news.stockName || ''}
                        </span>
                        <span>${news.publishedAt || '방금 전'}</span>
                    </div>
                </a>
            `;
            
            // ✅ 애니메이션 효과
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                card.style.transition = 'all 0.5s ease-out';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 50);
            
            return card;
        }
        
        // ✅ 필터링
        function filterNews(filter) {
            currentFilter = filter;
            
            // 버튼 상태 변경
            document.querySelectorAll('.filter-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            event.target.classList.add('active');
            
            // 뉴스 필터링
            let filtered = allNews;
            
            if (filter !== 'all') {
                filtered = allNews.filter(news => {
                    if (filter === 'KR') {
                        return !news.country || news.country === 'KR' || 
                               news.marketType === 'KOSPI' || news.marketType === 'KOSDAQ';
                    } else if (filter === 'US') {
                        return news.country === 'US' || 
                               news.marketType === 'NASDAQ' || news.marketType === 'NYSE';
                    } else {
                        return news.marketType === filter;
                    }
                });
            }
            
            console.log('필터링 결과:', filter, filtered.length + '개');
            displayNews(filtered);
        }
        
        // ✅ 빈 상태 표시
        function showEmptyState() {
            const grid = document.getElementById('newsGrid');
            grid.innerHTML = `
                <div class="empty-state" style="grid-column: 1/-1;">
                    <i class="bi bi-inbox"></i>
                    <h3>뉴스가 없습니다</h3>
                    <p style="color: #9ca3af; margin-top: 10px;">
                        아직 등록된 뉴스가 없습니다.
                    </p>
                </div>
            `;
            document.getElementById('newsCount').textContent = '0개의 뉴스';
        }
        
        // ✅ 에러 상태 표시
        function showErrorState() {
            const grid = document.getElementById('newsGrid');
            grid.innerHTML = `
                <div class="empty-state" style="grid-column: 1/-1;">
                    <i class="bi bi-exclamation-triangle"></i>
                    <h3>뉴스를 불러올 수 없습니다</h3>
                    <p style="color: #9ca3af; margin-top: 10px;">
                        잠시 후 다시 시도해주세요.
                    </p>
                    <button class="filter-btn" onclick="loadNews()" style="margin-top: 20px;">
                        다시 시도
                    </button>
                </div>
            `;
        }
        
        // ✅ 자동 새로고침 (5분마다)
        function autoRefresh() {
            loadNews();
        }
        
        setInterval(autoRefresh, 5 * 60 * 1000); // 5분
        
        // ✅ 페이지 로드 시 뉴스 로드
        document.addEventListener('DOMContentLoaded', () => {
            console.log('페이지 로드 완료');
            loadNews();
        });
        
        // ✅ 뉴스 수동 새로고침
        function refreshNews() {
            document.getElementById('newsGrid').innerHTML = '<div class="loading">뉴스를 불러오는 중입니다</div>';
            loadNews();
        }
    </script>
</body>
</html>
