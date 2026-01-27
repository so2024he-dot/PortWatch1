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
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        .news-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 15px;
        }
        
        .news-header {
            background: white;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .news-header h1 {
            font-weight: 700;
            color: #1f2937;
            margin-bottom: 1rem;
        }
        
        .news-card {
            transition: transform 0.2s, box-shadow 0.2s;
            cursor: pointer;
            height: 100%;
            border-radius: 15px;
            border: none;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .news-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 20px rgba(0,0,0,0.15);
        }
        
        .news-meta {
            font-size: 0.85em;
            color: #6c757d;
        }
        
        .news-category {
            display: inline-block;
            padding: 4px 12px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px;
            font-size: 0.75em;
            margin-right: 5px;
            font-weight: 600;
        }
        
        .loading-spinner {
            display: none;
            text-align: center;
            padding: 3rem;
        }
        
        .error-message {
            display: none;
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
                        <c:when test="${not empty member}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" 
                                   role="button" data-bs-toggle="dropdown">
                                    <i class="fas fa-user"></i> ${member.memberName}
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
    <div class="news-container">
        <!-- 헤더 -->
        <div class="news-header">
            <h1>
                <i class="fas fa-newspaper"></i> 증권 뉴스
            </h1>
            <p class="text-muted mb-3">최신 금융/증권 뉴스를 실시간으로 확인하세요</p>
            
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <button id="refreshNewsBtn" class="btn btn-primary">
                        <i class="fas fa-sync-alt"></i> 새로고침
                    </button>
                </div>
                <div id="lastUpdateTime" class="text-muted">
                    <i class="fas fa-clock"></i> 마지막 업데이트: -
                </div>
            </div>
        </div>
        
        <!-- 로딩 스피너 -->
        <div id="loadingSpinner" class="loading-spinner">
            <div class="spinner-border text-primary" role="status" style="width: 3rem; height: 3rem;">
                <span class="visually-hidden">Loading...</span>
            </div>
            <p class="mt-3">뉴스를 불러오는 중...</p>
        </div>
        
        <!-- 에러 메시지 -->
        <div id="errorMessage" class="alert alert-danger error-message">
            <i class="fas fa-exclamation-triangle"></i> 
            <span id="errorText"></span>
        </div>
        
        <!-- 뉴스 컨테이너 -->
        <div id="newsContainer" class="row">
            <!-- 뉴스 목록이 여기에 동적으로 추가됩니다 -->
        </div>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 뉴스 관리자 - 완전 수정 버전 (2026.01.16)
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 수정 사항:
     * - EL 표현식 오류 완전 제거
     * - 문자열 연결 방식으로 변경
     * - 뉴스 표시 로직 개선
     */
    const NewsManager = {
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
            this.startAutoRefresh();
        },
        
        /**
         * 이벤트 바인딩
         */
        bindEvents: function() {
            const refreshBtn = document.getElementById('refreshNewsBtn');
            if (refreshBtn) {
                refreshBtn.addEventListener('click', () => {
                    console.log('🔄 새로고침 버튼 클릭');
                    this.refreshNews();
                });
            }
        },
        
        /**
         * 뉴스 로드
         */
        loadNews: function() {
            console.log('📰 뉴스 로드 시작');
            this.showLoading();
            
            const apiUrl = this.contextPath + '/api/news/recent?limit=50';
            console.log('🔗 API 호출:', apiUrl);
            
            fetch(apiUrl)
                .then(response => {
                    console.log('📡 서버 응답:', response.status);
                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('✅ 데이터 수신:', data);
                    
                    // 응답 데이터 파싱
                    let newsList = [];
                    if (Array.isArray(data)) {
                        newsList = data;
                    } else if (data.news) {
                        newsList = data.news;
                    } else if (data.newsList) {
                        newsList = data.newsList;
                    }
                    
                    console.log('📋 뉴스 개수:', newsList.length);
                    
                    // 뉴스 렌더링
                    this.renderNews(newsList);
                    this.hideLoading();
                    this.updateTime();
                })
                .catch(error => {
                    console.error('❌ 로드 실패:', error);
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
            
            refreshBtn.disabled = true;
            refreshBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 크롤링 중...';
            
            fetch(this.contextPath + '/api/admin/update-news')
                .then(response => response.json())
                .then(data => {
                    console.log('✅ 크롤링 완료:', data);
                    
                    refreshBtn.disabled = false;
                    refreshBtn.innerHTML = originalHtml;
                    
                    // 뉴스 목록 새로고침
                    this.loadNews();
                    
                    const count = data.savedCount || data.count || 0;
                    alert(count + '개의 새로운 뉴스를 불러왔습니다!');
                })
                .catch(error => {
                    console.error('❌ 크롤링 실패:', error);
                    refreshBtn.disabled = false;
                    refreshBtn.innerHTML = originalHtml;
                    alert('뉴스 새로고침에 실패했습니다.');
                });
        },
        
        /**
         * 자동 새로고침
         */
        startAutoRefresh: function() {
            console.log('⏰ 자동 새로고침 시작 (5분 간격)');
            
            if (this.autoRefreshInterval) {
                clearInterval(this.autoRefreshInterval);
            }
            
            this.autoRefreshInterval = setInterval(() => {
                console.log('⏰ 자동 새로고침 실행');
                this.loadNews();
            }, 5 * 60 * 1000);
        },
        
        /**
         * 뉴스 렌더링 (EL 표현식 오류 완전 제거!)
         */
        renderNews: function(newsList) {
            console.log('🎨 뉴스 렌더링:', newsList.length + '개');
            
            const container = document.getElementById('newsContainer');
            
            if (!newsList || newsList.length === 0) {
                container.innerHTML = '' +
                    '<div class="col-12">' +
                    '    <div class="alert alert-info text-center">' +
                    '        <i class="fas fa-info-circle"></i><br>' +
                    '        <strong>뉴스가 없습니다.</strong><br>' +
                    '        새로고침 버튼을 눌러 뉴스를 불러오세요.' +
                    '    </div>' +
                    '</div>';
                return;
            }
            
            let html = '';
            
            for (let i = 0; i < newsList.length; i++) {
                const news = newsList[i];
                
                // 안전한 값 추출
                const newsId = news.newsId || news.id || i;
                const title = news.title || news.newsTitle || '제목 없음';
                const summary = news.summary || news.content || news.newsTitle || '';
                const category = news.category || news.newsCol || '';
                const source = news.source || news.name || '';
                
                // 날짜 포맷팅
                let dateStr = '';
                if (news.publishedDate) {
                    dateStr = this.formatDate(news.publishedDate);
                } else if (news.publishedAt) {
                    dateStr = this.formatDate(news.publishedAt);
                } else if (news.createdAt) {
                    dateStr = this.formatDate(news.createdAt);
                } else {
                    dateStr = '날짜 정보 없음';
                }
                
                // URL 생성
                const newsUrl = news.newsUrl || news.url || '#';
                
                // 카드 HTML 생성 (완전 문자열 연결 방식)
                html += '<div class="col-md-6 mb-4">';
                html += '  <div class="card news-card h-100" onclick="window.open(\'' + newsUrl + '\', \'_blank\')">';
                html += '    <div class="card-body">';
                html += '      <h5 class="card-title">' + this.escapeHtml(title) + '</h5>';
                
                // 카테고리 표시
                if (category) {
                    html += '      <div class="mb-2">';
                    html += '        <span class="news-category">' + this.escapeHtml(category) + '</span>';
                    html += '      </div>';
                }
                
                // 요약 표시
                if (summary) {
                    html += '      <p class="card-text text-muted">';
                    html += '        ' + this.escapeHtml(this.truncate(summary, 100));
                    html += '      </p>';
                }
                
                // 메타 정보
                html += '      <div class="news-meta mt-3">';
                html += '        <small>';
                html += '          <i class="fas fa-calendar"></i> ' + dateStr;
                
                if (source) {
                    html += '          <span class="ms-3">';
                    html += '            <i class="fas fa-newspaper"></i> ' + this.escapeHtml(source);
                    html += '          </span>';
                }
                
                html += '        </small>';
                html += '      </div>';
                html += '    </div>';
                html += '  </div>';
                html += '</div>';
            }
            
            container.innerHTML = html;
            console.log('✅ 렌더링 완료');
        },
        
        /**
         * 날짜 포맷팅
         */
        formatDate: function(dateStr) {
            try {
                const date = new Date(dateStr);
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                const hour = String(date.getHours()).padStart(2, '0');
                const minute = String(date.getMinutes()).padStart(2, '0');
                return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
            } catch (e) {
                return dateStr;
            }
        },
        
        /**
         * HTML 이스케이프
         */
        escapeHtml: function(text) {
            if (!text) return '';
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        },
        
        /**
         * 텍스트 자르기
         */
        truncate: function(text, maxLength) {
            if (!text) return '';
            if (text.length <= maxLength) return text;
            return text.substring(0, maxLength) + '...';
        },
        
        /**
         * 로딩 표시
         */
        showLoading: function() {
            document.getElementById('loadingSpinner').style.display = 'block';
            document.getElementById('newsContainer').style.display = 'none';
            document.getElementById('errorMessage').style.display = 'none';
        },
        
        /**
         * 로딩 숨김
         */
        hideLoading: function() {
            document.getElementById('loadingSpinner').style.display = 'none';
            document.getElementById('newsContainer').style.display = 'flex';
        },
        
        /**
         * 에러 표시
         */
        showError: function(message) {
            document.getElementById('loadingSpinner').style.display = 'none';
            document.getElementById('newsContainer').style.display = 'none';
            document.getElementById('errorMessage').style.display = 'block';
            document.getElementById('errorText').textContent = message;
        },
        
        /**
         * 시간 업데이트
         */
        updateTime: function() {
            const now = new Date();
            const timeStr = now.toLocaleString('ko-KR');
            document.getElementById('lastUpdateTime').innerHTML = 
                '<i class="fas fa-clock"></i> 마지막 업데이트: ' + timeStr;
        }
    };
    
    // 페이지 로드 시 초기화
    document.addEventListener('DOMContentLoaded', function() {
        NewsManager.init();
    });
    </script>
</body>
</html>
