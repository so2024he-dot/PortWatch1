<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

    </div>
    <!-- End Main Container -->
    
    <!-- Footer -->
    <footer class="mt-5" style="background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);">
        <div class="container-fluid px-3 px-lg-5 py-4">
            <div class="row">
                <div class="col-md-6 text-center text-md-start mb-3 mb-md-0">
                    <h5 style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;">
                        <i class="bi bi-graph-up-arrow"></i> PortWatch
                    </h5>
                    <p class="text-muted mb-0 small">스마트한 포트폴리오 관리 시스템</p>
                </div>
                <div class="col-md-6 text-center text-md-end">
                    <p class="text-muted mb-2 small">
                        <i class="bi bi-envelope"></i> contact@portwatch.com
                    </p>
                    <p class="text-muted mb-0 small">
                        © 2025 PortWatch. All rights reserved.
                    </p>
                </div>
            </div>
        </div>
    </footer>
    
    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom Scripts -->
    <script>
        // Set active nav link
        $(document).ready(function() {
            const currentPath = window.location.pathname;
            $('.nav-link').each(function() {
                if ($(this).attr('href') && currentPath.includes($(this).attr('href').split('/').pop())) {
                    $(this).addClass('active');
                }
            });
            
            // Smooth scroll
            $('a[href^="#"]').on('click', function(e) {
                e.preventDefault();
                const target = $(this.getAttribute('href'));
                if (target.length) {
                    $('html, body').stop().animate({
                        scrollTop: target.offset().top - 100
                    }, 500);
                }
            });
            
            // Add animation to cards
            $('.card, .summary-card').each(function(index) {
                $(this).css('animation-delay', (index * 0.1) + 's');
                $(this).addClass('animate-fade-in');
            });
        });
        
        // Format number with comma
        function formatNumber(num) {
            return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }
        
        // Format currency
        function formatCurrency(num) {
            return formatNumber(num) + '원';
        }
        
        // Format percentage
        function formatPercentage(num) {
            return (num >= 0 ? '+' : '') + num.toFixed(2) + '%';
        }
        
        // Show toast message
        function showToast(message, type = 'success') {
            const toast = `
                <div class="toast align-items-center text-white bg-${type} border-0 position-fixed top-0 end-0 m-3" role="alert" style="z-index: 9999;">
                    <div class="d-flex">
                        <div class="toast-body">
                            ${message}
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                    </div>
                </div>
            `;
            $('body').append(toast);
            const toastEl = $('.toast').last()[0];
            const bsToast = new bootstrap.Toast(toastEl, { delay: 3000 });
            bsToast.show();
            setTimeout(() => $(toastEl).remove(), 4000);
        }
    </script>
</body>
</html>
