<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - PortWatch</title>
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
        }
        
        /* 네비게이션 바 */
        nav {
            background: rgba(255, 255, 255, 0.95);
            padding: 1rem 2rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .nav-brand {
            font-size: 1.5rem;
            font-weight: bold;
            color: #667eea;
            text-decoration: none;
        }
        
        .nav-links {
            display: flex;
            gap: 2rem;
            list-style: none;
        }
        
        .nav-links a {
            text-decoration: none;
            color: #333;
            font-weight: 500;
            transition: color 0.3s;
        }
        
        .nav-links a:hover {
            color: #667eea;
        }
        
        .btn {
            padding: 0.5rem 1.5rem;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s;
        }
        
        .btn-secondary {
            background: #e0e0e0;
            color: #333;
        }
        
        .btn-secondary:hover {
            background: #d0d0d0;
        }
        
        /* 메인 컨테이너 */
        .container {
            max-width: 800px;
            margin: 3rem auto;
            padding: 0 2rem;
        }
        
        .profile-card {
            background: white;
            padding: 3rem;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        }
        
        .profile-header {
            text-align: center;
            margin-bottom: 2rem;
            padding-bottom: 2rem;
            border-bottom: 2px solid #f0f0f0;
        }
        
        .profile-icon {
            font-size: 5rem;
            margin-bottom: 1rem;
        }
        
        .profile-header h1 {
            color: #667eea;
            margin-bottom: 0.5rem;
        }
        
        .profile-status {
            display: inline-block;
            padding: 0.25rem 1rem;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 500;
        }
        
        .status-active {
            background: #e8f5e9;
            color: #2e7d32;
        }
        
        .profile-info {
            display: grid;
            gap: 1.5rem;
        }
        
        .info-row {
            display: grid;
            grid-template-columns: 150px 1fr;
            gap: 1rem;
            padding: 1rem 0;
            border-bottom: 1px solid #f0f0f0;
        }
        
        .info-row:last-child {
            border-bottom: none;
        }
        
        .info-label {
            color: #666;
            font-weight: 600;
        }
        
        .info-value {
            color: #333;
        }
        
        .profile-actions {
            margin-top: 2rem;
            display: flex;
            gap: 1rem;
            justify-content: center;
        }
        
        .btn-primary {
            background: #667eea;
            color: white;
        }
        
        .btn-primary:hover {
            background: #5568d3;
        }
        
        .btn-danger {
            background: #f44336;
            color: white;
        }
        
        .btn-danger:hover {
            background: #d32f2f;
        }
        
        /* 통계 카드 */
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-bottom: 2rem;
        }
        
        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            text-align: center;
        }
        
        .stat-icon {
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
        }
        
        .stat-value {
            font-size: 2rem;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 0.25rem;
        }
        
        .stat-label {
            color: #666;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav>
        <a href="${pageContext.request.contextPath}/" class="nav-brand">📈 PortWatch</a>
        
        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/">홈</a></li>
            <li><a href="${pageContext.request.contextPath}/portfolio/list">포트폴리오</a></li>
            <li><a href="${pageContext.request.contextPath}/stock/list">종목</a></li>
            <li><a href="${pageContext.request.contextPath}/member/mypage">마이페이지</a></li>
        </ul>
        
        <a href="${pageContext.request.contextPath}/member/logout" class="btn btn-secondary">로그아웃</a>
    </nav>
    
    <!-- 메인 컨텐츠 -->
    <div class="container">
        <!-- 통계 카드 -->
        <div class="stats">
            <div class="stat-card">
                <div class="stat-icon">📊</div>
                <div class="stat-value">0</div>
                <div class="stat-label">포트폴리오</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">⭐</div>
                <div class="stat-value">0</div>
                <div class="stat-label">관심종목</div>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon">📈</div>
                <div class="stat-value">0</div>
                <div class="stat-label">보유종목</div>
            </div>
        </div>
        
        <!-- 프로필 카드 -->
        <div class="profile-card">
            <div class="profile-header">
                <div class="profile-icon">👤</div>
                <h1>${member.memberName}</h1>
                <span class="profile-status status-active">
                    ${member.memberStatus == 'ACTIVE' ? '활성' : member.memberStatus}
                </span>
            </div>
            
            <div class="profile-info">
                <div class="info-row">
                    <div class="info-label">회원 ID</div>
                    <div class="info-value">${member.memberId}</div>
                </div>
                
                <div class="info-row">
                    <div class="info-label">이메일</div>
                    <div class="info-value">${member.memberEmail}</div>
                </div>
                
                <div class="info-row">
                    <div class="info-label">이름</div>
                    <div class="info-value">${member.memberName}</div>
                </div>
                
                <c:if test="${not empty member.memberPhone}">
                    <div class="info-row">
                        <div class="info-label">전화번호</div>
                        <div class="info-value">${member.memberPhone}</div>
                    </div>
                </c:if>
                
                <c:if test="${not empty member.memberGender}">
                    <div class="info-row">
                        <div class="info-label">성별</div>
                        <div class="info-value">
                            ${member.memberGender == 'M' ? '남성' : member.memberGender == 'F' ? '여성' : '기타'}
                        </div>
                    </div>
                </c:if>
                
                <c:if test="${not empty member.memberBirth}">
                    <div class="info-row">
                        <div class="info-label">생년월일</div>
                        <div class="info-value">
                            <fmt:formatDate value="${member.memberBirth}" pattern="yyyy-MM-dd" />
                        </div>
                    </div>
                </c:if>
                
                <div class="info-row">
                    <div class="info-label">가입일</div>
                    <div class="info-value">
                        <fmt:formatDate value="${member.memberRegDate}" pattern="yyyy-MM-dd HH:mm" />
                    </div>
                </div>
            </div>
            
            <div class="profile-actions">
                <a href="${pageContext.request.contextPath}/member/edit" class="btn btn-primary">
                    프로필 수정
                </a>
                <a href="${pageContext.request.contextPath}/member/changePassword" class="btn btn-secondary">
                    비밀번호 변경
                </a>
                <button onclick="if(confirm('정말 탈퇴하시겠습니까?')) location.href='${pageContext.request.contextPath}/member/delete'" class="btn btn-danger">
                    회원 탈퇴
                </button>
            </div>
        </div>
    </div>
</body>
</html>
