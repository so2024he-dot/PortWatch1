<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PortWatch - 주식 데이터 업데이트</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 50px 0;
        }
        .container {
            max-width: 900px;
        }
        .card {
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            border: none;
        }
        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 15px 15px 0 0 !important;
            padding: 25px;
        }
        .btn-update {
            border-radius: 10px;
            padding: 15px 30px;
            font-size: 16px;
            font-weight: bold;
            transition: all 0.3s;
        }
        .btn-update:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }
        .btn-korea {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            border: none;
            color: white;
        }
        .btn-usa {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            border: none;
            color: white;
        }
        .btn-all {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            border: none;
            color: white;
        }
        .status-box {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            margin-top: 20px;
            display: none;
        }
        .spinner-border {
            width: 1.5rem;
            height: 1.5rem;
            margin-right: 10px;
        }
        .alert {
            border-radius: 10px;
        }
        .info-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-top: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .badge-custom {
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 14px;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- 헤더 -->
        <div class="card">
            <div class="card-header text-center">
                <h2 class="mb-0">
                    <i class="bi bi-graph-up-arrow me-2"></i>
                    주식 데이터 자동 업데이트
                </h2>
                <p class="mb-0 mt-2">한국 & 미국 시가총액 100대 기업 실시간 크롤링</p>
            </div>
            <div class="card-body p-4">
                <!-- 업데이트 버튼 -->
                <div class="row g-3 mb-4">
                    <div class="col-md-4">
                        <button class="btn btn-korea btn-update w-100" onclick="updateKoreaStocks()">
                            <i class="bi bi-flag-fill me-2"></i>
                            한국 주식 업데이트
                            <div class="small mt-1">KOSPI/KOSDAQ TOP 100</div>
                        </button>
                    </div>
                    <div class="col-md-4">
                        <button class="btn btn-usa btn-update w-100" onclick="updateUSStocks()">
                            <i class="bi bi-flag-usa me-2"></i>
                            미국 주식 업데이트
                            <div class="small mt-1">S&P 500 TOP 100</div>
                        </button>
                    </div>
                    <div class="col-md-4">
                        <button class="btn btn-all btn-update w-100" onclick="updateAllStocks()">
                            <i class="bi bi-globe me-2"></i>
                            전체 업데이트
                            <div class="small mt-1">한국 + 미국 200개</div>
                        </button>
                    </div>
                </div>

                <!-- 상태 표시 -->
                <div id="statusBox" class="status-box">
                    <div id="loadingSpinner" class="text-center" style="display: none;">
                        <div class="spinner-border text-primary" role="status"></div>
                        <span class="ms-2">데이터를 가져오는 중입니다...</span>
                    </div>
                    <div id="resultMessage"></div>
                </div>

                <!-- 정보 카드 -->
                <div class="info-card">
                    <h5 class="mb-3">
                        <i class="bi bi-info-circle me-2"></i>
                        자동 업데이트 스케줄
                    </h5>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <div class="d-flex align-items-center">
                                <i class="bi bi-clock text-primary fs-4 me-3"></i>
                                <div>
                                    <strong>오전 9시</strong>
                                    <div class="small text-muted">전체 업데이트 (한국 + 미국)</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="d-flex align-items-center">
                                <i class="bi bi-clock text-success fs-4 me-3"></i>
                                <div>
                                    <strong>오후 6시</strong>
                                    <div class="small text-muted">전체 업데이트 (한국 + 미국)</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="d-flex align-items-center">
                                <i class="bi bi-arrow-repeat text-danger fs-4 me-3"></i>
                                <div>
                                    <strong>평일 9시~15시</strong>
                                    <div class="small text-muted">한국 주식 매시간</div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <div class="d-flex align-items-center">
                                <i class="bi bi-arrow-repeat text-info fs-4 me-3"></i>
                                <div>
                                    <strong>평일 23시~06시</strong>
                                    <div class="small text-muted">미국 주식 30분마다</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 통계 정보 -->
                <div class="row mt-4">
                    <div class="col-md-4 text-center">
                        <div class="badge badge-custom bg-danger">
                            <i class="bi bi-flag-fill me-1"></i>
                            한국: 100개 종목
                        </div>
                    </div>
                    <div class="col-md-4 text-center">
                        <div class="badge badge-custom bg-primary">
                            <i class="bi bi-flag-usa me-1"></i>
                            미국: 100개 종목
                        </div>
                    </div>
                    <div class="col-md-4 text-center">
                        <div class="badge badge-custom bg-success">
                            <i class="bi bi-check-circle me-1"></i>
                            총 200개 종목
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const API_BASE = '/api/crawler';

        // 한국 주식 업데이트
        function updateKoreaStocks() {
            updateStocks('/korea/update', '한국');
        }

        // 미국 주식 업데이트
        function updateUSStocks() {
            updateStocks('/us/update', '미국');
        }

        // 전체 업데이트
        function updateAllStocks() {
            updateStocks('/all/update', '전체');
        }

        // 주식 업데이트 공통 함수
        function updateStocks(endpoint, label) {
            const statusBox = document.getElementById('statusBox');
            const loadingSpinner = document.getElementById('loadingSpinner');
            const resultMessage = document.getElementById('resultMessage');

            // 상태 박스 표시
            statusBox.style.display = 'block';
            loadingSpinner.style.display = 'block';
            resultMessage.innerHTML = '';

            // API 호출
            fetch(API_BASE + endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                loadingSpinner.style.display = 'none';

                if (data.success) {
                    let message = `
                        <div class="alert alert-success">
                            <h5 class="alert-heading">
                                <i class="bi bi-check-circle me-2"></i>
                                ${label} 주식 업데이트 성공!
                            </h5>
                            <hr>
                    `;

                    if (data.totalCount) {
                        message += `
                            <p class="mb-1"><strong>한국:</strong> ${data.koreaCount}개</p>
                            <p class="mb-1"><strong>미국:</strong> ${data.usCount}개</p>
                            <p class="mb-1"><strong>전체:</strong> ${data.totalCount}개</p>
                        `;
                    } else {
                        message += `<p class="mb-1"><strong>업데이트:</strong> ${data.updatedCount}개</p>`;
                    }

                    message += `
                            <p class="mb-0"><strong>소요 시간:</strong> ${data.duration}</p>
                        </div>
                    `;

                    resultMessage.innerHTML = message;
                } else {
                    resultMessage.innerHTML = `
                        <div class="alert alert-danger">
                            <h5 class="alert-heading">
                                <i class="bi bi-x-circle me-2"></i>
                                업데이트 실패
                            </h5>
                            <p class="mb-0">${data.message}</p>
                        </div>
                    `;
                }
            })
            .catch(error => {
                loadingSpinner.style.display = 'none';
                resultMessage.innerHTML = `
                    <div class="alert alert-danger">
                        <h5 class="alert-heading">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            오류 발생
                        </h5>
                        <p class="mb-0">서버와 연결할 수 없습니다: ${error.message}</p>
                    </div>
                `;
            });
        }
    </script>
</body>
</html>
