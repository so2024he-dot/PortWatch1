<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>증권 뉴스 - PortWatch</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        .news-card {
            transition: transform 0.2s, box-shadow 0.2s;
            cursor: pointer;
            height: 100%;
        }
        
        .news-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        
        .news-meta {
            font-size: 0.85em;
            color: #6c757d;
        }
        
        .news-category {
            display: inline-block;
            padding: 3px 10px;
            background: #e9ecef;
            border-radius: 15px;
            font-size: 0.75em;
            margin-right: 5px;
        }
        
        .news-source {
            color: #6c757d;
        }
        
        .refresh-info {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                <i class="fas fa-chart-line"></i> PortWatch
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-th-large"></i> 대시보드
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/stock/list">
                            <i class="fas fa-chart-bar"></i> 주식
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/watchlist/list">
                            <i class="fas fa-star"></i> 관심종목
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/news/list">
                            <i class="fas fa-newspaper"></i> 뉴스
                        </a>
                    </li>
                    <c:choose>
                        <c:when test="${not empty loginMember}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" 
                                   role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-user"></i> ${loginMember.memberName}
                                </a>
                                <ul class="dropdown-menu" aria-labelledby="userDropdown">
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/member/mypage">
                                            <i class="fas fa-user-circle"></i> 마이페이지
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" href="${pageContext.request.contextPath}/member/logout">
                                            <i class="fas fa-sign-out-alt"></i> 로그아웃
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/member/login">
                                    <i class="fas fa-sign-in-alt"></i> 로그인
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
    
    <!-- 메인 컨텐츠 -->
    <div class="container mt-4">
        <!-- 페이지 헤더 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>
                <i class="fas fa-newspaper"></i> 실시간 증권 뉴스
            </h2>
            <div>
                <!-- ✅ 수정: onclick 제거, id 추가 -->
                <button id="refreshNewsBtn" class="btn btn-primary">
                    <i class="fas fa-sync-alt"></i> 새로고침
                </button>
            </div>
        </div>
        
        <!-- 자동 새로고침 안내 -->
        <div class="refresh-info">
            <i class="fas fa-info-circle"></i>
            <strong>자동 새로고침:</strong> 5분마다 최신 뉴스가 자동으로 업데이트됩니다.
        </div>
        
        <!-- 뉴스 목록 -->
        <div id="newsContainer">
            <div class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">로딩중...</span>
                </div>
                <p class="mt-3">뉴스를 불러오는 중입니다...</p>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         ✅ 수정된 JavaScript - API 엔드포인트 수정
         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ -->
    <script>
    /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * NewsManager 객체 - 뉴스 관리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 핵심 수정:
     * ❌ 잘못된 API: /portwatch/api/news/all
     * ✅ 올바른 API: /api/news/recent?limit=50
     * 
     * 기능:
     * - 실시간 뉴스 로드
     * - 수동 새로고침 (크롤링)
     * - 자동 새로고침 (5분 간격)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
    
    const NewsManager = {
        // ✅ 올바른 방법: Controller에서 전달받은 값 직접 사용
        contextPath: '${pageContext.request.contextPath}',
        autoRefreshInterval: null,
        
        /**
         * 초기화
         */
        init: function() {
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            console.log('📰 뉴스 매니저 초기화');
            console.log('  - contextPath:', this.contextPath);
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            
            this.bindEvents();
            this.loadNews();
            this.startAutoRefresh();  // 5분마다 자동 새로고침
        },
        
        /**
         * 이벤트 리스너 바인딩
         */
        bindEvents: function() {
            console.log('🔗 이벤트 리스너 바인딩');
            
            // ✅ 새로고침 버튼
            const refreshBtn = document.getElementById('refreshNewsBtn');
            if (refreshBtn) {
                refreshBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    console.log('🔄 뉴스 새로고침 버튼 클릭!');
                    this.refreshNews();
                });
                console.log('✅ 새로고침 버튼 이벤트 등록 완료');
            } else {
                console.error('❌ 새로고침 버튼을 찾을 수 없습니다!');
            }
        },
        
        /**
         * 뉴스 로드
         */
        loadNews: function() {
            console.log('📰 뉴스 로드 시작');
            this.showLoading();
            
            // ✅ 올바른 API 엔드포인트
            const apiUrl = this.contextPath + '/api/news/recent?limit=50';
            console.log('🔗 API 호출:', apiUrl);
            
            fetch(apiUrl)
                .then(response => {
                    console.log('📡 서버 응답:', response.status);
                    if (!response.ok) {
                        throw new Error('뉴스 로드 실패: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('✅ 뉴스 로드 완료:', data);
                    
                    // 응답 데이터 파싱
                    const newsList = Array.isArray(data) ? data : (data.news || data.newsList || []);
                    
                    console.log('📋 뉴스 개수:', newsList.length);
                    
                    // 뉴스 렌더링
                    this.renderNews(newsList);
                    this.hideLoading();
                })
                .catch(error => {
                    console.error('❌ 뉴스 로드 실패:', error);
                    this.showError('뉴스를 불러오는데 실패했습니다: ' + error.message);
                });
        },
        
        /**
         * 뉴스 새로고침 (크롤링)
         */
        refreshNews: function() {
            console.log('🔄 뉴스 크롤링 시작');
            
            const refreshBtn = document.getElementById('refreshNewsBtn');
            const originalHtml = refreshBtn.innerHTML;
            
            // 버튼 비활성화
            refreshBtn.disabled = true;
            refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 크롤링 중...';
            
            fetch(this.contextPath + '/api/news/crawl', {
                method: 'POST'
            })
            .then(response => response.json())
            .then(data => {
                console.log('✅ 크롤링 완료:', data);
                
                // 버튼 복원
                refreshBtn.disabled = false;
                refreshBtn.innerHTML = originalHtml;
                
                // 뉴스 목록 새로고침
                this.loadNews();
                
                const count = data.count || data.newCount || 0;
                alert(count + '개의 새로운 뉴스를 불러왔습니다!');
            })
            .catch(error => {
                console.error('❌ 뉴스 새로고침 실패:', error);
                
                // 버튼 복원
                refreshBtn.disabled = false;
                refreshBtn.innerHTML = originalHtml;
                
                alert('뉴스 새로고침에 실패했습니다.');
            });
        },
        
        /**
         * 자동 새로고침 시작
         */
        startAutoRefresh: function() {
            console.log('⏰ 자동 새로고침 시작 (5분 간격)');
            
            // 기존 인터벌 제거
            if (this.autoRefreshInterval) {
                clearInterval(this.autoRefreshInterval);
            }
            
            // 5분마다 자동 새로고침
            this.autoRefreshInterval = setInterval(() => {
                const now = new Date();
                console.log('🔄 자동 새로고침 실행:', now.toLocaleTimeString());
                this.loadNews();
            }, 5 * 60 * 1000);  // 5분 = 300,000ms
            
            console.log('✅ 자동 새로고침 설정 완료');
        },
        
        /**
         * 뉴스 렌더링
         */
        renderNews: function(newsList) {
            console.log('🎨 뉴스 렌더링');
            
            const container = document.getElementById('newsContainer');
            
            if (!newsList || newsList.length === 0) {
                container.innerHTML = `
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle"></i>
                        뉴스가 없습니다. 새로고침 버튼을 눌러 뉴스를 불러오세요.
                    </div>
                `;
                return;
            }
            
            let html = '<div class="row">';
            
            newsList.forEach(news => {
                // 날짜 포맷팅
                let dateStr = '';
                if (news.publishedAt) {
                    dateStr = news.publishedAt;
                } else if (news.createdAt) {
                    dateStr = news.createdAt;
                }
                
                html += `
                    <div class="col-md-6 mb-4">
                        <div class="card news-card h-100" 
                             onclick="location.href='${pageContext.request.contextPath}/news/detail/${news.newsId}'">
                            <div class="card-body">
                                <h5 class="card-title">
                                    ${news.title}
                                </h5>
                                
                                ${news.category ? `
                                    <div class="mb-2">
                                        <span class="news-category">${news.category}</span>
                                    </div>
                                ` : ''}
                                
                                <p class="card-text text-muted">
                                    ${news.summary || news.content || ''}
                                </p>
                                
                                <div class="news-meta mt-3">
                                    <small>
                                        <i class="fas fa-calendar"></i>
                                        ${dateStr}
                                    </small>
                                    ${news.source ? `
                                        <small class="ms-3">
                                            <i class="fas fa-newspaper"></i>
                                            ${news.source}
                                        </small>
                                    ` : ''}
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            });
            
            html += '</div>';
            container.innerHTML = html;
            
            console.log('✅ 뉴스 렌더링 완료');
        },
        
        /**
         * 로딩 표시
         */
        showLoading: function() {
            const container = document.getElementById('newsContainer');
            container.innerHTML = `
                <div class="text-center py-5">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">로딩중...</span>
                    </div>
                    <p class="mt-3">뉴스를 불러오는 중입니다...</p>
                </div>
            `;
        },
        
        /**
         * 로딩 숨김
         */
        hideLoading: function() {
            // 렌더링으로 자동 제거됨
        },
        
        /**
         * 에러 표시
         */
        showError: function(message) {
            const container = document.getElementById('newsContainer');
            container.innerHTML = `
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-triangle"></i>
                    ${message}
                </div>
            `;
        }
    };
    
    // ✅ 페이지 로드 시 초기화
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📄 News List 페이지 로드 완료');
        NewsManager.init();
    });
    
    // 페이지 종료 시 인터벌 정리
    window.addEventListener('beforeunload', function() {
        if (NewsManager.autoRefreshInterval) {
            clearInterval(NewsManager.autoRefreshInterval);
        }
    });
    </script>
</body>
</html>
