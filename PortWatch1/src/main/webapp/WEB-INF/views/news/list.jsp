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
         ✅ 수정된 JavaScript - JSP EL 표현식 오류 해결
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
        contextPath: '${pageContext.request.contextPath}',
        autoRefreshInterval: null,
        autoRefreshTime: 5 * 60 * 1000, // 5분
        
        /**
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 초기화
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
        init: function() {
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            console.log('📰 NewsManager 초기화');
            console.log('  - Context Path: ' + this.contextPath);
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            
            this.loadNews();
            this.setupEventHandlers();
            this.startAutoRefresh();
        },
        
        /**
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 이벤트 핸들러 설정
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
        setupEventHandlers: function() {
            const refreshBtn = document.getElementById('refreshNewsBtn');
            if (refreshBtn) {
                refreshBtn.addEventListener('click', () => {
                    console.log('🔄 수동 새로고침 버튼 클릭');
                    this.refreshNews();
                });
            }
        },
        
        /**
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 뉴스 로드 (DB에서 최신 뉴스 가져오기)
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
        loadNews: function() {
            console.log('📥 뉴스 로드 시작');
            
            fetch(this.contextPath + '/api/news/recent?limit=50')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('✅ 뉴스 로드 성공:', data.length + '개');
                    this.renderNews(data);
                })
                .catch(error => {
                    console.error('❌ 뉴스 로드 실패:', error);
                    this.showError('뉴스를 불러오는 중 오류가 발생했습니다.');
                });
        },
        
        /**
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 뉴스 새로고침 (크롤링 실행 + 로드)
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
        refreshNews: function() {
            console.log('🔄 뉴스 새로고침 시작 (크롤링)');
            
            const refreshBtn = document.getElementById('refreshNewsBtn');
            const originalHtml = refreshBtn.innerHTML;
            
            refreshBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> 크롤링 중...';
            refreshBtn.disabled = true;
            
            fetch(this.contextPath + '/api/news/refresh', {
                method: 'POST'
            })
                .then(response => response.json())
                .then(data => {
                    console.log('✅ 크롤링 완료:', data);
                    
                    // 1초 후 새로운 뉴스 로드
                    setTimeout(() => {
                        this.loadNews();
                    }, 1000);
                })
                .catch(error => {
                    console.error('❌ 크롤링 실패:', error);
                    this.showError('뉴스 크롤링 중 오류가 발생했습니다.');
                })
                .finally(() => {
                    refreshBtn.innerHTML = originalHtml;
                    refreshBtn.disabled = false;
                });
        },
        
        /**
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 자동 새로고침 시작
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
        startAutoRefresh: function() {
            console.log('⏰ 자동 새로고침 시작 (5분 간격)');
            
            this.autoRefreshInterval = setInterval(() => {
                console.log('⏰ 자동 새로고침 실행');
                this.loadNews();
            }, this.autoRefreshTime);
        },
        
        /**
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 뉴스 렌더링 (수정 버전 - JSP EL 표현식 오류 해결)
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 
         * ✅ 핵심 수정:
         * - backtick (`) 사용 제거
         * - JSP EL 표현식과 JavaScript Template Literals 분리
         * - 조건부 HTML 생성을 JavaScript 변수로 처리
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
        renderNews: function(newsList) {
            console.log('🎨 뉴스 렌더링');
            
            const container = document.getElementById('newsContainer');
            
            if (!newsList || newsList.length === 0) {
                container.innerHTML = '<div class="alert alert-info text-center">' +
                                        '<i class="fas fa-info-circle"></i> ' +
                                        '뉴스가 없습니다. 새로고침 버튼을 눌러 뉴스를 불러오세요.' +
                                      '</div>';
                return;
            }
            
            // ✅ contextPath는 JSP에서 생성 (JavaScript 영역 밖)
            const contextPath = this.contextPath;
            
            let html = '<div class="row">';
            
            newsList.forEach(news => {
                const dateStr = news.publishedAt || news.createdAt || '';
                
                // ✅ 조건부 HTML을 JavaScript 변수로 처리
                let categoryHtml = '';
                if (news.category) {
                    categoryHtml = '<div class="mb-2">' +
                                     '<span class="news-category">' + news.category + '</span>' +
                                   '</div>';
                }
                
                let sourceHtml = '';
                if (news.source) {
                    sourceHtml = '<small class="ms-3">' +
                                   '<i class="fas fa-newspaper"></i> ' +
                                   news.source +
                                 '</small>';
                }
                
                // ✅ 템플릿 리터럴 사용 (backtick 없음, + 연산자 사용)
                html += '<div class="col-md-6 mb-4">' +
                          '<div class="card news-card h-100" ' +
                               'onclick="location.href=\'' + contextPath + '/news/detail/' + news.newsId + '\'">' +
                            '<div class="card-body">' +
                              '<h5 class="card-title">' +
                                (news.title || '제목 없음') +
                              '</h5>' +
                              categoryHtml +
                              '<p class="card-text text-muted">' +
                                (news.summary || news.content || '내용 없음') +
                              '</p>' +
                              '<div class="news-meta mt-3">' +
                                '<small>' +
                                  '<i class="fas fa-calendar"></i> ' +
                                  dateStr +
                                '</small>' +
                                sourceHtml +
                              '</div>' +
                            '</div>' +
                          '</div>' +
                        '</div>';
            });
            
            html += '</div>';
            container.innerHTML = html;
            
            console.log('✅ 뉴스 렌더링 완료');
        },
        
        /**
         * 에러 표시
         */
        showError: function(message) {
            const container = document.getElementById('newsContainer');
            container.innerHTML = '<div class="alert alert-danger">' +
                                    '<i class="fas fa-exclamation-triangle"></i> ' +
                                    message +
                                  '</div>';
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
