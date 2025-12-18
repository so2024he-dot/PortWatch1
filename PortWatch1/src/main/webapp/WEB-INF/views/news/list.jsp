<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>뉴스 - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        body {
            background-color: #f8f9fa;
            padding-top: 20px;
        }
        
        .news-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 10px;
            margin-bottom: 30px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        
        .page-header h1 {
            margin: 0;
            font-size: 2rem;
            font-weight: bold;
        }
        
        .page-header p {
            margin: 10px 0 0 0;
            opacity: 0.9;
        }
        
        .refresh-btn {
            background: white;
            color: #667eea;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            font-weight: bold;
            transition: all 0.3s;
        }
        
        .refresh-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }
        
        .news-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: all 0.3s;
            cursor: pointer;
        }
        
        .news-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        }
        
        .news-title {
            font-size: 1.2rem;
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
            line-height: 1.5;
        }
        
        .news-meta {
            display: flex;
            gap: 15px;
            color: #666;
            font-size: 0.9rem;
            margin-bottom: 10px;
        }
        
        .news-meta i {
            margin-right: 5px;
        }
        
        .stock-badge {
            display: inline-block;
            background: #667eea;
            color: white;
            padding: 5px 12px;
            border-radius: 15px;
            font-size: 0.85rem;
            font-weight: bold;
        }
        
        .news-summary {
            color: #666;
            line-height: 1.6;
            margin-top: 10px;
        }
        
        .no-news {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .no-news i {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        
        .loading-spinner {
            display: none;
            text-align: center;
            padding: 20px;
        }
        
        .loading-spinner.active {
            display: block;
        }
        
        .spinner-border {
            width: 3rem;
            height: 3rem;
        }
    </style>
</head>
<body>
    
    <div class="news-container">
        
        <!-- 페이지 헤더 -->
        <div class="page-header">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h1><i class="fas fa-newspaper"></i> 주식 뉴스</h1>
                    <p>최신 증시 뉴스를 확인하세요</p>
                </div>
                <button class="refresh-btn" onclick="refreshNews()">
                    <i class="fas fa-sync-alt"></i> 새로고침
                </button>
            </div>
        </div>
        
        <!-- 로딩 스피너 -->
        <div class="loading-spinner" id="loadingSpinner">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-3">뉴스를 불러오는 중...</p>
        </div>
        
        <!-- 뉴스 목록 -->
        <div id="newsList">
            <c:choose>
                <c:when test="${not empty newsList}">
                    <c:forEach var="news" items="${newsList}">
                        <div class="news-card" onclick="location.href='${pageContext.request.contextPath}/news/detail/${news.newsId}'">
                            
                            <!-- 뉴스 제목 -->
                            <div class="news-title">
                                ${news.title}
                            </div>
                            
                            <!-- 뉴스 메타 정보 -->
                            <div class="news-meta">
                                <span class="stock-badge">
                                    <i class="fas fa-chart-line"></i> ${news.stockName} (${news.stockCode})
                                </span>
                                <span>
                                    <i class="far fa-clock"></i> 
                                    <fmt:formatDate value="${news.publishedDate}" pattern="yyyy-MM-dd HH:mm"/>
                                </span>
                                <span>
                                    <i class="fas fa-tag"></i> ${news.source}
                                </span>
                            </div>
                            
                            <!-- 뉴스 요약 -->
                            <c:if test="${not empty news.summary}">
                                <div class="news-summary">
                                    ${news.summary}
                                </div>
                            </c:if>
                            
                        </div>
                    </c:forEach>
                </c:when>
                
                <c:otherwise>
                    <div class="no-news">
                        <i class="fas fa-newspaper"></i>
                        <h3>등록된 뉴스가 없습니다</h3>
                        <p>새로고침 버튼을 눌러 최신 뉴스를 가져오세요</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <script>
        // 뉴스 새로고침
        function refreshNews() {
            
            // 로딩 표시
            $('#loadingSpinner').addClass('active');
            
            $.ajax({
                url: '${pageContext.request.contextPath}/news/refresh',
                type: 'POST',
                dataType: 'json',
                success: function(response) {
                    if (response.success) {
                        alert(response.count + '개의 새로운 뉴스를 가져왔습니다.');
                        location.reload();
                    } else {
                        alert('뉴스 새로고침 실패: ' + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    console.error('AJAX Error:', error);
                    alert('뉴스 새로고침 중 오류가 발생했습니다.');
                },
                complete: function() {
                    // 로딩 숨김
                    $('#loadingSpinner').removeClass('active');
                }
            });
        }
        
        // 자동 새로고침 (5분마다)
        /*
        setInterval(function() {
            refreshNews();
        }, 5 * 60 * 1000);
        */
    </script>
    
</body>
</html>
