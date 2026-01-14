<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - Dashboard</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        
        .dashboard-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            max-width: 1200px;
            width: 100%;
            padding: 40px;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .header h1 {
            color: #667eea;
            font-size: 32px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .user-avatar {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }
        
        .user-name {
            font-size: 18px;
            font-weight: 600;
            color: #333;
        }
        
        .welcome-message {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .welcome-message h2 {
            font-size: 28px;
            margin-bottom: 10px;
        }
        
        .welcome-message p {
            font-size: 16px;
            opacity: 0.9;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 15px;
            border-left: 4px solid #667eea;
            transition: transform 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .stat-card h3 {
            color: #666;
            font-size: 14px;
            margin-bottom: 10px;
            text-transform: uppercase;
        }
        
        .stat-card .value {
            color: #333;
            font-size: 28px;
            font-weight: bold;
        }
        
        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-top: 30px;
        }
        
        .action-btn {
            padding: 15px 25px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            text-decoration: none;
            display: block;
        }
        
        .action-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .logout-btn {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        .portfolio-section {
            margin-top: 30px;
            padding: 25px;
            background: #f8f9fa;
            border-radius: 15px;
        }
        
        .portfolio-section h2 {
            color: #333;
            margin-bottom: 20px;
            font-size: 24px;
        }
        
        .empty-portfolio {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        
        .empty-portfolio i {
            font-size: 48px;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <!-- Header -->
        <div class="header">
            <h1>📊 PortWatch</h1>
            <div class="user-info">
                <div class="user-avatar">
                    ${member.memberName.substring(0, 1)}
                </div>
                <div>
                    <div class="user-name">${member.memberName}</div>
                    <small style="color: #999;">${member.memberEmail}</small>
                </div>
            </div>
        </div>
        
        <!-- Welcome Message -->
        <div class="welcome-message">
            <h2>환영합니다, ${member.memberName}님! 🎉</h2>
            <p>PortWatch에서 포트폴리오를 관리하세요</p>
        </div>
        
        <!-- Stats Grid -->
        <div class="stats-grid">
            <div class="stat-card">
                <h3>총 포트폴리오</h3>
                <div class="value">
                    <c:choose>
                        <c:when test="${portfolioList != null}">
                            ${portfolioList.size()}개
                        </c:when>
                        <c:otherwise>
                            0개
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <div class="stat-card">
                <h3>회원 상태</h3>
                <div class="value" style="font-size: 20px;">
                    <c:choose>
                        <c:when test="${member.memberStatus == 'ACTIVE'}">
                            ✅ 활성
                        </c:when>
                        <c:otherwise>
                            ⚠️ ${member.memberStatus}
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <div class="stat-card">
                <h3>회원 권한</h3>
                <div class="value" style="font-size: 20px;">
                    <c:choose>
                        <c:when test="${member.memberRole == 'ADMIN'}">
                            👑 관리자
                        </c:when>
                        <c:otherwise>
                            👤 일반 회원
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <div class="stat-card">
                <h3>가입일</h3>
                <div class="value" style="font-size: 16px;">
                    ${member.createdAt != null ? member.createdAt : 'N/A'}
                </div>
            </div>
        </div>
        
        <!-- Quick Actions -->
        <div class="quick-actions">
            <a href="${pageContext.request.contextPath}/portfolio/list" class="action-btn">
                📈 포트폴리오 보기
            </a>
            <a href="${pageContext.request.contextPath}/portfolio/create" class="action-btn">
                ➕ 포트폴리오 추가
            </a>
            <a href="${pageContext.request.contextPath}/news/list" class="action-btn">
                📰 뉴스 보기
            </a>
            <a href="${pageContext.request.contextPath}/stock/list" class="action-btn">
                📊 종목 검색
            </a>
            <a href="${pageContext.request.contextPath}/member/profile" class="action-btn">
                ⚙️ 프로필 설정
            </a>
            <a href="${pageContext.request.contextPath}/member/logout" class="action-btn logout-btn">
                🚪 로그아웃
            </a>
        </div>
        
        <!-- Portfolio Section -->
        <div class="portfolio-section">
            <h2>내 포트폴리오</h2>
            <c:choose>
                <c:when test="${portfolioList != null && portfolioList.size() > 0}">
                    <div style="color: #333;">
                        <p>✅ ${portfolioList.size()}개의 포트폴리오가 있습니다.</p>
                        <a href="${pageContext.request.contextPath}/portfolio/list" 
                           style="color: #667eea; text-decoration: none; font-weight: 600; margin-top: 10px; display: inline-block;">
                            자세히 보기 →
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-portfolio">
                        <div style="font-size: 48px; margin-bottom: 15px;">📊</div>
                        <p style="font-size: 18px; margin-bottom: 20px;">아직 포트폴리오가 없습니다</p>
                        <a href="${pageContext.request.contextPath}/portfolio/create" 
                           class="action-btn" 
                           style="display: inline-block; width: auto;">
                            첫 포트폴리오 만들기
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
