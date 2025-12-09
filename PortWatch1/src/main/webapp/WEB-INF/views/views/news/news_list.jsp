<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../common/header.jsp" />

<style>
.news-container {
	max-width: 1200px;
	margin: 2rem auto;
	padding: 0 1rem;
}

.news-header {
	margin-bottom: 2rem;
	text-align: center;
}

.news-title {
	font-size: 2.5rem;	
	font-weight: 700;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: yellow;
	margin-bottom: 0.5rem;
}

.news-subtitle {
	color: #white;
	font-size: 1.1rem;
}

.news-controls {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 1.5rem;
	padding: 1rem;
	background: #f9fafb;
	border-radius: 10px;
}

.news-count {
	font-weight: 600;
	color: var(--primary-color);
}

.btn-refresh {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border: none;
	color: white;
	padding: 0.5rem 1.5rem;
	border-radius: 8px;
	font-weight: 600;
	transition: all 0.3s;
}

.btn-refresh:hover {
	transform: translateY(-2px);
	box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.news-list {
	display: grid;
	gap: 1.5rem;
}

.news-card {
	background: white;
	border-radius: 15px;
	padding: 1.5rem;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
	transition: all 0.3s;
	border-left: 4px solid transparent;
}

.news-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
	border-left-color: var(--primary-color);
}

.news-card-title {
	font-size: 1.2rem;
	font-weight: 600;
	color: #1f2937;
	margin-bottom: 0.5rem;
	line-height: 1.4;
}

.news-card-title a {
	color: inherit;
	text-decoration: none;
	transition: color 0.3s;
}

.news-card-title a:hover {
	color: var(--primary-color);
}

.news-card-content {
	color: #6b7280;
	font-size: 0.95rem;
	margin-bottom: 1rem;
	line-height: 1.6;
}

.news-card-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 1rem;
	border-top: 1px solid #f3f4f6;
}

.news-source {
	display: flex;
	align-items: center;
	gap: 0.5rem;
	color: var(--primary-color);
	font-weight: 600;
	font-size: 0.875rem;
}

.news-source-icon {
	width: 24px;
	height: 24px;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-size: 0.75rem;
}

.news-date {
	color: #9ca3af;
	font-size: 0.875rem;
}

.loading {
	text-align: center;
	padding: 3rem;
}

.spinner {
	width: 50px;
	height: 50px;
	border: 4px solid #f3f4f6;
	border-top: 4px solid var(--primary-color);
	border-radius: 50%;
	animation: spin 1s linear infinite;
	margin: 0 auto 1rem;
}

@keyframes spin {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}

.loading p {
	color: #6b7280;
	font-size: 1.1rem;
}

.animate-fade-in {
	animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
	from {
		opacity: 0;
		transform: translateY(20px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.alert {
	padding: 1.5rem;
	border-radius: 10px;
	margin-bottom: 1rem;
}

.alert-warning {
	background: #fef3c7;
	border: 1px solid #fbbf24;
	color: #92400e;
}

.alert-danger {
	background: #fee2e2;
	border: 1px solid #ef4444;
	color: #991b1b;
}

.alert-info {
	background: #dbeafe;
	border: 1px solid #3b82f6;
	color: #1e3a8a;
}

@media ( max-width : 768px) {
	.news-title {
		font-size: 2rem;
	}
	.news-controls {
		flex-direction: column;
		gap: 1rem;
	}
}

/* Notification animation */
@keyframes slideInRight {
    from {
        transform: translateX(100%);
        opacity: 0;
    }
    to {
        transform: translateX(0);
        opacity: 1;
    }
}

/* Loading spinner for button */
.spinner-border-sm {
    width: 1rem;
    height: 1rem;
    border-width: 0.15em;
}
</style>

<div class="news-container">
	<div class="news-header animate-fade-in">
		<h1 class="news-title">
			<i class="bi bi-newspaper"></i> 실시간 증권 뉴스
		</h1>
		<p class="news-subtitle">네이버 금융의 최신 증권 뉴스를 실시간으로 확인하세요</p>
	</div>

	<div class="news-controls">
		<div class="news-count">
			<i class="bi bi-journal-text me-2"></i> 총 <span id="newsCount">${newsCount}</span>개의 뉴스
		</div>
		<button class="btn btn-refresh" onclick="refreshNews()">
			<i class="bi bi-arrow-clockwise me-2"></i>새로고침
		</button>
	</div>

	<div id="newsListContainer" class="news-list">
		<c:choose>
			<c:when test="${not empty newsList}">
				<c:forEach items="${newsList}" var="news">
					<div class="news-card animate-fade-in">
						<h3 class="news-card-title">
							<a href="${news.newsUrl}" target="_blank" rel="noopener noreferrer">
								${news.newsTitle}
							</a>
						</h3>
						<p class="news-card-content">${news.newsContent}</p>
						<div class="news-card-meta">
							<div class="news-source">
								<div class="news-source-icon">
									<i class="bi bi-building"></i>
								</div>
								<span>${news.newsSource}</span>
							</div>
							<div class="news-date">
								<i class="bi bi-clock me-1"></i>
								<fmt:formatDate value="${news.newsPubDate}" pattern="yyyy-MM-dd HH:mm" />
							</div>
						</div>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="loading">
					<div class="spinner"></div>
					<p>뉴스를 불러오는 중입니다...</p>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>

<script>
// Enhanced refresh function with better error handling and loading states
function refreshNews() {
    const container = document.getElementById('newsListContainer');
    const refreshBtn = document.querySelector('.btn-refresh');
    
    // Disable button during loading
    if (refreshBtn) {
        refreshBtn.disabled = true;
        refreshBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-2 spinner-border spinner-border-sm"></i>불러오는 중...';
    }
    
    // Show loading state
    container.innerHTML = 
        '<div class="loading">' +
            '<div class="spinner"></div>' +
            '<p>최신 뉴스를 불러오는 중입니다...</p>' +
            '<small class="text-muted">잠시만 기다려주세요 (최대 15초)</small>' +
        '</div>';
    
    // AJAX request with enhanced error handling
    $.ajax({
        url: '${pageContext.request.contextPath}/api/news/latest',
        type: 'GET',
        data: { limit: 20 },
        timeout: 20000, // 20 second timeout
        success: function(response) {
            console.log('뉴스 로드 성공:', response);
            
            if (response.success && response.newsList && response.newsList.length > 0) {
                displayNews(response.newsList);
                $('#newsCount').text(response.count);
                
                // Show success message
                showNotification('✅ ' + response.count + '개의 최신 뉴스를 불러왔습니다!', 'success');
            } else {
                // No news found
                container.innerHTML = 
                    '<div class="alert alert-warning">' +
                        '<i class="bi bi-exclamation-triangle me-2"></i>' +
                        '<strong>뉴스를 찾을 수 없습니다.</strong>' +
                        '<p class="mb-0 mt-2">현재 네이버 금융에서 뉴스를 가져올 수 없습니다. 잠시 후 다시 시도해주세요.</p>' +
                    '</div>';
                showNotification('⚠️ 뉴스를 불러올 수 없습니다', 'warning');
            }
        },
        error: function(xhr, status, error) {
            console.error('뉴스 로드 실패:', { xhr: xhr, status: status, error: error });
            
            var errorMessage = '서버 연결에 실패했습니다.';
            
            if (status === 'timeout') {
                errorMessage = '서버 응답 시간이 초과되었습니다. 잠시 후 다시 시도해주세요.';
            } else if (xhr.status === 500) {
                errorMessage = '서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.';
            } else if (xhr.status === 0) {
                errorMessage = '네트워크 연결을 확인해주세요.';
            }
            
            container.innerHTML = 
                '<div class="alert alert-danger">' +
                    '<i class="bi bi-x-circle me-2"></i>' +
                    '<strong>오류가 발생했습니다</strong>' +
                    '<p class="mb-0 mt-2">' + errorMessage + '</p>' +
                    '<button class="btn btn-sm btn-outline-danger mt-3" onclick="refreshNews()">' +
                        '<i class="bi bi-arrow-clockwise me-1"></i>다시 시도' +
                    '</button>' +
                '</div>';
            
            showNotification('❌ ' + errorMessage, 'error');
        },
        complete: function() {
            // Re-enable button
            if (refreshBtn) {
                refreshBtn.disabled = false;
                refreshBtn.innerHTML = '<i class="bi bi-arrow-clockwise me-2"></i>새로고침';
            }
        }
    });
}

// Enhanced display function with better formatting
function displayNews(newsList) {
    const container = document.getElementById('newsListContainer');
    var html = '';
    
    if (!newsList || newsList.length === 0) {
        html = 
            '<div class="alert alert-info">' +
                '<i class="bi bi-info-circle me-2"></i>' +
                '표시할 뉴스가 없습니다.' +
            '</div>';
    } else {
        for (var i = 0; i < newsList.length; i++) {
            var news = newsList[i];
            var index = i;
            
            // Escape HTML to prevent XSS
            var title = escapeHtml(news.newsTitle || '제목 없음');
            var content = escapeHtml(news.newsContent || '');
            var source = escapeHtml(news.newsSource || '출처 없음');
            var url = news.newsUrl || '#';
            var animationDelay = (index * 0.05) + 's';
            
            html += '<div class="news-card animate-fade-in" style="animation-delay: ' + animationDelay + '">';
            html +=     '<h3 class="news-card-title">';
            html +=         '<a href="' + url + '" target="_blank" rel="noopener noreferrer">';
            html +=             title;
            html +=         '</a>';
            html +=     '</h3>';
            
            if (content) {
                html += '<p class="news-card-content">' + content + '</p>';
            }
            
            html +=     '<div class="news-card-meta">';
            html +=         '<div class="news-source">';
            html +=             '<div class="news-source-icon">';
            html +=                 '<i class="bi bi-building"></i>';
            html +=             '</div>';
            html +=             '<span>' + source + '</span>';
            html +=         '</div>';
            html +=         '<div class="news-date">';
            html +=             '<i class="bi bi-clock me-1"></i>';
            html +=             '방금 전';
            html +=         '</div>';
            html +=     '</div>';
            html += '</div>';
        }
    }
    
    container.innerHTML = html;
}

// Show notification message
function showNotification(message, type) {
    type = type || 'info';
    
    // Remove existing notifications
    $('.notification-toast').remove();
    
    var bgColors = {
        'success': '#10b981',
        'error': '#ef4444',
        'warning': '#f59e0b',
        'info': '#3b82f6'
    };
    
    var notification = $('<div class="notification-toast">')
        .css({
            'position': 'fixed',
            'top': '20px',
            'right': '20px',
            'background': bgColors[type] || bgColors['info'],
            'color': 'white',
            'padding': '1rem 1.5rem',
            'border-radius': '10px',
            'box-shadow': '0 4px 12px rgba(0, 0, 0, 0.15)',
            'z-index': '9999',
            'max-width': '400px',
            'animation': 'slideInRight 0.3s ease-out'
        })
        .text(message);
    
    $('body').append(notification);
    
    // Auto remove after 3 seconds
    setTimeout(function() {
        notification.fadeOut(function() {
            $(this).remove();
        });
    }, 3000);
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    var map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}
</script>

<jsp:include page="../common/footer.jsp" />
