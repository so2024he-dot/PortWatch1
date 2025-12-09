<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="common/header.jsp" />

<style>
    .hero-section {
        background: white;
        border-radius: 20px;
        padding: 4rem 2rem;
        text-align: center;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
        margin-bottom: 3rem;
    }
    
    .hero-icon {
        font-size: 5rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 1rem;
    }
    
    .hero-title {
        font-size: 3rem;
        font-weight: 800;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 1rem;
    }
    
    .hero-subtitle {
        font-size: 1.5rem;
        color: #6b7280;
        margin-bottom: 2rem;
    }
    
    .hero-cta {
        display: inline-block;
        padding: 1rem 3rem;
        font-size: 1.2rem;
        font-weight: 600;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 12px;
        text-decoration: none;
        transition: all 0.3s;
        box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
    }
    
    .hero-cta:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
        color: white;
    }
    
    /* ⭐ Feature Card 링크 스타일 */
    .feature-link {
        text-decoration: none;
        color: inherit;
        display: block;
        height: 100%;
    }
    
    .feature-card {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        text-align: center;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
        transition: all 0.3s;
        height: 100%;
        border: 1px solid rgba(102, 126, 234, 0.1);
        cursor: pointer;  /* ⭐ 마우스 커서 변경 */
    }
    
    .feature-card:hover {
        transform: translateY(-10px) scale(1.02);  /* ⭐ 확대 효과 추가 */
        box-shadow: 0 15px 40px rgba(102, 126, 234, 0.3);  /* ⭐ 그림자 강화 */
        border-color: rgba(102, 126, 234, 0.5);  /* ⭐ 테두리 색상 강화 */
    }
    
    .feature-icon {
        font-size: 3.5rem;
        margin-bottom: 1rem;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        transition: all 0.3s;  /* ⭐ 아이콘 애니메이션 */
    }
    
    .feature-card:hover .feature-icon {
        transform: scale(1.1);  /* ⭐ hover 시 아이콘 확대 */
    }
    
    .feature-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 1rem;
    }
    
    .feature-desc {
        color: #6b7280;
        font-size: 1rem;
        line-height: 1.6;
    }
    
    .stats-section {
        background: white;
        border-radius: 20px;
        padding: 3rem 2rem;
        margin-top: 3rem;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    }
    
    .stat-item {
        text-align: center;
        padding: 1.5rem;
    }
    
    .stat-number {
        font-size: 3rem;
        font-weight: 800;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 0.5rem;
    }
    
    .stat-label {
        font-size: 1.1rem;
        color: #6b7280;
        font-weight: 500;
    }
    
    @media (max-width: 768px) {
        .hero-title {
            font-size: 2rem;
        }
        
        .hero-subtitle {
            font-size: 1.1rem;
        }
        
        .hero-cta {
            padding: 0.75rem 2rem;
            font-size: 1rem;
        }
        
        .feature-icon {
            font-size: 2.5rem;
        }
        
        .feature-title {
            font-size: 1.25rem;
        }
        
        .stat-number {
            font-size: 2rem;
        }
    }
</style>

<!-- Alert Messages -->
<c:if test="${not empty message}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        <i class="bi bi-check-circle me-2"></i>${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        <i class="bi bi-exclamation-circle me-2"></i>${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
</c:if>

<!-- Hero Section -->
<div class="hero-section animate-fade-in">
    <div class="hero-icon">
        <i class="bi bi-graph-up-arrow"></i>
    </div>
    <h1 class="hero-title">PortWatch</h1>
    <p class="hero-subtitle">
        당신의 주식 포트폴리오를 스마트하게 관리하세요
    </p>
    
    <c:choose>
        <c:when test="${not empty sessionScope.member}">
            <a href="${pageContext.request.contextPath}/portfolio/list" class="hero-cta">
                <i class="bi bi-briefcase me-2"></i>포트폴리오 시작하기
            </a>
        </c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}/member/signup" class="hero-cta">
                <i class="bi bi-rocket-takeoff me-2"></i>무료로 시작하기
            </a>
        </c:otherwise>
    </c:choose>
</div>

<!-- Features (⭐ 클릭 가능하도록 수정) -->
<div class="row g-4 mb-4">
    <!-- 포트폴리오 관리 -->
    <div class="col-lg-3 col-md-6">
        <a href="${pageContext.request.contextPath}/portfolio/list" class="feature-link">
            <div class="feature-card animate-fade-in" style="animation-delay: 0.1s;">
                <div class="feature-icon">
                    <i class="bi bi-briefcase"></i>
                </div>
                <h3 class="feature-title">포트폴리오 관리</h3>
                <p class="feature-desc">
                    여러 개의 포트폴리오를 만들고 실시간으로 수익률을 확인하세요
                </p>
            </div>
        </a>
    </div>
    
    <!-- 종목 분석 -->
    <div class="col-lg-3 col-md-6">
        <a href="${pageContext.request.contextPath}/stock/list" class="feature-link">
            <div class="feature-card animate-fade-in" style="animation-delay: 0.2s;">
                <div class="feature-icon">
                    <i class="bi bi-graph-up"></i>
                </div>
                <h3 class="feature-title">종목 분석</h3>
                <p class="feature-desc">
                    KOSPI, KOSDAQ 종목 정보와 가격 변동을 한눈에 확인하세요
                </p>
            </div>
        </a>
    </div>
    
    <!-- 관심종목 -->
    <div class="col-lg-3 col-md-6">
        <a href="${pageContext.request.contextPath}/watchlist/list" class="feature-link">
            <div class="feature-card animate-fade-in" style="animation-delay: 0.3s;">
                <div class="feature-icon">
                    <i class="bi bi-star-fill"></i>
                </div>
                <h3 class="feature-title">관심종목</h3>
                <p class="feature-desc">
                    관심있는 종목을 저장하고 빠르게 접근하세요
                </p>
            </div>
        </a>
    </div>
    
    <!-- 실시간 뉴스 -->
    <div class="col-lg-3 col-md-6">
        <a href="${pageContext.request.contextPath}/news/list" class="feature-link">
            <div class="feature-card animate-fade-in" style="animation-delay: 0.4s;">
                <div class="feature-icon">
                    <i class="bi bi-newspaper"></i>
                </div>
                <h3 class="feature-title">실시간 뉴스</h3>
                <p class="feature-desc">
                    최신 주식 시장 뉴스를 실시간으로 받아보세요
                </p>
            </div>
        </a>
    </div>
</div>

<!-- Stats Section -->
<div class="stats-section animate-fade-in" style="animation-delay: 0.5s;">
    <div class="row">
        <div class="col-lg-3 col-6">
            <div class="stat-item">
                <div class="stat-number">25+</div>
                <div class="stat-label">등록 종목</div>
            </div>
        </div>
        <div class="col-lg-3 col-6">
            <div class="stat-item">
                <div class="stat-number">1000+</div>
                <div class="stat-label">회원수</div>
            </div>
        </div>
        <div class="col-lg-3 col-6">
            <div class="stat-item">
                <div class="stat-number">24/7</div>
                <div class="stat-label">실시간 모니터링</div>
            </div>
        </div>
        <div class="col-lg-3 col-6">
            <div class="stat-item">
                <div class="stat-number">100%</div>
                <div class="stat-label">무료</div>
            </div>
        </div>
    </div>
</div>

<script>
    // Auto-hide alerts
    setTimeout(function() {
        $('.alert').fadeOut('slow');
    }, 3000);
</script>

<jsp:include page="common/footer.jsp" />

    
