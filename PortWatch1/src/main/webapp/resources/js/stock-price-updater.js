/**
 * PortWatch 실시간 주가 업데이트 유틸리티
 * 주식 상세 페이지, 포트폴리오, 관심종목에서 사용
 */

var StockPriceUpdater = (function() {
    'use strict';
    
    // 설정
    var config = {
        apiBaseUrl: '/api/stock-price',
        updateInterval: 60000, // 60초마다 자동 업데이트
        enableAutoUpdate: false, // 기본값: 수동 업데이트만
        debugMode: true
    };
    
    // 업데이트 중 상태
    var isUpdating = false;
    var autoUpdateTimer = null;
    
    /**
     * 로그 출력
     */
    function log(message, type) {
        if (!config.debugMode) return;
        
        var prefix = '📊 [StockPrice]';
        switch(type) {
            case 'error':
                console.error(prefix, message);
                break;
            case 'warn':
                console.warn(prefix, message);
                break;
            case 'success':
                console.log('✅', prefix, message);
                break;
            default:
                console.log(prefix, message);
        }
    }
    
    /**
     * 단일 종목 주가 업데이트
     */
    function updateSingleStock(stockCode, callback) {
        if (isUpdating) {
            log('이미 업데이트 중입니다.', 'warn');
            return;
        }
        
        isUpdating = true;
        log('종목 ' + stockCode + ' 업데이트 시작...');
        
        $.ajax({
            url: config.apiBaseUrl + '/update/' + stockCode,
            method: 'GET',
            success: function(response) {
                log('업데이트 성공: ' + stockCode, 'success');
                isUpdating = false;
                
                if (callback && typeof callback === 'function') {
                    callback(null, response.data);
                }
            },
            error: function(xhr, status, error) {
                log('업데이트 실패: ' + error, 'error');
                isUpdating = false;
                
                if (callback && typeof callback === 'function') {
                    callback(error, null);
                }
            }
        });
    }
    
    /**
     * 여러 종목 주가 업데이트
     */
    function updateMultipleStocks(stockCodes, callback) {
        if (isUpdating) {
            log('이미 업데이트 중입니다.', 'warn');
            return;
        }
        
        if (!stockCodes || stockCodes.length === 0) {
            log('업데이트할 종목이 없습니다.', 'warn');
            return;
        }
        
        isUpdating = true;
        log(stockCodes.length + '개 종목 업데이트 시작...');
        
        $.ajax({
            url: config.apiBaseUrl + '/update-multiple',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ stockCodes: stockCodes }),
            success: function(response) {
                log('업데이트 성공: ' + response.successCount + '개', 'success');
                isUpdating = false;
                
                if (callback && typeof callback === 'function') {
                    callback(null, response.data);
                }
            },
            error: function(xhr, status, error) {
                log('업데이트 실패: ' + error, 'error');
                isUpdating = false;
                
                if (callback && typeof callback === 'function') {
                    callback(error, null);
                }
            }
        });
    }
    
    /**
     * 최신 주가 조회 (DB에서)
     */
    function getLatestPrice(stockCode, callback) {
        $.ajax({
            url: config.apiBaseUrl + '/latest/' + stockCode,
            method: 'GET',
            success: function(response) {
                if (callback && typeof callback === 'function') {
                    callback(null, response.data);
                }
            },
            error: function(xhr, status, error) {
                if (callback && typeof callback === 'function') {
                    callback(error, null);
                }
            }
        });
    }
    
    /**
     * 실시간 크롤링 (DB 저장 안 함)
     */
    function crawlPrice(stockCode, callback) {
        $.ajax({
            url: config.apiBaseUrl + '/crawl/' + stockCode,
            method: 'GET',
            success: function(response) {
                if (callback && typeof callback === 'function') {
                    callback(null, response.data);
                }
            },
            error: function(xhr, status, error) {
                if (callback && typeof callback === 'function') {
                    callback(error, null);
                }
            }
        });
    }
    
    /**
     * 자동 업데이트 시작
     */
    function startAutoUpdate(stockCode, updateCallback) {
        if (autoUpdateTimer) {
            log('이미 자동 업데이트가 실행 중입니다.', 'warn');
            return;
        }
        
        config.enableAutoUpdate = true;
        log('자동 업데이트 시작 (' + (config.updateInterval / 1000) + '초 간격)');
        
        // 즉시 한번 업데이트
        updateSingleStock(stockCode, updateCallback);
        
        // 주기적 업데이트
        autoUpdateTimer = setInterval(function() {
            if (config.enableAutoUpdate) {
                updateSingleStock(stockCode, updateCallback);
            }
        }, config.updateInterval);
    }
    
    /**
     * 자동 업데이트 중지
     */
    function stopAutoUpdate() {
        if (autoUpdateTimer) {
            clearInterval(autoUpdateTimer);
            autoUpdateTimer = null;
            config.enableAutoUpdate = false;
            log('자동 업데이트 중지');
        }
    }
    
    /**
     * 가격 포맷팅
     */
    function formatPrice(price) {
        if (!price) return '-';
        return new Intl.NumberFormat('ko-KR').format(price) + '원';
    }
    
    /**
     * 거래량 포맷팅
     */
    function formatVolume(volume) {
        if (!volume) return '-';
        return new Intl.NumberFormat('ko-KR').format(volume);
    }
    
    /**
     * 변동률 계산
     */
    function calculateChangeRate(currentPrice, previousPrice) {
        if (!currentPrice || !previousPrice) return 0;
        return ((currentPrice - previousPrice) / previousPrice * 100).toFixed(2);
    }
    
    /**
     * 설정 변경
     */
    function setConfig(options) {
        if (options.updateInterval) {
            config.updateInterval = options.updateInterval;
        }
        if (options.debugMode !== undefined) {
            config.debugMode = options.debugMode;
        }
    }
    
    // Public API
    return {
        // 주가 업데이트
        updateSingle: updateSingleStock,
        updateMultiple: updateMultipleStocks,
        getLatest: getLatestPrice,
        crawl: crawlPrice,
        
        // 자동 업데이트
        startAuto: startAutoUpdate,
        stopAuto: stopAutoUpdate,
        
        // 유틸리티
        formatPrice: formatPrice,
        formatVolume: formatVolume,
        calculateChangeRate: calculateChangeRate,
        
        // 설정
        config: setConfig,
        
        // 상태 확인
        isUpdating: function() { return isUpdating; },
        isAutoRunning: function() { return autoUpdateTimer !== null; }
    };
})();
