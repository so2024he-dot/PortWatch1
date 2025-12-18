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
        
        .header p {
            color: #6b7280;
            margin: 0;
        }
        
        .controls {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }
        
        .news-count {
            font-size: 1.1em;
            font-weight: 600;
            color: #1f2937;
        }
        
        .btn-refresh {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 10px 25px;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .btn-refresh:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-refresh:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        
        .news-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 25px;
        }
        
        .news-card {
            background: white;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: all 0.3s;
            cursor: pointer;
            opacity: 0;
            animation: fadeInUp 0.5s forwards;
        }
        
        .news-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }
        
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        
        .news-card:nth-child(1) { animation-delay: 0.05s; }
        .news-card:nth-child(2) { animation-delay: 0.1s; }
        .news-card:nth-child(3) { animation-delay: 0.15s; }
        .news-card:nth-child(4) { animation-delay: 0.2s; }
        .news-card:nth-child(5) { animation-delay: 0.25s; }
        .news-card:nth-child(6) { animation-delay: 0.3s; }
        
        .news-header {
            padding: 20px;
            border-bottom: 2px solid #f3f4f6;
        }
        
        .news-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .news-source {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
        }
        
        .news-time {
            color: #6b7280;
            font-size: 0.9em;
        }
        
        .news-title {
            font-size: 1.2em;
            font-weight: 700;
            color: #1f2937;
            line-height: 1.4;
            margin: 0;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .news-body {
            padding: 20px;
        }
        
        .news-summary {
            color: #4b5563;
            line-height: 1.6;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .news-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .news-stock {
            display: inline-block;
            background: #f3f4f6;
            color: #374151;
            padding: 5px 12px;
            border-radius: 8px;
            font-size: 0.9em;
            font-weight: 600;
        }
        
        .news-link {
            color: #667eea;
            font-weight: 600;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 5px;
        }
        
        .news-link:hover {
            color: #764ba2;
        }
        
        .loading {
            text-align: center;
            padding: 60px 20px;
            color: white;
        }
        
        .spinner {
            width: 50px;
            height: 50px;
            border: 4px solid rgba(255, 255, 255, 0.3);
            border-top-color: white;
            border-radius: 50%;
            animation: spin 0.8s linear infinite;
            margin: 0 auto 20px;
        }
        
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        
        .empty-state {
            background: white;
            border-radius: 15px;
            padding: 60px 20px;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }
        
        .empty-state i {
            font-size: 4em;
            color: #d1d5db;
            margin-bottom: 20px;
        }
        
        .empty-state h3 {
            color: #1f2937;
            margin-bottom: 10px;
        }
        
        .empty-state p {
            color: #6b7280;
        }
        
        @media (max-width: 768px) {
            .news-grid {
                grid-template-columns: 1fr;
            }
            
            .header h1 {
                font-size: 2em;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <h1>📰 실시간 증권 뉴스</h1>
            <p>네이버 금융의 최신 증권 뉴스를 실시간으로 확인하세요</p>
        </div>
        
        <!-- Controls -->
        <div class="controls">
            <div class="news-count">
                <i class="bi bi-newspaper me-2"></i>
                총 <span id="newsCount">${fn:length(newsList)}</span>개의 뉴스
            </div>
            <button class="btn-refresh" onclick="refreshNews()" id="refreshBtn">
                <i class="bi bi-arrow-clockwise me-2"></i>새로고침
            </button>
        </div>
        
        <!-- News Grid -->
        <div id="newsContainer">
            <c:choose>
                <c:when test="${empty newsList}">
                    <div class="empty-state">
                        <i class="bi bi-inbox"></i>
                        <h3>뉴스가 없습니다</h3>
                        <p>뉴스를 불러오는 중이거나 아직 수집된 뉴스가 없습니다.</p>
                        <button class="btn-refresh mt-3" onclick="refreshNews()">
                            <i class="bi bi-arrow-clockwise me-2"></i>새로고침
                        </button>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="news-grid">
                        <c:forEach items="${newsList}" var="news" varStatus="status">
                            <div class="news-card" onclick="openNews('${news.link}')">
                                <div class="news-header">
                                    <div class="news-meta">
                                        <span class="news-source">네이버증권</span>
                                        <span class="news-time">
                                            <fmt:formatDate value="${news.publishedDate}" pattern="MM-dd HH:mm" />
                                        </span>
                                    </div>
                                    <h3 class="news-title">${news.title}</h3>
                                </div>
                                <div class="news-body">
                                    <p class="news-summary">${news.summary}</p>
                                    <div class="news-footer">
                                        <c:if test="${not empty news.stockCode}">
                                            <span class="news-stock">
                                                <i class="bi bi-graph-up me-1"></i>${news.stockCode}
                                            </span>
                                        </c:if>
                                        <a href="${news.link}" class="news-link" 
                                           onclick="event.stopPropagation(); openNews('${news.link}')">
                                            전문보기 <i class="bi bi-arrow-right"></i>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Loading Indicator -->
        <div id="loadingIndicator" class="loading" style="display: none;">
            <div class="spinner"></div>
            <h4>뉴스를 불러오는 중...</h4>
        </div>
    </div>
    
    <script>
        // 페이지 로드 완료 후 자동 새로고침 시작
        let autoRefreshInterval = null;
        let isRefreshing = false;
        
        // 자동 새로고침 (60초마다)
        function startAutoRefresh() {
            console.log('자동 새로고침 시작 (60초 간격)');
            autoRefreshInterval = setInterval(() => {
                if (!isRefreshing) {
                    console.log('자동 새로고침 실행');
                    refreshNews(true);
                }
            }, 60000); // 60초
        }
        
        // 새로고침 함수
        function refreshNews(isAuto = false) {
            if (isRefreshing) {
                console.log('이미 새로고침 중...');
                return;
            }
            
            isRefreshing = true;
            const refreshBtn = document.getElementById('refreshBtn');
            const newsContainer = document.getElementById('newsContainer');
            const loadingIndicator = document.getElementById('loadingIndicator');
            
            // 버튼 비활성화
            refreshBtn.disabled = true;
            refreshBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-2"></i>새로고침 중...';
            
            // 로딩 표시 (자동 새로고침이 아닐 때만)
            if (!isAuto) {
                newsContainer.style.opacity = '0.5';
                loadingIndicator.style.display = 'block';
            }
            
            // AJAX로 새로고침 (버퍼링 없이)
            fetch('${pageContext.request.contextPath}/news/refresh', {
                method: 'POST'
            })
            .then(response => response.json())
            .then(result => {
                if (result.success) {
                    console.log('✅ 새로고침 완료:', result.count, '개 뉴스');
                    
                    // 페이지 리로드 없이 부드럽게 업데이트
                    setTimeout(() => {
                        location.reload();
                    }, 500);
                } else {
                    console.error('❌ 새로고침 실패:', result.message);
                    alert('새로고침 실패: ' + result.message);
                    
                    // 원상복구
                    newsContainer.style.opacity = '1';
                    loadingIndicator.style.display = 'none';
                    refreshBtn.disabled = false;
                    refreshBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-2"></i>새로고침';
                    isRefreshing = false;
                }
            })
            .catch(error => {
                console.error('❌ 네트워크 오류:', error);
                alert('네트워크 오류가 발생했습니다.');
                
                // 원상복구
                newsContainer.style.opacity = '1';
                loadingIndicator.style.display = 'none';
                refreshBtn.disabled = false;
                refreshBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-2"></i>새로고침';
                isRefreshing = false;
            });
        }
        
        // 뉴스 열기 (새 탭)
        function openNews(url) {
            window.open(url, '_blank');
        }
        
        // 페이지 로드 시 자동 새로고침 시작
        window.addEventListener('load', () => {
            console.log('페이지 로드 완료');
            startAutoRefresh();
        });
        
        // 페이지 벗어날 때 자동 새로고침 중지
        window.addEventListener('beforeunload', () => {
            if (autoRefreshInterval) {
                clearInterval(autoRefreshInterval);
                console.log('자동 새로고침 중지');
            }
        });
        
        // 스크롤 애니메이션
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -100px 0px'
        };
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        }, observerOptions);
        
        // 모든 뉴스 카드에 옵저버 적용
        document.querySelectorAll('.news-card').forEach(card => {
            observer.observe(card);
        });
    </script>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
