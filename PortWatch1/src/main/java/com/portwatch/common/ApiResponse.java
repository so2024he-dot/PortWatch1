package com.portwatch.common;

/**
* ✅ 공통 API 응답 클래스
* 
* REST API 응답 통일을 위한 공통 응답 포맷
* 
* @author PortWatch
* @version 1.0
*/
public class ApiResponse<T> {

private boolean success;      // 성공 여부
private String message;       // 응답 메시지
private T data;               // 응답 데이터
private String errorCode;     // 에러 코드 (선택)
private Long timestamp;       // 응답 시간

// 기본 생성자
public ApiResponse() {
    this.timestamp = System.currentTimeMillis();
}

// 생성자 (성공)
public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.timestamp = System.currentTimeMillis();
}

// 생성자 (실패)
public ApiResponse(boolean success, String message, String errorCode) {
    this.success = success;
    this.message = message;
    this.errorCode = errorCode;
    this.timestamp = System.currentTimeMillis();
}

/**
 * 성공 응답 생성
 */
public static <T> ApiResponse<T> success(T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage("요청이 성공적으로 처리되었습니다.");
    response.setData(data);
    return response;
}

/**
 * 성공 응답 생성 (메시지 포함)
 */
public static <T> ApiResponse<T> success(String message, T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage(message);
    response.setData(data);
    return response;
}

/**
 * 실패 응답 생성
 */
public static <T> ApiResponse<T> error(String message) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage(message);
    return response;
}

/**
 * 실패 응답 생성 (에러 코드 포함)
 */
public static <T> ApiResponse<T> error(String message, String errorCode) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setMessage(message);
    response.setErrorCode(errorCode);
    return response;
}

// Getters and Setters
public boolean isSuccess() {
    return success;
}

public void setSuccess(boolean success) {
    this.success = success;
}

public String getMessage() {
    return message;
}

public void setMessage(String message) {
    this.message = message;
}

public T getData() {
    return data;
}

public void setData(T data) {
    this.data = data;
}

public String getErrorCode() {
    return errorCode;
}

public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
}

public Long getTimestamp() {
    return timestamp;
}

public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
}

@Override
public String toString() {
    return "ApiResponse{" +
            "success=" + success +
            ", message='" + message + '\'' +
            ", data=" + data +
            ", errorCode='" + errorCode + '\'' +
            ", timestamp=" + timestamp +
            '}';
}
}


