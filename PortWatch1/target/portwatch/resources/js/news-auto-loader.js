/**
 * ✅ 뉴스 자동 로딩 및 버퍼링 제거
 * 
 * 기능:
 * 1. 페이지 로드 시 자동으로 뉴스 불러오기
 * 2. 스크롤 시 자동으로 다음 뉴스 로드 (무한 스크롤)
 * 3. 뉴스 클릭 시 버퍼링 없이 바로 표시
 * 4. 로딩 인디케이터 추가
 * 
 * @author PortWatch
 * @version 1.0
 */

// ========================================
// 전역 변수
// ========================================
let currentPage = 1;
let isLoading = false;
let hasMoreNews = true;

// ========================================
// 1. 페이지 로드 시 자동 실행
// ========================================
$(document).ready(function() {
    console.log('📰 뉴스 모듈 초기화...');
    
    // 초기 뉴스 로드
    loadNews(1);
    
    // 무한 스크롤 설정
    setupInfiniteScroll();
    
    // 뉴스 클릭 이벤트
    setupNewsClickHandler();
});

// ========================================
// 2. 뉴스 로드 함수 (AJAX)
// ========================================
function loadNews(page, stockCode = null) {
    if (isLoading || !hasMoreNews) {
        console.log('⏸️ 로딩 중이거나 더 이상 뉴스가 없습니다.');
        return;
    }
    
    isLoading = true;
    showLoadingIndicator();
    
    const url = stockCode 
        ? `/api/news/stock/${stockCode}?page=${page}&limit=10`
        : `/api/news/recent?page=${page}&limit=10`;
    
    $.ajax({
        url: url,
        method: 'GET',
        dataType: 'json',
        timeout: 10000, // 10초 타임아웃
        success: function(response) {
            console.log(`✅ 뉴스 ${response.length}개 로드 완료`);
            
            if (response.length === 0) {
                hasMoreNews = false;
                showNoMoreNewsMessage();
            } else {
                renderNews(response);
                currentPage++;
            }
            
            hideLoadingIndicator();
            isLoading = false;
        },
        error: function(xhr, status, error) {
            console.error('❌ 뉴스 로드 실패:', error);
            
            hideLoadingIndicator();
            isLoading = false;
            
            // 에러 메시지 표시
            showErrorMessage('뉴스를 불러오는데 실패했습니다. 다시 시도해주세요.');
        }
    });
}

// ========================================
// 3. 뉴스 렌더링 (버퍼링 없이)
// ========================================
function renderNews(newsList) {
    const newsContainer = $('#news-container');
    
    newsList.forEach(function(news) {
        const newsCard = createNewsCard(news);
        newsContainer.append(newsCard);
        
        // ✅ 애니메이션 효과 (부드러운 표시)
        newsCard.hide().fadeIn(300);
    });
}

// ========================================
// 4. 뉴스 카드 생성 (HTML)
// ========================================
function createNewsCard(news) {
    const card = $(`
        <div class="news-card" data-news-id="${news.newsId}" data-link="${news.link}">
            <div class="news-header">
                <span class="news-source">${news.source || '뉴스'}</span>
                <span class="news-date">${formatDate(news.publishedAt)}</span>
            </div>
            <div class="news-body">
                <h4 class="news-title">${news.title}</h4>
                <div class="news-meta">
                    <span class="stock-code">${news.stockCode}</span>
                    <span class="stock-name">${news.stockName}</span>
                </div>
            </div>
            <div class="news-footer">
                <button class="btn-read-more">
                    <i class="fas fa-external-link-alt"></i> 기사 보기
                </button>
            </div>
        </div>
    `);
    
    return card;
}

// ========================================
// 5. 뉴스 클릭 이벤트 (버퍼링 제거)
// ========================================
function setupNewsClickHandler() {
    // ✅ 이벤트 위임 (동적 생성된 요소에도 적용)
    $(document).on('click', '.news-card, .btn-read-more', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        const card = $(this).closest('.news-card');
        const link = card.data('link');
        
        if (link) {
            // ✅ 새 탭에서 바로 열기 (버퍼링 없음)
            window.open(link, '_blank', 'noopener,noreferrer');
            
            // 클릭 피드백
            card.addClass('clicked');
            setTimeout(() => card.removeClass('clicked'), 300);
        } else {
            console.warn('⚠️ 뉴스 링크가 없습니다.');
        }
    });
}

// ========================================
// 6. 무한 스크롤 설정
// ========================================
function setupInfiniteScroll() {
    $(window).scroll(function() {
        // 페이지 하단에 도달했는지 확인
        if ($(window).scrollTop() + $(window).height() >= $(document).height() - 100) {
            loadNews(currentPage);
        }
    });
}

// ========================================
// 7. 로딩 인디케이터
// ========================================
function showLoadingIndicator() {
    if ($('#news-loading-indicator').length === 0) {
        const indicator = $(`
            <div id="news-loading-indicator" class="loading-indicator">
                <div class="spinner"></div>
                <p>뉴스를 불러오는 중...</p>
            </div>
        `);
        $('#news-container').after(indicator);
    } else {
        $('#news-loading-indicator').show();
    }
}

function hideLoadingIndicator() {
    $('#news-loading-indicator').fadeOut(300);
}

function showNoMoreNewsMessage() {
    const message = $(`
        <div class="no-more-news">
            <i class="fas fa-check-circle"></i>
            <p>모든 뉴스를 불러왔습니다.</p>
        </div>
    `);
    $('#news-container').after(message);
}

function showErrorMessage(message) {
    const errorDiv = $(`
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-triangle"></i> ${message}
            <button type="button" class="close" data-dismiss="alert">
                <span>&times;</span>
            </button>
        </div>
    `);
    $('#news-container').prepend(errorDiv);
    
    // 5초 후 자동 제거
    setTimeout(() => errorDiv.fadeOut(300, () => errorDiv.remove()), 5000);
}

// ========================================
// 8. 유틸리티 함수
// ========================================
function formatDate(dateString) {
    if (!dateString) return '방금 전';
    
    const date = new Date(dateString);
    const now = new Date();
    const diff = Math.floor((now - date) / 1000); // 초 단위
    
    if (diff < 60) return '방금 전';
    if (diff < 3600) return Math.floor(diff / 60) + '분 전';
    if (diff < 86400) return Math.floor(diff / 3600) + '시간 전';
    if (diff < 604800) return Math.floor(diff / 86400) + '일 전';
    
    // 일주일 이상이면 날짜 표시
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${year}-${month}-${day}`;
}

// ========================================
// 9. 종목별 뉴스 로드 (필터링)
// ========================================
function loadNewsByStock(stockCode) {
    console.log(`📊 종목별 뉴스 로드: ${stockCode}`);
    
    // 기존 뉴스 초기화
    $('#news-container').empty();
    currentPage = 1;
    hasMoreNews = true;
    
    // 새 뉴스 로드
    loadNews(1, stockCode);
}

// ========================================
// 10. 뉴스 새로고침
// ========================================
function refreshNews() {
    console.log('🔄 뉴스 새로고침...');
    
    // 초기화
    $('#news-container').empty();
    currentPage = 1;
    hasMoreNews = true;
    
    // 다시 로드
    loadNews(1);
}

// ========================================
// CSS 스타일 (버퍼링 제거 & 애니메이션)
// ========================================
const newsStyles = `
<style>
/* 뉴스 카드 스타일 */
.news-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 16px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    transition: all 0.3s ease;
    cursor: pointer;
}

.news-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.news-card.clicked {
    transform: scale(0.98);
    opacity: 0.8;
}

/* 뉴스 헤더 */
.news-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;
    font-size: 12px;
    color: #6c757d;
}

.news-source {
    background: #007bff;
    color: white;
    padding: 2px 8px;
    border-radius: 4px;
    font-weight: 600;
}

/* 뉴스 제목 */
.news-title {
    font-size: 16px;
    font-weight: 600;
    color: #212529;
    margin-bottom: 12px;
    line-height: 1.5;
}

/* 뉴스 메타 정보 */
.news-meta {
    display: flex;
    gap: 8px;
    margin-top: 8px;
}

.stock-code {
    background: #f8f9fa;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
    color: #495057;
}

.stock-name {
    color: #6c757d;
    font-size: 12px;
}

/* 버튼 스타일 */
.btn-read-more {
    width: 100%;
    margin-top: 12px;
    padding: 10px;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s ease;
}

.btn-read-more:hover {
    background: #0056b3;
    transform: translateY(-2px);
}

/* 로딩 인디케이터 */
.loading-indicator {
    text-align: center;
    padding: 40px;
    color: #6c757d;
}

.spinner {
    width: 40px;
    height: 40px;
    margin: 0 auto 16px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #007bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

/* 더 이상 뉴스 없음 */
.no-more-news {
    text-align: center;
    padding: 40px;
    color: #6c757d;
}

.no-more-news i {
    font-size: 48px;
    color: #28a745;
    margin-bottom: 16px;
}
</style>
`;

// 스타일 추가
$('head').append(newsStyles);
