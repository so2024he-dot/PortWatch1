package com.portwatch.domain;

import java.sql.Timestamp;

/**
 * 관심종목 VO
 * 
 * DB 테이블: WATCHLIST
 * - watchlist_id (PK)
 * - member_id (FK -> MEMBER)
 * - stock_id (FK -> STOCK)
 * - added_at (등록일시)
 */
public class WatchlistVO {
    
    private Integer watchlistId;  // 관심종목 ID (PK)
    private Integer memberId;     // 회원 ID (FK)
    private Integer stockId;      // 종목 ID (FK)
    private Timestamp addedAt;    // 등록일시
    
    // Constructors
    public WatchlistVO() {
    }
    
    public WatchlistVO(Integer memberId, Integer stockId) {
        this.memberId = memberId;
        this.stockId = stockId;
    }
    
    // Getters and Setters
    public Integer getWatchlistId() {
        return watchlistId;
    }
    
    public void setWatchlistId(Integer watchlistId) {
        this.watchlistId = watchlistId;
    }
    
    public Integer getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
    
    public Integer getStockId() {
        return stockId;
    }
    
    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }
    
    public Timestamp getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }
    
    @Override
    public String toString() {
        return "WatchlistVO{" +
                "watchlistId=" + watchlistId +
                ", memberId=" + memberId +
                ", stockId=" + stockId +
                ", addedAt=" + addedAt +
                '}';
    }
}
