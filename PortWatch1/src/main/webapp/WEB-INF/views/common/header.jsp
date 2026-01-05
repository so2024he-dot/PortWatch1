<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- ========================================
     ✅ 수정된 Header - 홈버튼 추가
     ======================================== -->
<header class="main-header">
    <div class="header-container">
        <!-- 왼쪽: 로고 & 홈버튼 -->
        <div class="header-left">
            <a href="${pageContext.request.contextPath}/" class="logo">
                <i class="fas fa-chart-line"></i>
                <span>PortWatch</span>
            </a>
            
            <!-- ✅ 새로 추가된 홈버튼 -->
            <a href="${pageContext.request.contextPath}/" class="home-btn" title="홈으로">
                <i class="fas fa-home"></i>
                <span>홈</span>
            </a>
        </div>

        <!-- 가운데: 네비게이션 메뉴 -->
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/stock/list" 
               class="${currentMenu == 'stock' ? 'active' : ''}">
                <i class="fas fa-building"></i>
                <span>종목</span>
            </a>
            <a href="${pageContext.request.contextPath}/watchlist/list" 
               class="${currentMenu == 'watchlist' ? 'active' : ''}">
                <i class="fas fa-star"></i>
                <span>관심종목</span>
            </a>
            <a href="${pageContext.request.contextPath}/portfolio/list" 
               class="${currentMenu == 'portfolio' ? 'active' : ''}">
                <i class="fas fa-briefcase"></i>
                <span>포트폴리오</span>
            </a>
            <a href="${pageContext.request.contextPath}/news/list" 
               class="${currentMenu == 'news' ? 'active' : ''}">
                <i class="fas fa-newspaper"></i>
                <span>뉴스</span>
            </a>
            <a href="${pageContext.request.contextPath}/dashboard" 
               class="${currentMenu == 'dashboard' ? 'active' : ''}">
                <i class="fas fa-tachometer-alt"></i>
                <span>대시보드</span>
            </a>
        </nav>

        <!-- 오른쪽: 사용자 메뉴 -->
        <div class="header-right">
            <c:choose>
                <c:when test="${not empty sessionScope.member}">
                    <!-- 로그인 상태 -->
                    <div class="user-menu">
                        <button class="user-btn" onclick="toggleUserMenu()">
                            <i class="fas fa-user-circle"></i>
                            <span>${sessionScope.member.name}</span>
                            <i class="fas fa-chevron-down"></i>
                        </button>
                        <div class="user-dropdown" id="userDropdown">
                            <a href="${pageContext.request.contextPath}/member/profile">
                                <i class="fas fa-user"></i>
                                내 정보
                            </a>
                            <a href="${pageContext.request.contextPath}/member/settings">
                                <i class="fas fa-cog"></i>
                                설정
                            </a>
                            <div class="dropdown-divider"></div>
                            <a href="${pageContext.request.contextPath}/member/logout" class="logout">
                                <i class="fas fa-sign-out-alt"></i>
                                로그아웃
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- 로그아웃 상태 -->
                    <a href="${pageContext.request.contextPath}/member/login" class="login-btn">
                        <i class="fas fa-sign-in-alt"></i>
                        <span>로그인</span>
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<!-- ========================================
     ✅ 홈버튼 스타일 추가
     ======================================== -->
<style>
/* 홈버튼 스타일 */
.home-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    margin-left: 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white !important;
    border-radius: 8px;
    text-decoration: none;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.home-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.5);
    background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.home-btn i {
    font-size: 18px;
}

.home-btn span {
    font-size: 14px;
}

/* 기존 헤더 스타일 */
.main-header {
    background: white;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    position: sticky;
    top: 0;
    z-index: 1000;
}

.header-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 15px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-left {
    display: flex;
    align-items: center;
}

.logo {
    display: flex;
    align-items: center;
    gap: 10px;
    text-decoration: none;
    color: #2c3e50;
    font-size: 24px;
    font-weight: 700;
}

.logo i {
    color: #667eea;
    font-size: 28px;
}

.main-nav {
    display: flex;
    gap: 5px;
}

.main-nav a {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    color: #555;
    text-decoration: none;
    border-radius: 8px;
    transition: all 0.3s ease;
    font-weight: 500;
}

.main-nav a:hover {
    background: #f0f0f0;
    color: #667eea;
}

.main-nav a.active {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
}

.user-menu {
    position: relative;
}

.user-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 16px;
    background: white;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 500;
}

.user-btn:hover {
    border-color: #667eea;
    background: #f8f9ff;
}

.user-dropdown {
    position: absolute;
    top: 100%;
    right: 0;
    margin-top: 10px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    min-width: 200px;
    display: none;
}

.user-dropdown.show {
    display: block;
}

.user-dropdown a {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 20px;
    color: #333;
    text-decoration: none;
    transition: all 0.2s ease;
}

.user-dropdown a:hover {
    background: #f8f9ff;
    color: #667eea;
}

.dropdown-divider {
    height: 1px;
    background: #e0e0e0;
    margin: 5px 0;
}

.login-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 20px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white !important;
    text-decoration: none;
    border-radius: 8px;
    font-weight: 600;
    transition: all 0.3s ease;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>

<!-- ========================================
     JavaScript
     ======================================== -->
<script>
function toggleUserMenu() {
    const dropdown = document.getElementById('userDropdown');
    dropdown.classList.toggle('show');
}

// 외부 클릭 시 드롭다운 닫기
document.addEventListener('click', function(event) {
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.getElementById('userDropdown');
    
    if (dropdown && !userMenu.contains(event.target)) {
        dropdown.classList.remove('show');
    }
});
</script>
