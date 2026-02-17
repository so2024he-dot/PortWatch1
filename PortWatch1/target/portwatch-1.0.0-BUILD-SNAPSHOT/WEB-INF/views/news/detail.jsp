<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${news.title} - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        body {
            background-color: #f8f9fa;
            padding-top: 20px;
        }
        
        .news-detail-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .back-btn {
            background: #667eea;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            margin-bottom: 20px;
            transition: all 0.3s;
        }
        
        .back-btn:hover {
            background: #5568d3;
            transform: translateY(-2px);
        }
        
        .news-detail-card {
            background: white;
            border-radius: 10px;
            padding: 40px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .news-header {
            border-bottom: 2px solid #f0f0f0;
            padding-bottom: 20px;
            margin-bottom: 30px;
        }
        
        .news-title-detail {
            font-size: 2rem;
            font-weight: bold;
            color: #333;
            line-height: 1.5;
            margin-bottom: 20px;
        }
        
        .news-meta-detail {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            color: #666;
            font-size: 1rem;
        }
        
        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .meta-item i {
            color: #667eea;
        }
        
        .stock-badge-detail {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 1rem;
        }
        
        .news-content {
            font-size: 1.1rem;
            line-height: 1.8;
            color: #333;
            margin-top: 30px;
        }
        
        .news-image {
            width: 100%;
            max-width: 700px;
            height: auto;
            border-radius: 10px;
            margin: 20px 0;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        
        .original-link {
            margin-top: 30px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 10px;
            text-align: center;
        }
        
        .original-link a {
            color: #667eea;
            font-weight: bold;
            text-decoration: none;
            font-size: 1.1rem;
        }
        
        .original-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    
    <div class="news-detail-container">
        
        <!-- 뒤로 가기 버튼 -->
        <button class="back-btn" onclick="history.back()">
            <i class="fas fa-arrow-left"></i> 목록으로
        </button>
        
        <!-- 뉴스 상세 -->
        <div class="news-detail-card">
            
            <!-- 뉴스 헤더 -->
            <div class="news-header">
                
                <!-- 뉴스 제목 -->
                <h1 class="news-title-detail">
                    ${news.title}
                </h1>
                
                <!-- 메타 정보 -->
                <div class="news-meta-detail">
                    <div class="meta-item">
                        <span class="stock-badge-detail">
                            <i class="fas fa-chart-line"></i> ${news.stockName} (${news.stockCode})
                        </span>
                    </div>
                    <div class="meta-item">
                        <i class="far fa-clock"></i>
                        <span>
                            <fmt:formatDate value="${news.publishedDate}" pattern="yyyy년 MM월 dd일 HH:mm"/>
                        </span>
                    </div>
                    <div class="meta-item">
                        <i class="fas fa-tag"></i>
                        <span>${news.source}</span>
                    </div>
                </div>
                
            </div>
            
            <!-- 뉴스 이미지 (있는 경우) -->
            <c:if test="${not empty news.imageUrl}">
                <img src="${news.imageUrl}" alt="뉴스 이미지" class="news-image">
            </c:if>
            
            <!-- 뉴스 본문 -->
            <div class="news-content">
                <c:choose>
                    <c:when test="${not empty news.content}">
                        ${news.content}
                    </c:when>
                    <c:otherwise>
                        <p style="color: #999; text-align: center; padding: 40px 0;">
                            뉴스 본문을 불러올 수 없습니다.<br>
                            원문 링크를 통해 전체 내용을 확인하세요.
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <!-- 원문 링크 -->
            <div class="original-link">
                <i class="fas fa-external-link-alt"></i>
                <a href="${news.link}" target="_blank" rel="noopener noreferrer">
                    원문 보기 →
                </a>
            </div>
            
        </div>
        
    </div>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
</body>
</html>
