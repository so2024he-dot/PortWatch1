package com.portwatch.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ✅ JSP Functions 유틸리티 클래스
 * 
 * JSP에서 사용할 포맷팅 함수들을 제공
 * 
 * @author PortWatch
 * @version FINAL - Spring 5.0.7 + MySQL 8.0.33
 */
public class FormatUtil {
    
    /**
     * ✅ 가격 포맷팅 (천 단위 콤마)
     * 
     * @param value 가격 (BigDecimal, Double, Integer 등)
     * @return 포맷팅된 문자열 (예: "79,000")
     */
    public static String formatPrice(Object value) {
        if (value == null) {
            return "0";
        }
        
        try {
            BigDecimal bd;
            
            if (value instanceof BigDecimal) {
                bd = (BigDecimal) value;
            } else if (value instanceof Double) {
                bd = BigDecimal.valueOf((Double) value);
            } else if (value instanceof Integer) {
                bd = BigDecimal.valueOf((Integer) value);
            } else if (value instanceof Long) {
                bd = BigDecimal.valueOf((Long) value);
            } else if (value instanceof String) {
                bd = new BigDecimal((String) value);
            } else {
                bd = new BigDecimal(value.toString());
            }
            
            DecimalFormat df = new DecimalFormat("#,##0");
            return df.format(bd);
            
        } catch (Exception e) {
            return value.toString();
        }
    }
    
    /**
     * ✅ 숫자 포맷팅 (천 단위 콤마)
     * 
     * @param value 숫자
     * @return 포맷팅된 문자열 (예: "1,234,567")
     */
    public static String formatNumber(Object value) {
        return formatPrice(value);
    }
    
    /**
     * ✅ 퍼센트 포맷팅
     * 
     * @param value 비율 (0.15 → 15.0%)
     * @return 포맷팅된 문자열 (예: "+15.0%")
     */
    public static String formatPercent(Object value) {
        if (value == null) {
            return "0.0%";
        }
        
        try {
            BigDecimal bd;
            
            if (value instanceof BigDecimal) {
                bd = (BigDecimal) value;
            } else if (value instanceof Double) {
                bd = BigDecimal.valueOf((Double) value);
            } else if (value instanceof Integer) {
                bd = BigDecimal.valueOf((Integer) value);
            } else if (value instanceof Long) {
                bd = BigDecimal.valueOf((Long) value);
            } else if (value instanceof String) {
                bd = new BigDecimal((String) value);
            } else {
                bd = new BigDecimal(value.toString());
            }
            
            DecimalFormat df = new DecimalFormat("#0.00");
            String formatted = df.format(bd);
            
            // 양수면 + 부호 추가
            if (bd.compareTo(BigDecimal.ZERO) > 0) {
                return "+" + formatted + "%";
            } else {
                return formatted + "%";
            }
            
        } catch (Exception e) {
            return value.toString() + "%";
        }
    }
    
    /**
     * ✅ 날짜 포맷팅 (YYYY-MM-DD)
     * 
     * @param date 날짜
     * @return 포맷팅된 문자열 (예: "2025-12-27")
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        } catch (Exception e) {
            return date.toString();
        }
    }
    
    /**
     * ✅ 날짜시간 포맷팅 (YYYY-MM-DD HH:mm:ss)
     * 
     * @param date 날짜시간
     * @return 포맷팅된 문자열 (예: "2025-12-27 14:30:00")
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        } catch (Exception e) {
            return date.toString();
        }
    }
    
    /**
     * ✅ 소수점 포맷팅
     * 
     * @param value 숫자
     * @param decimals 소수점 자리수
     * @return 포맷팅된 문자열
     */
    public static String formatDecimal(Object value, int decimals) {
        if (value == null) {
            return "0";
        }
        
        try {
            BigDecimal bd;
            
            if (value instanceof BigDecimal) {
                bd = (BigDecimal) value;
            } else if (value instanceof Double) {
                bd = BigDecimal.valueOf((Double) value);
            } else if (value instanceof Integer) {
                bd = BigDecimal.valueOf((Integer) value);
            } else {
                bd = new BigDecimal(value.toString());
            }
            
            StringBuilder pattern = new StringBuilder("#,##0");
            if (decimals > 0) {
                pattern.append(".");
                for (int i = 0; i < decimals; i++) {
                    pattern.append("0");
                }
            }
            
            DecimalFormat df = new DecimalFormat(pattern.toString());
            return df.format(bd);
            
        } catch (Exception e) {
            return value.toString();
        }
    }
}
