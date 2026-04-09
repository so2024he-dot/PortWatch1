<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 프로필 - PortWatch</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .profile-container {
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .profile-header {
            background: white;
            border-radius: 20px;
            padding: 40px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
        }
        
        .profile-avatar {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 48px;
            color: white;
            margin-bottom: 20px;
        }
        
        .profile-name {
            font-size: 32px;
            font-weight: 700;
            color: #2d3748;
            margin-bottom: 10px;
        }
        
        .profile-email {
            font-size: 18px;
            color: #718096;
            margin-bottom: 5px;
        }
        
        .profile-id {
            font-size: 14px;
            color: #a0aec0;
            font-family: monospace;
        }
        
        .profile-content {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
        }
        
        @media (max-width: 768px) {
            .profile-content {
                grid-template-columns: 1fr;
            }
        }
        
        .profile-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
        }
        
        .card-title {
            font-size: 24px;
            font-weight: 700;
            color: #2d3748;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .card-title i {
            font-size: 28px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-label {
            display: block;
            font-size: 14px;
            font-weight: 600;
            color: #4a5568;
            margin-bottom: 8px;
        }
        
        .form-control {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e2e8f0;
            border-radius: 10px;
            font-size: 16px;
            transition: all 0.3s ease;
        }
        
        .form-control:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .form-control:disabled {
            background: #f7fafc;
            cursor: not-allowed;
        }
        
        .btn-primary {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
        }
        
        .btn-secondary {
            width: 100%;
            padding: 14px;
            background: #e2e8f0;
            color: #4a5568;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-top: 10px;
        }
        
        .btn-secondary:hover {
            background: #cbd5e0;
        }
        
        .info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #e2e8f0;
        }
        
        .info-item:last-child {
            border-bottom: none;
        }
        
        .info-label {
            font-size: 14px;
            font-weight: 600;
            color: #718096;
        }
        
        .info-value {
            font-size: 16px;
            font-weight: 600;
            color: #2d3748;
        }
        
        .badge {
            display: inline-block;
            padding: 6px 12px;
            background: #48bb78;
            color: white;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .badge.admin {
            background: #ed8936;
        }
        
        .alert {
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            display: none;
        }
        
        .alert-success {
            background: #c6f6d5;
            color: #22543d;
            border: 1px solid #9ae6b4;
        }
        
        .alert-danger {
            background: #fed7d7;
            color: #742a2a;
            border: 1px solid #fc8181;
        }
        
        .back-btn {
            position: fixed;
            top: 30px;
            left: 30px;
            width: 50px;
            height: 50px;
            background: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: #667eea;
            text-decoration: none;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
        }
        
        .back-btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
    </style>
</head>
<body>
    <!-- 뒤로가기 버튼 -->
    <a href="${pageContext.request.contextPath}/dashboard" class="back-btn">
        <i class="fas fa-arrow-left"></i>
    </a>
    
    <div class="profile-container">
        <!-- 프로필 헤더 -->
        <div class="profile-header">
            <div class="d-flex align-items-center">
                <div class="profile-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <div class="ms-4">
                    <h1 class="profile-name">${member.memberName}</h1>
                    <p class="profile-email">
                        <i class="fas fa-envelope me-2"></i>${member.memberEmail}
                    </p>
                    <p class="profile-id">
                        <i class="fas fa-id-card me-2"></i>ID: ${member.memberId}
                    </p>
                    <div class="mt-2">
                        <c:choose>
                            <c:when test="${member.memberRole == 'ADMIN'}">
                                <span class="badge admin">관리자</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge">일반 회원</span>
                            </c:otherwise>
                        </c:choose>
                        <span class="badge ms-2">
                            <c:choose>
                                <c:when test="${member.memberStatus == 'ACTIVE'}">활성</c:when>
                                <c:otherwise>비활성</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 프로필 컨텐츠 -->
        <div class="profile-content">
            <!-- 회원 정보 수정 -->
            <div class="profile-card">
                <h2 class="card-title">
                    <i class="fas fa-user-edit"></i>
                    회원 정보 수정
                </h2>
                
                <div id="updateAlert" class="alert"></div>
                
                <form id="profileUpdateForm">
                    <div class="form-group">
                        <label class="form-label">이름</label>
                        <input type="text" class="form-control" id="memberName" 
                               name="memberName" value="${member.memberName}" required>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">이메일</label>
                        <input type="email" class="form-control" 
                               value="${member.memberEmail}" disabled>
                        <small class="text-muted">이메일은 변경할 수 없습니다.</small>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">전화번호</label>
                        <input type="tel" class="form-control" id="memberPhone" 
                               name="memberPhone" value="${member.memberPhone}" 
                               placeholder="010-1234-5678">
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">주소</label>
                        <input type="text" class="form-control" id="memberAddress" 
                               name="memberAddress" value="${member.memberAddress}" 
                               placeholder="주소를 입력하세요">
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">성별</label>
                        <select class="form-control" id="memberGender" name="memberGender">
                            <option value="">선택 안 함</option>
                            <option value="MALE" ${member.memberGender == 'MALE' ? 'selected' : ''}>남성</option>
                            <option value="FEMALE" ${member.memberGender == 'FEMALE' ? 'selected' : ''}>여성</option>
                        </select>
                    </div>
                    
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-save me-2"></i>정보 저장
                    </button>
                </form>
            </div>
            
            <!-- 비밀번호 변경 -->
            <div class="profile-card">
                <h2 class="card-title">
                    <i class="fas fa-key"></i>
                    비밀번호 변경
                </h2>
                
                <div id="passwordAlert" class="alert"></div>
                
                <form id="passwordChangeForm">
                    <div class="form-group">
                        <label class="form-label">현재 비밀번호</label>
                        <input type="password" class="form-control" id="currentPassword" 
                               name="currentPassword" required>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">새 비밀번호</label>
                        <input type="password" class="form-control" id="newPassword" 
                               name="newPassword" minlength="4" required>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">새 비밀번호 확인</label>
                        <input type="password" class="form-control" id="confirmPassword" 
                               name="confirmPassword" minlength="4" required>
                    </div>
                    
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-lock me-2"></i>비밀번호 변경
                    </button>
                </form>
                
                <!-- 계정 정보 -->
                <div class="mt-4 pt-4" style="border-top: 2px solid #e2e8f0;">
                    <h3 class="card-title">
                        <i class="fas fa-info-circle"></i>
                        계정 정보
                    </h3>
                    <div class="info-item">
                        <span class="info-label">가입일</span>
                        <span class="info-value">
                            <fmt:formatDate value="${member.createdAt}" pattern="yyyy-MM-dd" />
                        </span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">최종 수정일</span>
                        <span class="info-value">
                            <fmt:formatDate value="${member.updatedAt}" pattern="yyyy-MM-dd HH:mm" />
                        </span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">회원 등급</span>
                        <span class="info-value">
                            <c:choose>
                                <c:when test="${member.memberRole == 'ADMIN'}">관리자</c:when>
                                <c:otherwise>일반 회원</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // 프로필 정보 수정
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        $('#profileUpdateForm').on('submit', function(e) {
            e.preventDefault();
            
            const data = {
                memberId: '${member.memberId}',
                memberName: $('#memberName').val().trim(),
                memberPhone: $('#memberPhone').val().trim(),
                memberAddress: $('#memberAddress').val().trim(),
                memberGender: $('#memberGender').val()
            };
            
            if (!data.memberName) {
                showAlert('updateAlert', 'danger', '이름을 입력해주세요.');
                return;
            }
            
            $.ajax({
                url: '${pageContext.request.contextPath}/member/profile/update',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function(response) {
                    if (response.success) {
                        showAlert('updateAlert', 'success', '프로필이 수정되었습니다.');
                        
                        // 페이지 새로고침 (3초 후)
                        setTimeout(function() {
                            location.reload();
                        }, 1500);
                    } else {
                        showAlert('updateAlert', 'danger', response.message);
                    }
                },
                error: function(xhr) {
                    const response = xhr.responseJSON;
                    showAlert('updateAlert', 'danger', 
                        response ? response.message : '프로필 수정 중 오류가 발생했습니다.');
                }
            });
        });
        
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // 비밀번호 변경
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        $('#passwordChangeForm').on('submit', function(e) {
            e.preventDefault();
            
            const currentPassword = $('#currentPassword').val();
            const newPassword = $('#newPassword').val();
            const confirmPassword = $('#confirmPassword').val();
            
            // 유효성 검사
            if (newPassword.length < 4) {
                showAlert('passwordAlert', 'danger', '새 비밀번호는 4자 이상이어야 합니다.');
                return;
            }
            
            if (newPassword !== confirmPassword) {
                showAlert('passwordAlert', 'danger', '새 비밀번호가 일치하지 않습니다.');
                return;
            }
            
            if (currentPassword === newPassword) {
                showAlert('passwordAlert', 'danger', '새 비밀번호는 현재 비밀번호와 달라야 합니다.');
                return;
            }
            
            $.ajax({
                url: '${pageContext.request.contextPath}/member/profile/password',
                method: 'POST',
                data: {
                    currentPassword: currentPassword,
                    newPassword: newPassword
                },
                success: function(response) {
                    if (response.success) {
                        showAlert('passwordAlert', 'success', '비밀번호가 변경되었습니다.');
                        
                        // 폼 초기화
                        $('#passwordChangeForm')[0].reset();
                    } else {
                        showAlert('passwordAlert', 'danger', response.message);
                    }
                },
                error: function(xhr) {
                    const response = xhr.responseJSON;
                    showAlert('passwordAlert', 'danger', 
                        response ? response.message : '비밀번호 변경 중 오류가 발생했습니다.');
                }
            });
        });
        
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        // 알림 표시 함수
        // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
        function showAlert(alertId, type, message) {
            const alertElement = $('#' + alertId);
            alertElement.removeClass('alert-success alert-danger');
            alertElement.addClass('alert-' + type);
            alertElement.text(message);
            alertElement.fadeIn();
            
            setTimeout(function() {
                alertElement.fadeOut();
            }, 5000);
        }
    </script>
</body>
</html>
