/**
 * ✅ 통합 주식 차트 (한국 + 미국)
 * 
 * 기능:
 * 1. Chart.js를 사용한 실시간 차트
 * 2. 한국 주식과 미국 주식 동일한 차트
 * 3. 일봉/주봉/월봉 지원
 * 4. 인터랙티브 차트 (줌, 팬)
 * 
 * @author PortWatch
 * @version 1.0
 */

// ========================================
// 전역 변수
// ========================================
let stockChart = null;
let chartPeriod = 'daily'; // daily, weekly, monthly

// ========================================
// 1. 차트 초기화
// ========================================
function initStockChart(stockCode, country) {
    console.log(`📈 차트 초기화: ${stockCode} (${country})`);
    
    // 기존 차트 제거
    if (stockChart) {
        stockChart.destroy();
    }
    
    // 차트 데이터 로드
    loadChartData(stockCode, country, chartPeriod);
}

// ========================================
// 2. 차트 데이터 로드
// ========================================
function loadChartData(stockCode, country, period) {
    console.log(`📊 차트 데이터 로드: ${stockCode}, ${period}`);
    
    $.ajax({
        url: `/api/chart/${stockCode}?period=${period}`,
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                console.log(`✅ 차트 데이터 ${response.data.length}개 로드`);
                renderChart(response.data, stockCode, country);
            } else {
                console.error('❌ 차트 데이터 없음');
                showNoChartDataMessage();
            }
        },
        error: function(xhr, status, error) {
            console.error('❌ 차트 데이터 로드 실패:', error);
            
            // 더미 데이터로 대체 (개발 중)
            const dummyData = generateDummyChartData(30);
            renderChart(dummyData, stockCode, country);
        }
    });
}

// ========================================
// 3. 차트 렌더링
// ========================================
function renderChart(data, stockCode, country) {
    const ctx = document.getElementById('stockChart');
    
    if (!ctx) {
        console.error('❌ 차트 캔버스를 찾을 수 없습니다.');
        return;
    }
    
    // 데이터 가공
    const labels = data.map(d => formatChartDate(d.date));
    const prices = data.map(d => d.close);
    const volumes = data.map(d => d.volume);
    
    // 색상 결정 (상승/하락)
    const borderColor = getPriceChangeColor(data);
    
    // Chart.js 설정
    const config = {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: '종가',
                    data: prices,
                    borderColor: borderColor,
                    backgroundColor: `${borderColor}20`,
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 3,
                    pointHoverRadius: 6,
                    yAxisID: 'y-price'
                },
                {
                    label: '거래량',
                    data: volumes,
                    type: 'bar',
                    backgroundColor: 'rgba(54, 162, 235, 0.3)',
                    borderColor: 'rgba(54, 162, 235, 0.8)',
                    borderWidth: 1,
                    yAxisID: 'y-volume',
                    order: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                },
                title: {
                    display: true,
                    text: `${stockCode} 차트`,
                    font: {
                        size: 16,
                        weight: 'bold'
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            
                            if (context.datasetIndex === 0) {
                                // 가격
                                label += formatPrice(context.parsed.y, country);
                            } else {
                                // 거래량
                                label += formatVolume(context.parsed.y);
                            }
                            
                            return label;
                        }
                    }
                },
                zoom: {
                    zoom: {
                        wheel: {
                            enabled: true
                        },
                        pinch: {
                            enabled: true
                        },
                        mode: 'x'
                    },
                    pan: {
                        enabled: true,
                        mode: 'x'
                    }
                }
            },
            scales: {
                'y-price': {
                    type: 'linear',
                    display: true,
                    position: 'left',
                    title: {
                        display: true,
                        text: country === 'US' ? 'Price (USD)' : '가격 (원)'
                    },
                    ticks: {
                        callback: function(value) {
                            return formatPrice(value, country);
                        }
                    }
                },
                'y-volume': {
                    type: 'linear',
                    display: true,
                    position: 'right',
                    title: {
                        display: true,
                        text: '거래량'
                    },
                    grid: {
                        drawOnChartArea: false
                    },
                    ticks: {
                        callback: function(value) {
                            return formatVolume(value);
                        }
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: '날짜'
                    }
                }
            }
        }
    };
    
    // 차트 생성
    stockChart = new Chart(ctx, config);
    
    console.log('✅ 차트 렌더링 완료');
}

// ========================================
// 4. 차트 기간 변경
// ========================================
function changeChartPeriod(period, stockCode, country) {
    console.log(`📊 차트 기간 변경: ${period}`);
    
    chartPeriod = period;
    
    // 버튼 활성화 상태 변경
    $('.chart-period-btn').removeClass('active');
    $(`#btn-${period}`).addClass('active');
    
    // 차트 다시 로드
    loadChartData(stockCode, country, period);
}

// ========================================
// 5. 더미 데이터 생성 (개발/테스트용)
// ========================================
function generateDummyChartData(days) {
    const data = [];
    let basePrice = 50000;
    let baseVolume = 1000000;
    
    for (let i = days; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        
        // 가격 랜덤 변동
        const change = (Math.random() - 0.5) * 2000;
        basePrice += change;
        
        // 거래량 랜덤 변동
        baseVolume = baseVolume * (0.8 + Math.random() * 0.4);
        
        data.push({
            date: date.toISOString().split('T')[0],
            open: basePrice - Math.random() * 500,
            high: basePrice + Math.random() * 1000,
            low: basePrice - Math.random() * 1000,
            close: basePrice,
            volume: Math.floor(baseVolume)
        });
    }
    
    return data;
}

// ========================================
// 6. 캔들스틱 차트 (고급 기능)
// ========================================
function renderCandlestickChart(data, stockCode, country) {
    const ctx = document.getElementById('stockChart');
    
    if (!ctx) return;
    
    // Chart.js에서는 캔들스틱을 직접 지원하지 않으므로,
    // 커스텀 차트 플러그인이나 TradingView 위젯 사용 권장
    
    // 여기서는 기본 차트로 대체
    renderChart(data, stockCode, country);
}

// ========================================
// 7. 유틸리티 함수
// ========================================
function formatChartDate(dateString) {
    const date = new Date(dateString);
    
    if (chartPeriod === 'daily') {
        // 일봉: MM/DD
        return `${date.getMonth() + 1}/${date.getDate()}`;
    } else if (chartPeriod === 'weekly') {
        // 주봉: MM/DD
        return `${date.getMonth() + 1}/${date.getDate()}`;
    } else {
        // 월봉: YYYY/MM
        return `${date.getFullYear()}/${date.getMonth() + 1}`;
    }
}

function getPriceChangeColor(data) {
    if (data.length < 2) return '#007bff';
    
    const firstPrice = data[0].close;
    const lastPrice = data[data.length - 1].close;
    
    if (lastPrice > firstPrice) {
        return '#dc3545'; // 상승 - 빨강
    } else if (lastPrice < lastPrice) {
        return '#007bff'; // 하락 - 파랑
    } else {
        return '#6c757d'; // 보합 - 회색
    }
}

function formatPrice(price, country) {
    if (!price) return '-';
    
    if (country === 'US') {
        return '$' + Number(price).toFixed(2);
    } else {
        return Number(price).toLocaleString() + '원';
    }
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

function showNoChartDataMessage() {
    const container = $('#chart-container');
    container.html(`
        <div class="no-chart-data">
            <i class="fas fa-chart-line"></i>
            <p>차트 데이터가 없습니다.</p>
        </div>
    `);
}

// ========================================
// 8. 차트 새로고침
// ========================================
function refreshChart(stockCode, country) {
    console.log('🔄 차트 새로고침...');
    initStockChart(stockCode, country);
}

// ========================================
// 9. 차트 내보내기 (이미지)
// ========================================
function exportChartAsImage() {
    if (!stockChart) {
        alert('차트가 없습니다.');
        return;
    }
    
    const url = stockChart.toBase64Image();
    const link = document.createElement('a');
    link.download = 'stock-chart.png';
    link.href = url;
    link.click();
    
    console.log('✅ 차트 이미지 다운로드');
}

// ========================================
// HTML에서 사용할 전역 함수 노출
// ========================================
window.initStockChart = initStockChart;
window.changeChartPeriod = changeChartPeriod;
window.refreshChart = refreshChart;
window.exportChartAsImage = exportChartAsImage;
