/**
 * ✅ 종목 필터링 JavaScript
 * 
 * 기능:
 * 1. 전체/나라별/시장별 필터링
 * 2. 업종별 필터링
 * 3. 검색 기능
 * 4. 동적 UI 업데이트
 * 
 * @author PortWatch
 * @version 1.0
 */

// ========================================
// 전역 변수
// ========================================
let currentFilter = {
    country: null,
    market: null,
    industry: null
};

// ========================================
// 1. 페이지 로드 시 초기화
// ========================================
$(document).ready(function() {
    console.log('📊 종목 필터 모듈 초기화...');
    
    // 초기 종목 로드 (전체)
    loadAllStocks();
    
    // 필터 버튼 이벤트
    setupFilterButtons();
    
    // 검색 기능
    setupSearch();
    
    // 업종 목록 로드
    loadIndustries();
});

// ========================================
// 2. 전체 종목 로드
// ========================================
function loadAllStocks() {
    console.log('📊 전체 종목 로드...');
    
    $.ajax({
        url: '/api/stocks',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                console.log(`✅ ${response.count}개 종목 로드 완료`);
                renderStocks(response.stocks);
                updateStatsInfo(response.count, '전체');
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 종목 로드 실패:', error);
            showErrorMessage('종목을 불러오는데 실패했습니다.');
        }
    });
}

// ========================================
// 3. 나라별 필터링
// ========================================
function filterByCountry(country) {
    console.log(`📊 나라별 필터링: ${country}`);
    
    currentFilter.country = country;
    
    $.ajax({
        url: `/api/stocks/country/${country}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                console.log(`✅ ${response.count}개 종목 로드 완료`);
                renderStocks(response.stocks);
                updateStatsInfo(response.count, country === 'KR' ? '한국' : '미국');
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 나라별 필터링 실패:', error);
            showErrorMessage('나라별 필터링에 실패했습니다.');
        }
    });
}

// ========================================
// 4. 시장별 필터링
// ========================================
function filterByMarket(market) {
    console.log(`📊 시장별 필터링: ${market}`);
    
    currentFilter.market = market;
    
    $.ajax({
        url: `/api/stocks/market/${market}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                console.log(`✅ ${response.count}개 종목 로드 완료`);
                renderStocks(response.stocks);
                updateStatsInfo(response.count, market);
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 시장별 필터링 실패:', error);
            showErrorMessage('시장별 필터링에 실패했습니다.');
        }
    });
}

// ========================================
// 5. 업종별 필터링
// ========================================
function filterByIndustry(industry) {
    console.log(`📊 업종별 필터링: ${industry}`);
    
    currentFilter.industry = industry;
    
    $.ajax({
        url: `/api/stocks/industry/${industry}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                console.log(`✅ ${response.count}개 종목 로드 완료`);
                renderStocks(response.stocks);
                updateStatsInfo(response.count, industry);
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 업종별 필터링 실패:', error);
            showErrorMessage('업종별 필터링에 실패했습니다.');
        }
    });
}

// ========================================
// 6. 복합 필터링
// ========================================
function applyFilters() {
    console.log('📊 복합 필터 적용:', currentFilter);
    
    const params = new URLSearchParams();
    if (currentFilter.country) params.append('country', currentFilter.country);
    if (currentFilter.market) params.append('market', currentFilter.market);
    if (currentFilter.industry) params.append('industry', currentFilter.industry);
    
    $.ajax({
        url: `/api/stocks/filter?${params.toString()}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                console.log(`✅ ${response.count}개 종목 로드 완료`);
                renderStocks(response.stocks);
                updateStatsInfo(response.count, '복합 필터');
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 복합 필터링 실패:', error);
            showErrorMessage('필터링에 실패했습니다.');
        }
    });
}

// ========================================
// 7. 종목 렌더링
// ========================================
function renderStocks(stocks) {
    const container = $('#stocks-container');
    container.empty();
    
    if (stocks.length === 0) {
        container.html(`
            <div class="no-stocks">
                <i class="fas fa-inbox"></i>
                <p>조건에 맞는 종목이 없습니다.</p>
            </div>
        `);
        return;
    }
    
    stocks.forEach(function(stock) {
        const stockCard = createStockCard(stock);
        container.append(stockCard);
    });
}

// ========================================
// 8. 종목 카드 생성
// ========================================
function createStockCard(stock) {
    const changeClass = getChangeClass(stock.priceChangeRate);
    const changeIcon = getChangeIcon(stock.priceChangeRate);
    
    const card = $(`
        <div class="stock-card" data-stock-code="${stock.stockCode}">
            <div class="stock-header">
                <div class="stock-name-group">
                    <h4 class="stock-name">${stock.stockName}</h4>
                    <span class="stock-code">${stock.stockCode}</span>
                </div>
                <div class="stock-market">
                    <span class="badge badge-${getMarketBadgeClass(stock.marketType)}">
                        ${stock.marketType}
                    </span>
                </div>
            </div>
            <div class="stock-body">
                <div class="stock-price">
                    <span class="current-price">
                        ${formatPrice(stock.currentPrice, stock.country)}
                    </span>
                    <span class="price-change ${changeClass}">
                        ${changeIcon}
                        ${formatChange(stock.priceChange, stock.country)}
                        (${formatRate(stock.priceChangeRate)})
                    </span>
                </div>
                <div class="stock-info">
                    <div class="info-item">
                        <span class="label">거래량</span>
                        <span class="value">${formatVolume(stock.volume)}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">업종</span>
                        <span class="value">${stock.industry || '-'}</span>
                    </div>
                </div>
            </div>
            <div class="stock-footer">
                <button class="btn-detail" onclick="viewStockDetail('${stock.stockCode}')">
                    상세보기
                </button>
                <button class="btn-buy" onclick="openBuyModal('${stock.stockCode}')">
                    매수
                </button>
            </div>
        </div>
    `);
    
    return card;
}

// ========================================
// 9. 필터 버튼 설정
// ========================================
function setupFilterButtons() {
    // 전체 버튼
    $('#btn-all').click(function() {
        currentFilter = { country: null, market: null, industry: null };
        loadAllStocks();
        updateActiveButton($(this));
    });
    
    // 한국 버튼
    $('#btn-korea').click(function() {
        filterByCountry('KR');
        updateActiveButton($(this));
    });
    
    // 미국 버튼
    $('#btn-usa').click(function() {
        filterByCountry('US');
        updateActiveButton($(this));
    });
    
    // 시장별 버튼 (KOSPI, KOSDAQ, NASDAQ, NYSE)
    $('.btn-market').click(function() {
        const market = $(this).data('market');
        filterByMarket(market);
        updateActiveButton($(this));
    });
    
    // 업종별 드롭다운
    $('#industry-select').change(function() {
        const industry = $(this).val();
        if (industry) {
            filterByIndustry(industry);
        } else {
            loadAllStocks();
        }
    });
}

// ========================================
// 10. 검색 기능
// ========================================
function setupSearch() {
    const searchInput = $('#stock-search-input');
    const searchButton = $('#stock-search-button');
    
    // 검색 버튼 클릭
    searchButton.click(function() {
        const keyword = searchInput.val().trim();
        if (keyword) {
            searchStocks(keyword);
        }
    });
    
    // 엔터 키 입력
    searchInput.keypress(function(e) {
        if (e.which === 13) {
            const keyword = $(this).val().trim();
            if (keyword) {
                searchStocks(keyword);
            }
        }
    });
}

function searchStocks(keyword) {
    console.log(`🔍 종목 검색: ${keyword}`);
    
    $.ajax({
        url: `/api/stocks/search?keyword=${encodeURIComponent(keyword)}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                console.log(`✅ ${response.count}개 종목 검색 완료`);
                renderStocks(response.stocks);
                updateStatsInfo(response.count, `검색: ${keyword}`);
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 검색 실패:', error);
            showErrorMessage('검색에 실패했습니다.');
        }
    });
}

// ========================================
// 11. 업종 목록 로드
// ========================================
function loadIndustries() {
    $.ajax({
        url: '/api/stocks/industries',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                const select = $('#industry-select');
                select.empty();
                select.append('<option value="">전체 업종</option>');
                
                response.industries.forEach(function(industry) {
                    select.append(`<option value="${industry}">${industry}</option>`);
                });
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 업종 목록 로드 실패:', error);
        }
    });
}

// ========================================
// 12. UI 업데이트 함수
// ========================================
function updateActiveButton(button) {
    $('.filter-button').removeClass('active');
    button.addClass('active');
}

function updateStatsInfo(count, filterName) {
    $('#stock-count').text(count);
    $('#current-filter').text(filterName);
}

function showErrorMessage(message) {
    // 에러 토스트 표시
    const toast = $(`
        <div class="toast error">
            <i class="fas fa-exclamation-circle"></i>
            ${message}
        </div>
    `);
    $('body').append(toast);
    
    setTimeout(() => toast.fadeOut(300, () => toast.remove()), 3000);
}

// ========================================
// 13. 유틸리티 함수
// ========================================
function formatPrice(price, country) {
    if (!price) return '-';
    
    if (country === 'US') {
        return '$' + Number(price).toFixed(2);
    } else {
        return Number(price).toLocaleString() + '원';
    }
}

function formatChange(change, country) {
    if (!change) return '-';
    
    const num = Number(change);
    const sign = num >= 0 ? '+' : '';
    
    if (country === 'US') {
        return sign + '$' + num.toFixed(2);
    } else {
        return sign + num.toLocaleString() + '원';
    }
}

function formatRate(rate) {
    if (!rate) return '-';
    
    const num = Number(rate);
    const sign = num >= 0 ? '+' : '';
    
    return sign + num.toFixed(2) + '%';
}

function formatVolume(volume) {
    if (!volume) return '-';
    
    const num = Number(volume);
    
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    } else {
        return num.toLocaleString();
    }
}

function getChangeClass(rate) {
    if (!rate) return '';
    
    const num = Number(rate);
    if (num > 0) return 'positive';
    if (num < 0) return 'negative';
    return '';
}

function getChangeIcon(rate) {
    if (!rate) return '';
    
    const num = Number(rate);
    if (num > 0) return '▲';
    if (num < 0) return '▼';
    return '-';
}

function getMarketBadgeClass(market) {
    const badges = {
        'KOSPI': 'primary',
        'KOSDAQ': 'info',
        'NASDAQ': 'success',
        'NYSE': 'warning',
        'AMEX': 'secondary'
    };
    
    return badges[market] || 'secondary';
}

// ========================================
// 14. 종목 상세보기 / 매수 모달
// ========================================
function viewStockDetail(stockCode) {
    console.log('📊 종목 상세보기:', stockCode);
    window.location.href = `/stocks/${stockCode}`;
}

function openBuyModal(stockCode) {
    console.log('💰 매수 모달 열기:', stockCode);
    $('#buyStockCode').val(stockCode);
    $('#buyModal').modal('show');
}
