<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
    </div> <!-- Close container-fluid -->
    
    <!-- Footer -->
    <footer class="bg-light text-center text-lg-start mt-5">
        <div class="container p-4">
            <div class="row">
                <div class="col-lg-6 col-md-12 mb-4 mb-md-0">
                    <h5 class="text-uppercase"><spring:message code="app.name" /></h5>
                    <p><spring:message code="app.description" /></p>
                </div>
                
                <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
                    <h5 class="text-uppercase">Links</h5>
                    <ul class="list-unstyled mb-0">
                        <li><a href="<c:url value='/stock/search' />" class="text-dark"><spring:message code="menu.stock.search" /></a></li>
                        <li><a href="<c:url value='/market/index' />" class="text-dark"><spring:message code="menu.market" /></a></li>
                    </ul>
                </div>
                
                <div class="col-lg-3 col-md-6 mb-4 mb-md-0">
                    <h5 class="text-uppercase">Contact</h5>
                    <ul class="list-unstyled mb-0">
                        <li><i class="bi bi-envelope"></i> support@portwatch.com</li>
                        <li><i class="bi bi-telephone"></i> 02-1234-5678</li>
                    </ul>
                </div>
            </div>
        </div>
        
        <div class="text-center p-3 bg-dark text-white">
            © 2024 <spring:message code="app.name" />. All rights reserved.
        </div>
    </footer>
    
    <!-- Bootstrap Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Custom JS -->
    <script src="<c:url value='/resources/js/common.js' />"></script>
</body>
</html>
