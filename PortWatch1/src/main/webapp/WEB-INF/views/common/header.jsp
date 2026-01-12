<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     ✅ 완벽하게 개선된 Header - 모든 메뉴 연결
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ -->
<header class="main-header">
    <div class="header-container">
        <!-- 왼쪽: 로고 & 홈버튼 -->
        <div class="header-left">
            <a href="${pageContext.request.contextPath}/" class="logo">
                <i class="fas fa-chart-line"></i>
                <span>PortWatch</span>
            </a>
            
            <!-- ✅ 홈버튼 -->
            <a href="${pageContext.request.contextPath}/" class="home-btn" title="홈으로">
                <i class="fas fa-home"></i>
                <span>홈</span>
            </a>
        </div>

        <!-- 가운데: 네비게이션 메뉴 -->
        <nav class="main-nav">
            <!-- ✅ Dashboard -->
            <a href="${pageContext.request.contextPath}/dashboard" 
               class="${currentMenu == 'dashboard' ? 'active' : ''}"
               title="대시보드">
                <i class="fas fa-tachometer-alt"></i>
                <span>대시보드</span>
            </a>
            
            <!-- ✅ 종목 -->
            <a href="${pageContext.request.contextPath}/stock/list" 
               class="${currentMenu == 'stock' ? 'active' : ''}"
               title="종목 목록">
                <i class="fas fa-building"></i>
                <span>종목</span>
            </a>
            
            <!-- ✅ 관심종목 -->
            <a href="${pageContext.request.contextPath}/watchlist/list" 
               class="${currentMenu == 'watchlist' ? 'active' : ''}"
               title="관심종목">
                <i class="fas fa-star"></i>
                <span>관심종목</span>
            </a>
            
            <!-- ✅ 포트폴리오 -->
            <a href="${pageContext.request.contextPath}/portfolio/list" 
               class="${currentMenu == 'portfolio' ? 'active' : ''}"
               title="포트폴리오 관리">
                <i class="fas fa-briefcase"></i>
                <span>포트폴리오</span>
            </a>
            
            <!-- ✅ 뉴스 -->
            <a href="${pageContext.request.contextPath}/news/list" 
               class="${currentMenu == 'news' ? 'active' : ''}"
               title="뉴스">
                <i class="fas fa-newspaper"></i>
                <span>뉴스</span>
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

<!-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     ✅ Header 스타일
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ -->
<style>
:root {
    --primary-color: #667eea;
    --secondary-color: #764ba2;
    --hover-color: #f8f9ff;
    --shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    --shadow-hover: 0 4px 20px rgba(102, 126, 234, 0.3);
}

/* 헤더 기본 */
.main-header {
    background: white;
    box-shadow: var(--shadow);
    position: sticky;
    top: 0;
    z-index: 1000;
    border-bottom: 1px solid #e0e0e0;
}

.header-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 12px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

/* 왼쪽: 로고 & 홈버튼 */
.header-left {
    display: flex;
    align-items: center;
    gap: 15px;
}

.logo {
    display: flex;
    align-items: center;
    gap: 10px;
    text-decoration: none;
    color: #2c3e50;
    font-size: 22px;
    font-weight: 700;
    transition: all 0.3s ease;
}

.logo:hover {
    color: var(--primary-color);
    transform: scale(1.05);
}

.logo i {
    color: var(--primary-color);
    font-size: 26px;
}

/* 홈버튼 */
.home-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
    color: white !important;
    border-radius: 8px;
    text-decoration: none;
    font-weight: 600;
    font-size: 14px;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.home-btn:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-hover);
    background: linear-gradient(135deg, var(--secondary-color) 0%, var(--primary-color) 100%);
}

.home-btn i {
    font-size: 16px;
}

/* 네비게이션 메뉴 */
.main-nav {
    display: flex;
    gap: 3px;
    align-items: center;
}

.main-nav a {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 18px;
    color: #555;
    text-decoration: none;
    border-radius: 8px;
    transition: all 0.3s ease;
    font-weight: 500;
    font-size: 14px;
    position: relative;
}

.main-nav a:hover {
    background: var(--hover-color);
    color: var(--primary-color);
    transform: translateY(-1px);
}

.main-nav a.active {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
    color: white;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.main-nav a i {
    font-size: 16px;
}

/* 사용자 메뉴 */
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
    font-size: 14px;
}

.user-btn:hover {
    border-color: var(--primary-color);
    background: var(--hover-color);
    transform: translateY(-1px);
}

.user-btn i {
    font-size: 18px;
}

.user-dropdown {
    position: absolute;
    top: calc(100% + 10px);
    right: 0;
    background: white;
    border-radius: 10px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
    min-width: 200px;
    display: none;
    overflow: hidden;
    animation: slideDown 0.3s ease;
}

@keyframes slideDown {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
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
    font-size: 14px;
}

.user-dropdown a:hover {
    background: var(--hover-color);
    color: var(--primary-color);
}

.user-dropdown a.logout {
    color: #dc3545;
}

.user-dropdown a.logout:hover {
    background: #fff5f5;
    color: #c82333;
}

.dropdown-divider {
    height: 1px;
    background: #e0e0e0;
    margin: 5px 0;
}

/* 로그인 버튼 */
.login-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 20px;
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
    color: white !important;
    text-decoration: none;
    border-radius: 8px;
    font-weight: 600;
    font-size: 14px;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-hover);
}

.login-btn i {
    font-size: 16px;
}

/* 반응형 */
@media (max-width: 768px) {
    .header-container {
        padding: 10px 15px;
    }
    
    .main-nav a span,
    .home-btn span {
        display: none;
    }
    
    .logo span {
        display: none;
    }
    
    .main-nav {
        gap: 2px;
    }
    
    .main-nav a {
        padding: 8px 12px;
    }
}
</style>

<!-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     ✅ JavaScript
     ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ -->
<script>
// 사용자 메뉴 토글
function toggleUserMenu() {
    const dropdown = document.getElementById('userDropdown');
    dropdown.classList.toggle('show');
}

// 외부 클릭 시 드롭다운 닫기
document.addEventListener('click', function(event) {
    const userMenu = document.querySelector('.user-menu');
    const dropdown = document.getElementById('userDropdown');
    
    if (dropdown && userMenu && !userMenu.contains(event.target)) {
        dropdown.classList.remove('show');
    }
});

// 현재 페이지 활성화 표시
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.main-nav a');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.includes(href.split('/').pop())) {
            link.classList.add('active');
        }
    });
});
</script>
