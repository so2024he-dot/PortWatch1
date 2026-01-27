    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>세션 진단</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background: #f5f5f5;
        }
        .info-box {
            background: white;
            padding: 20px;
            margin: 20px 0;
            border-radius: 10px;
            border-left: 5px solid #667eea;
        }
        .success { border-left-color: #28a745; background: #d4edda; }
        .error { border-left-color: #dc3545; background: #f8d7da; }
        h2 { color: #667eea; }
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 10px 0;
        }
        th, td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background: #667eea;
            color: white;
        }
        .btn {
            background: #667eea;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin: 5px;
        }
        .btn:hover {
            background: #764ba2;
        }
    </style>
</head>
<body>
    <h1>🔍 세션 진단 페이지</h1>
    
    <%
        // 세션 정보 확인
        String memberId = (String) session.getAttribute("memberId");
        String userId = (String) session.getAttribute("userId");
        String loginId = (String) session.getAttribute("loginId");
        String memberName = (String) session.getAttribute("memberName");
        
        boolean hasAnySession = (memberId != null || userId != null || loginId != null);
    %>
    
    <div class="info-box <%= hasAnySession ? "success" : "error" %>">
        <h2><%= hasAnySession ? "✅ 세션 있음" : "❌ 세션 없음 - 로그인 필요" %></h2>
    </div>
    
    <div class="info-box">
        <h2>📋 현재 세션 정보</h2>
        <table>
            <tr>
                <th>세션 속성명</th>
                <th>값</th>
                <th>상태</th>
            </tr>
            <tr>
                <td><strong>memberId</strong></td>
                <td><%= memberId != null ? memberId : "(없음)" %></td>
                <td><%= memberId != null ? "✅" : "❌" %></td>
            </tr>
            <tr>
                <td><strong>userId</strong></td>
                <td><%= userId != null ? userId : "(없음)" %></td>
                <td><%= userId != null ? "✅" : "❌" %></td>
            </tr>
            <tr>
                <td><strong>loginId</strong></td>
                <td><%= loginId != null ? loginId : "(없음)" %></td>
                <td><%= loginId != null ? "✅" : "❌" %></td>
            </tr>
            <tr>
                <td><strong>memberName</strong></td>
                <td><%= memberName != null ? memberName : "(없음)" %></td>
                <td><%= memberName != null ? "✅" : "❌" %></td>
            </tr>
        </table>
    </div>
    
    <div class="info-box">
        <h2>🔧 해결 방법</h2>
        
        <% if (!hasAnySession) { %>
            <p><strong>❌ 문제:</strong> 세션이 없습니다. 로그인이 필요합니다.</p>
            <p><strong>✅ 해결:</strong></p>
            <ol>
                <li><a href="${pageContext.request.contextPath}/member/login" class="btn">로그인 페이지로 이동</a></li>
                <li>로그인 후 다시 이 페이지를 방문하세요</li>
            </ol>
        <% } else { %>
            <p><strong>✅ 세션이 있습니다!</strong></p>
            <p>관심종목 페이지로 이동 가능합니다.</p>
            <a href="${pageContext.request.contextPath}/watchlist/list" class="btn">관심종목 보기</a>
        <% } %>
    </div>
    
    <div class="info-box">
        <h2>🎯 WatchlistController가 찾는 세션</h2>
        <p><strong>현재 설정:</strong> <code>memberId</code> 또는 <code>userId</code></p>
        <p><strong>권장 설정:</strong> 로그인 시 <code>memberId</code>를 세션에 저장하세요</p>
        
        <% if (memberId != null) { %>
            <p style="color: green;">✅ <strong>memberId</strong>가 있습니다. 관심종목이 정상 작동합니다!</p>
        <% } else if (userId != null) { %>
            <p style="color: orange;">⚠️ <strong>userId</strong>만 있습니다. WatchlistController를 수정하거나 로그인 시 memberId도 저장하세요.</p>
        <% } else { %>
            <p style="color: red;">❌ <strong>memberId</strong>와 <strong>userId</strong> 모두 없습니다. 로그인이 필요합니다.</p>
        <% } %>
    </div>
    
    <div class="info-box">
        <h2>📊 전체 세션 속성 목록</h2>
        <table>
            <tr>
                <th>속성명</th>
                <th>값</th>
            </tr>
            <%
                java.util.Enumeration<String> attrs = session.getAttributeNames();
                boolean hasAttrs = false;
                while (attrs.hasMoreElements()) {
                    hasAttrs = true;
                    String attrName = attrs.nextElement();
                    Object attrValue = session.getAttribute(attrName);
            %>
                <tr>
                    <td><%= attrName %></td>
                    <td><%= attrValue %></td>
                </tr>
            <%
                }
                if (!hasAttrs) {
            %>
                <tr>
                    <td colspan="2" style="text-align: center; color: #999;">
                        세션에 저장된 속성이 없습니다.
                    </td>
                </tr>
            <%
                }
            %>
        </table>
    </div>
    
    <div style="text-align: center; margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/" class="btn">홈으로</a>
        <a href="${pageContext.request.contextPath}/member/login" class="btn">로그인</a>
    </div>
</body>
</html>

    
