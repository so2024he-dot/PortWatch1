<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="app.name" /> - <spring:message code="app.title" /></title>
    
    <!-- Bootstrap 5.3.0 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    
    <!-- Custom CSS -->
    <link href="<c:url value='/resources/css/common.css' />" rel="stylesheet">
    
    <!-- jQuery 3.7.0 -->
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container-fluid">
            <a class="navbar-brand" href="<c:url value='/' />">
                <i class="bi bi-graph-up-arrow"></i> <spring:message code="app.name" />
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/' />">
                            <i class="bi bi-house-door"></i> <spring:message code="menu.home" />
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/stock/search' />">
                            <i class="bi bi-search"></i> <spring:message code="menu.stock.search" />
                        </a>
                    </li>
                    <c:if test="${not empty sessionScope.member}">
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/watchlist/list' />">
                                <i class="bi bi-star"></i> <spring:message code="menu.watchlist" />
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value='/portfolio/list' />">
                                <i class="bi bi-briefcase"></i> <spring:message code="menu.portfolio" />
                            </a>
                        </li>
                    </c:if>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/market/index' />">
                            <i class="bi bi-graph-up"></i> <spring:message code="menu.market" />
                        </a>
                    </li>
                </ul>
                
                <!-- User Menu -->
                <ul class="navbar-nav">
                    <c:choose>
                        <c:when test="${not empty sessionScope.member}">
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" 
                                   data-bs-toggle="dropdown">
                                    <i class="bi bi-person-circle"></i> ${sessionScope.member.memberName}
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='/member/mypage' />">
                                            <i class="bi bi-person"></i> <spring:message code="menu.mypage" />
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li>
                                        <a class="dropdown-item" href="<c:url value='/member/logout' />">
                                            <i class="bi bi-box-arrow-right"></i> <spring:message code="menu.logout" />
                                        </a>
                                    </li>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/member/login' />">
                                    <i class="bi bi-box-arrow-in-right"></i> <spring:message code="menu.login" />
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="<c:url value='/member/signup' />">
                                    <i class="bi bi-person-plus"></i> <spring:message code="menu.signup" />
                                </a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>
    
    <!-- Alert Messages -->
    <c:if test="${not empty message}">
        <div class="container mt-3">
            <div class="alert alert-${messageType != null ? messageType : 'info'} alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="container mt-3">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </div>
    </c:if>
    
    <!-- Main Content -->
    <div class="container-fluid mt-4">
