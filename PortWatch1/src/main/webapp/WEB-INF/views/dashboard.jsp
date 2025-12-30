<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>대시보드 - PortWatch</title>
    
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    
    <style>
        .portfolio-card {
            transition: transform 0.2s, box-shadow 0.2s;
            cursor: pointer;
            height: 100%;
        }
        
        .portfolio-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        
        .profit-positive {
            color: #dc3545;
        }
        
        .profit-negative {
            color: #0d6efd;
        }
        
        .summary-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
        }
        
        .stat-box {
            text-align: center;
            padding: 20px;
            background: rgba(255,255,255,0.1);
            border-radius: 10px;
            margin: 10px 0;
        }
        
        .stat-value {
            font-size: 2em;
            font-weight: bold;
        }
        
        .stat-label {
            font-size: 0.9em;
            opacity: 0.9;
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
                    <!-- ⭐ 홈 버튼 추가! -->
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/">
                            <i class="fas fa-home"></i> 홈
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/dashboard">
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/news/list">
                            <i class="fas fa-newspaper"></i> 뉴스
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/portfolio/list">
                            <i class="fas fa-briefcase"></i> 포트폴리오
                        </a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="userDropdown" 
                           role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user"></i> ${loginMember.name}
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
                </ul>
            </div>
            
          
        </div>
    </nav>
    
    <!-- 메인 컨텐츠 -->
    <div class="container mt-4">
        <!-- 페이지 헤더 -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>
                <i class="fas fa-th-large"></i> 대시보드
            </h2>
            <div>
                <!-- ✅ 수정: onclick 제거, id 추가 -->
                <button id="newPortfolioBtn" class="btn btn-primary">
                    <i class="fas fa-plus"></i> 새 포트폴리오
                </button>
            </div>
        </div>
        
        <!-- 요약 카드 -->
        <div class="summary-card">
            <div class="row">
                <div class="col-md-4">
                    <div class="stat-box">
                        <div class="stat-label">총 자산</div>
                        <div class="stat-value" id="totalAssets">0원</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-box">
                        <div class="stat-label">총 수익</div>
                        <div class="stat-value" id="totalProfit">0원</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stat-box">
                        <div class="stat-label">수익률</div>
                        <div class="stat-value" id="profitRate">0%</div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 포트폴리오 목록 -->
        <h4 class="mb-3">
            <i class="fas fa-briefcase"></i> 나의 포트폴리오
        </h4>
        
        <div id="portfolioListContainer">
            <div class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">로딩중...</span>
                </div>
                <p class="mt-3">포트폴리오를 불러오는 중입니다...</p>
            </div>
        </div>
        
        <!-- 차트 -->
        <div class="row mt-5">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">
                            <i class="fas fa-chart-line"></i> 포트폴리오 수익률 추이
                        </h5>
                        <canvas id="profitChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 포트폴리오 생성 모달 -->
    <div class="modal fade" id="createPortfolioModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-plus"></i> 새 포트폴리오 만들기
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="createPortfolioForm">
                        <div class="mb-3">
                            <label for="portfolioName" class="form-label">포트폴리오 이름</label>
                            <input type="text" class="form-control" id="portfolioName" 
                                   placeholder="예: 성장주 포트폴리오" required>
                        </div>
                        
                        <div class="mb-3">
                            <label for="stockCode" class="form-label">종목 코드</label>
                            <input type="text" class="form-control" id="stockCode" 
                                   placeholder="예: 005930" required>
                            <small class="text-muted">종목 코드를 입력하세요</small>
                        </div>
                        
                        <div class="mb-3">
                            <label for="quantity" class="form-label">수량</label>
                            <input type="number" class="form-control" id="quantity" 
                                   placeholder="예: 10" step="0.0001" min="0.0001" required>
                            <small class="text-muted">미국 주식은 소수점 가능 (예: 0.5주)</small>
                        </div>
                        
                        <div class="mb-3">
                            <label for="purchasePrice" class="form-label">매수 단가</label>
                            <input type="number" class="form-control" id="purchasePrice" 
                                   placeholder="예: 75000" step="0.01" min="0.01" required>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        취소
                    </button>
                    <!-- ✅ 수정: onclick 제거, id 추가 -->
                    <button type="button" id="savePortfolioBtn" class="btn btn-primary">
                        <i class="fas fa-save"></i> 저장
                    </button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         ✅ 수정된 JavaScript - 느슨한 결합 구조
         ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ -->
    <script>
    /* ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * PortfolioManager 객체 - 포트폴리오 관리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 
     * 핵심 수정:
     * ❌ 잘못된 방법: onclick="showCreateModal()"
     * ✅ 올바른 방법: addEventListener('click', handler)
     * 
     * 장점:
     * - HTML과 JavaScript 분리 (느슨한 결합)
     * - 유지보수 용이
     * - 이벤트 관리 일원화
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ */
    
    const PortfolioManager = {
        // ✅ 올바른 방법: Controller에서 전달받은 값 직접 사용
        contextPath: '${pageContext.request.contextPath}',  // ❌ ${this.contextPath} 아님!
        chart: null,
        
        /**
         * 초기화
         */
        init: function() {
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            console.log('📊 포트폴리오 매니저 초기화');
            console.log('  - contextPath:', this.contextPath);
            console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
            
            this.bindEvents();
            this.loadPortfolios();
            this.initChart();
        },
        
        /**
         * 이벤트 리스너 바인딩
         */
        bindEvents: function() {
            console.log('🔗 이벤트 리스너 바인딩');
            
            // ✅ 새 포트폴리오 버튼
            const newPortfolioBtn = document.getElementById('newPortfolioBtn');
            if (newPortfolioBtn) {
                newPortfolioBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    console.log('📝 새 포트폴리오 버튼 클릭!');
                    this.showCreateModal();
                });
                console.log('✅ 새 포트폴리오 버튼 이벤트 등록 완료');
            } else {
                console.error('❌ 새 포트폴리오 버튼을 찾을 수 없습니다!');
            }
            
            // ✅ 포트폴리오 저장 버튼
            const savePortfolioBtn = document.getElementById('savePortfolioBtn');
            if (savePortfolioBtn) {
                savePortfolioBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    console.log('💾 포트폴리오 저장 버튼 클릭!');
                    this.createPortfolio();
                });
                console.log('✅ 저장 버튼 이벤트 등록 완료');
            } else {
                console.error('❌ 저장 버튼을 찾을 수 없습니다!');
            }
            
            // Enter 키로 폼 제출
            const form = document.getElementById('createPortfolioForm');
            if (form) {
                form.addEventListener('submit', (e) => {
                    e.preventDefault();
                    this.createPortfolio();
                });
            }
        },
        
        /**
         * 모달창 표시
         */
        showCreateModal: function() {
            console.log('📋 모달창 표시');
            const modalElement = document.getElementById('createPortfolioModal');
            if (modalElement) {
                const modal = new bootstrap.Modal(modalElement);
                modal.show();
                console.log('✅ 모달창 열림');
            } else {
                console.error('❌ 모달 요소를 찾을 수 없습니다!');
                alert('모달창을 열 수 없습니다.');
            }
        },
        
        /**
         * 포트폴리오 생성
         */
        createPortfolio: function() {
            console.log('💼 포트폴리오 생성 시작');
            
            const portfolioName = document.getElementById('portfolioName').value.trim();
            const stockCode = document.getElementById('stockCode').value.trim();
            const quantity = document.getElementById('quantity').value.trim();
            const purchasePrice = document.getElementById('purchasePrice').value.trim();
            
            // 유효성 검사
            if (!portfolioName) {
                alert('포트폴리오 이름을 입력해주세요.');
                document.getElementById('portfolioName').focus();
                return;
            }
            
            if (!stockCode) {
                alert('종목 코드를 입력해주세요.');
                document.getElementById('stockCode').focus();
                return;
            }
            
            if (!quantity || parseFloat(quantity) <= 0) {
                alert('수량을 입력해주세요.');
                document.getElementById('quantity').focus();
                return;
            }
            
            if (!purchasePrice || parseFloat(purchasePrice) <= 0) {
                alert('매수 단가를 입력해주세요.');
                document.getElementById('purchasePrice').focus();
                return;
            }
            
            console.log('📝 포트폴리오 정보:', {
                portfolioName,
                stockCode,
                quantity,
                purchasePrice
            });
            
            // Fetch API로 AJAX 요청
            fetch(this.contextPath + '/portfolio/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: 'portfolioName=' + encodeURIComponent(portfolioName) +
                      '&stockCode=' + encodeURIComponent(stockCode) +
                      '&quantity=' + encodeURIComponent(quantity) +
                      '&purchasePrice=' + encodeURIComponent(purchasePrice)
            })
            .then(response => {
                console.log('📡 서버 응답:', response.status);
                if (!response.ok) {
                    throw new Error('포트폴리오 생성 실패');
                }
                return response.text();
            })
            .then(data => {
                console.log('✅ 포트폴리오 생성 완료!');
                
                // 모달 닫기
                const modal = bootstrap.Modal.getInstance(
                    document.getElementById('createPortfolioModal')
                );
                if (modal) {
                    modal.hide();
                }
                
                // 폼 초기화
                document.getElementById('portfolioName').value = '';
                document.getElementById('stockCode').value = '';
                document.getElementById('quantity').value = '';
                document.getElementById('purchasePrice').value = '';
                
                // 목록 새로고침
                this.loadPortfolios();
                
                alert('포트폴리오가 생성되었습니다!');
            })
            .catch(error => {
                console.error('❌ 포트폴리오 생성 실패:', error);
                alert('포트폴리오 생성에 실패했습니다.');
            });
        },
        
        /**
         * 포트폴리오 목록 로드
         */
        loadPortfolios: function() {
            console.log('📋 포트폴리오 목록 로드');
            
            fetch(this.contextPath + '/api/portfolio/list')
                .then(response => response.json())
                .then(data => {
                    console.log('✅ 포트폴리오 로드 완료:', data);
                    this.renderPortfolios(data.portfolios || []);
                    this.updateSummary(data.summary || {});
                })
                .catch(error => {
                    console.error('❌ 포트폴리오 로드 실패:', error);
                    this.showError();
                });
        },
        
        /**
         * 포트폴리오 목록 렌더링
         */
        renderPortfolios: function(portfolios) {
            const container = document.getElementById('portfolioListContainer');
            
            if (!portfolios || portfolios.length === 0) {
                container.innerHTML = `
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle"></i>
                        포트폴리오가 없습니다. 새 포트폴리오를 만들어보세요!
                    </div>
                `;
                return;
            }
            
            let html = '<div class="row">';
            
            portfolios.forEach(portfolio => {
                const profitClass = portfolio.profitLoss >= 0 ? 'profit-positive' : 'profit-negative';
                const profitIcon = portfolio.profitLoss >= 0 ? 'fa-arrow-up' : 'fa-arrow-down';
                
                html += `
                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="card portfolio-card" 
                             onclick="location.href='${pageContext.request.contextPath}/portfolio/detail/${portfolio.portfolioId}'">
                            <div class="card-body">
                                <h5 class="card-title">${portfolio.portfolioName}</h5>
                                <p class="text-muted">${portfolio.stockName} (${portfolio.stockCode})</p>
                                
                                <div class="mb-2">
                                    <small>보유 수량</small>
                                    <div class="fw-bold">${portfolio.quantity}주</div>
                                </div>
                                
                                <div class="mb-2">
                                    <small>평가 금액</small>
                                    <div class="fw-bold">${portfolio.totalValue.toLocaleString()}원</div>
                                </div>
                                
                                <div class="${profitClass}">
                                    <i class="fas ${profitIcon}"></i>
                                    ${portfolio.profitLoss.toLocaleString()}원
                                    (${portfolio.profitRate.toFixed(2)}%)
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            });
            
            html += '</div>';
            container.innerHTML = html;
        },
        
        /**
         * 요약 정보 업데이트
         */
        updateSummary: function(summary) {
            document.getElementById('totalAssets').textContent = 
                (summary.totalAssets || 0).toLocaleString() + '원';
            
            document.getElementById('totalProfit').textContent = 
                (summary.totalProfit || 0).toLocaleString() + '원';
            
            document.getElementById('profitRate').textContent = 
                (summary.profitRate || 0).toFixed(2) + '%';
        },
        
        /**
         * 에러 표시
         */
        showError: function() {
            const container = document.getElementById('portfolioListContainer');
            container.innerHTML = `
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-triangle"></i>
                    포트폴리오를 불러오는데 실패했습니다.
                </div>
            `;
        },
        
        /**
         * 차트 초기화
         */
        initChart: function() {
            const ctx = document.getElementById('profitChart');
            if (ctx) {
                this.chart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: [],
                        datasets: [{
                            label: '수익률 (%)',
                            data: [],
                            borderColor: '#667eea',
                            backgroundColor: 'rgba(102, 126, 234, 0.1)',
                            tension: 0.4
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                display: true,
                                position: 'top'
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            }
        }
    };
    
    // ✅ 페이지 로드 시 초기화
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📄 Dashboard 페이지 로드 완료');
        PortfolioManager.init();
    });
    </script>
</body>
</html>
