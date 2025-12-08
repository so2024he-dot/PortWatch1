<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../common/header.jsp" />

<style>
    .news-container {
        max-width: 1200px;
        margin: 2rem auto;
        padding: 0 1rem;
    }
    
    .news-header {
        margin-bottom: 2rem;
        text-align: center;
    }
    
    .news-title {
        font-size: 2.5rem;
        font-weight: 700;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 0.5rem;
    }
    
    .news-subtitle {
        color: #6b7280;
        font-size: 1.1rem;
    }
    
    .news-controls {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
        padding: 1rem;
        background: #f9fafb;
        border-radius: 10px;
    }
    
    .news-count {
        font-weight: 600;
        color: var(--primary-color);
    }
    
    .btn-refresh {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        color: white;
        padding: 0.5rem 1.5rem;
        border-radius: 8px;
        font-weight: 600;
        transition: all 0.3s;
    }
    
    .btn-refresh:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
    }
    
    .news-list {
        display: grid;
        gap: 1.5rem;
    }
    
    .news-card {
        background: white;
        border-radius: 15px;
        padding: 1.5rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        transition: all 0.3s;
        border-left: 4px solid transparent;
    }
    
    .news-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        border-left-color: var(--primary-color);
    }
    
    .news-card-title {
        font-size: 1.2rem;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 0.5rem;
        line-height: 1.4;
    }
    
    .news-card-title a {
        color: #1f2937;
        text-decoration: none;
        transition: color 0.3s;
    }
    
    .news-card-title a:hover {
        color: var(--primary-color);
    }
    
    .news-card-content {
        color: #6b7280;
        font-size: 0.95rem;
        line-height: 1.6;
        margin-bottom: 1rem;
    }
    
    .news-card-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 0.85rem;
        color: #9ca3af;
    }
    
    .news-source {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .news-source-icon {
        width: 20px;
        height: 20px;
        background: var(--primary-color);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 0.7rem;
    }
    
    .loading {
        text-align: center;
        padding: 3rem;
        color: #6b7280;
    }
    
    .spinner {
        border: 3px solid #f3f4f6;
        border-top: 3px solid var(--primary-color);
        border-radius: 50%;
        width: 40px;
        height: 40px;
        animation: spin 1s linear infinite;
        margin: 0 auto 1rem;
    }
    
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
    
    @media (max-width: 768px) {
        .news-title {
            font-size: 2rem;
        }
        
        .news-controls {
            flex-direction: column;
            gap: 1rem;
        }
    }
</style>

<div class="news-container">
    <div class="news-header animate-fade-in">
        <h1 class="news-title">
            <i class="bi bi-newspaper">실시간 증권 뉴스</i> 
        </h1>
        <p class="news-subtitle">네이버 금융의 최신 증권 뉴스를 실시간으로 확인하세요</p>
    </div>
    
    <div class="news-controls">
        <div class="news-count">
            <i class="bi bi-journal-text me-2"></i>
            총 <span id="newsCount">${newsCount}</span>개의 뉴스
        </div>
        <button class="btn btn-refresh" onclick="refreshNews()">
            <i class="bi bi-arrow-clockwise me-2"></i>새로고침
        </button>
    </div>
    
    <div id="newsListContainer" class="news-list">
        <c:choose>
            <c:when test="${not empty newsList}">
                <c:forEach items="${newsList}" var="news">
                    <div class="news-card animate-fade-in">
                        <h3 class="news-card-title">
                            <a href="${news.newsUrl}" target="_blank" rel="noopener noreferrer">
                                ${news.newsTitle}
                            </a>
                        </h3>
                        <p class="news-card-content">${news.newsContent}</p>
                        <div class="news-card-meta">
                            <div class="news-source">
                                <div class="news-source-icon">
                                    <i class="bi bi-building"></i>
                                </div>
                                <span>${news.newsSource}</span>
                            </div>
                            <div class="news-date">
                                <i class="bi bi-clock me-1"></i>
                                <fmt:formatDate value="${news.newsPubDate}" pattern="yyyy-MM-dd HH:mm"/>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="loading">
                    <div class="spinner"></div>
                    <p>뉴스를 불러오는 중입니다...</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
function refreshNews() {
    const container = document.getElementById('newsListContainer');
    container.innerHTML = '<div class="loading"><div class="spinner"></div><p>뉴스를 불러오는 중입니다...</p></div>';
    
    $.ajax({
        url: '${pageContext.request.contextPath}/api/news/latest',
        type: 'GET',
        data: { limit: 20 },
        success: function(response) {
            if (response.success) {
                displayNews(response.newsList);
                $('#newsCount').text(response.count);
            } else {
                container.innerHTML = '<div class="alert alert-danger">뉴스를 불러오는 중 오류가 발생했습니다.</div>';
            }
        },
        error: function() {
            container.innerHTML = '<div class="alert alert-danger">서버 연결에 실패했습니다.</div>';
        }
    });
}

function displayNews(newsList) {
    const container = document.getElementById('newsListContainer');
    let html = '';
    
    newsList.forEach(news => {
        html += `
            <div class="news-card animate-fade-in">
                <h3 class="news-card-title">
                    <a href="${news.newsUrl}" target="_blank" rel="noopener noreferrer">
                        ${news.newsTitle}
                    </a>
                </h3>
                <p class="news-card-content">${news.newsContent}</p>
                <div class="news-card-meta">
                    <div class="news-source">
                        <div class="news-source-icon">
                            <i class="bi bi-building"></i>
                        </div>
                        <span>${news.newsSource}</span>
                    </div>
                    <div class="news-date">
                        <i class="bi bi-clock me-1"></i>
                        방금 전
                    </div>
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
}
</script>

<jsp:include page="../common/footer.jsp" />

    
