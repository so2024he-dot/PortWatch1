/* PortWatch Common JavaScript - Spring 5.0.7 */

// 숫자 포맷팅 (천단위 콤마)
function formatNumber(num) {
    if (num === null || num === undefined) return '0';
    return Number(num).toLocaleString('ko-KR');
}

// 가격 변동 클래스 반환
function getPriceChangeClass(value) {
    if (value > 0) return 'price-up';
    if (value < 0) return 'price-down';
    return 'price-neutral';
}

// 가격 변동 포맷 (부호 포함)
function formatPriceChange(value) {
    if (value === null || value === undefined) return '0';
    const num = Number(value);
    return (num >= 0 ? '+' : '') + num.toLocaleString('ko-KR');
}

// 비율 포맷 (%)
function formatRate(value) {
    if (value === null || value === undefined) return '0.00';
    const num = Number(value);
    return (num >= 0 ? '+' : '') + num.toFixed(2);
}

// 날짜 포맷 (YYYY-MM-DD)
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
}

// 날짜시간 포맷 (YYYY-MM-DD HH:mm:ss)
function formatDateTime(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().replace('T', ' ').substring(0, 19);
}

// 로딩 표시
function showLoading(selector) {
    $(selector).html('<div class="spinner-container"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div></div>');
}

// 에러 메시지 표시
function showError(message, selector) {
    const html = '<div class="alert alert-danger alert-dismissible fade show" role="alert">' +
                message +
                '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
                '</div>';
    if (selector) {
        $(selector).html(html);
    } else {
        $('.container').first().prepend(html);
    }
}

// 빈 메시지 표시
function showEmptyMessage(message, selector) {
    $(selector).html('<div class="text-center text-muted py-5">' + message + '</div>');
}

// AJAX 에러 핸들링
function handleAjaxError(xhr, status, error) {
    console.error('AJAX Error:', status, error);
    if (xhr.status === 401) {
        alert('로그인이 필요합니다.');
        location.href = '/member/login';
    } else if (xhr.status === 403) {
        alert('접근 권한이 없습니다.');
    } else if (xhr.status === 500) {
        alert('서버 오류가 발생했습니다.');
    } else {
        alert('요청 처리 중 오류가 발생했습니다.');
    }
}

// 확인 다이얼로그
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// Toast 메시지 표시
function showToast(message, type = 'success') {
    const bgClass = type === 'success' ? 'bg-success' : type === 'error' ? 'bg-danger' : 'bg-info';
    const toastHtml = `
        <div class="toast align-items-center text-white ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    let toastContainer = $('.toast-container');
    if (toastContainer.length === 0) {
        toastContainer = $('<div class="toast-container position-fixed bottom-0 end-0 p-3"></div>');
        $('body').append(toastContainer);
    }
    
    const toastElement = $(toastHtml);
    toastContainer.append(toastElement);
    
    const toast = new bootstrap.Toast(toastElement[0]);
    toast.show();
    
    toastElement.on('hidden.bs.toast', function() {
        $(this).remove();
    });
}

// LocalStorage 헬퍼
const LocalStorage = {
    set: function(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.error('LocalStorage set error:', e);
        }
    },
    get: function(key) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (e) {
            console.error('LocalStorage get error:', e);
            return null;
        }
    },
    remove: function(key) {
        try {
            localStorage.removeItem(key);
        } catch (e) {
            console.error('LocalStorage remove error:', e);
        }
    }
};

// SessionStorage 헬퍼
const SessionStorage = {
    set: function(key, value) {
        try {
            sessionStorage.setItem(key, JSON.stringify(value));
        } catch (e) {
            console.error('SessionStorage set error:', e);
        }
    },
    get: function(key) {
        try {
            const item = sessionStorage.getItem(key);
            return item ? JSON.parse(item) : null;
        } catch (e) {
            console.error('SessionStorage get error:', e);
            return null;
        }
    },
    remove: function(key) {
        try {
            sessionStorage.removeItem(key);
        } catch (e) {
            console.error('SessionStorage remove error:', e);
        }
    }
};

// Debounce 함수
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Throttle 함수
function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// 페이지 로드 시 초기화
$(document).ready(function() {
    // Bootstrap Tooltip 초기화
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Bootstrap Popover 초기화
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function(popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
});
