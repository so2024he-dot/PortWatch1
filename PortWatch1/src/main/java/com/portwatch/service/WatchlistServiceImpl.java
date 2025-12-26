package com.portwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.portwatch.domain.WatchlistVO;
import com.portwatch.domain.StockVO;
import com.portwatch.persistence.WatchlistDAO;
import com.portwatch.persistence.StockDAO;
import java.util.List;

/**
 * ✅ 관심종목 서비스 구현 - 완전 수정 버전
 * 
 * 수정 사항:
 * 1. memberId 타입 통일 (String)
 * 2. 중복 메서드 제거
 * 3. 느슨한 결합 유지
 * 
 * @author PortWatch
 * @version 1.0
 */
@Service
public class WatchlistServiceImpl implements WatchlistService {
    
    @Autowired
    private WatchlistDAO watchlistDAO;
    
    @Autowired
    private StockDAO stockDAO;
    
    /**
     * 관심종목 추가 (VO 객체 사용)
     */
    @Override
    @Transactional
    public void addWatchlist(WatchlistVO watchlist) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⭐ 관심종목 추가 요청");
        
        // null 체크
        if (watchlist == null) {
            throw new IllegalArgumentException("관심종목 정보가 없습니다.");
        }
        
        // 필수 값 체크
        if (watchlist.getMemberId() == null || watchlist.getMemberId().trim().isEmpty()) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        if (watchlist.getStockId() == null || watchlist.getStockId() <= 0) {
            throw new IllegalArgumentException("종목 ID가 유효하지 않습니다.");
        }
        
        System.out.println("  - 회원 ID: " + watchlist.getMemberId());
        System.out.println("  - 종목 ID: " + watchlist.getStockId());
        
        // ✅ 중복 확인
        boolean exists = checkExists(watchlist.getMemberId(), watchlist.getStockId());
        if (exists) {
            System.out.println("❌ 이미 관심종목에 등록되어 있습니다.");
            throw new Exception("이미 관심종목에 등록되어 있습니다.");
        }
        
        // ✅ 관심종목 추가
        watchlistDAO.insertWatchlist(watchlist);
        
        System.out.println("✅ 관심종목 추가 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * 관심종목 추가 (memberId, stockCode 사용)
     * Controller에서 호출
     */
    @Override
    @Transactional
    public void addToWatchlist(String memberId, String stockCode) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⭐ 관심종목 추가 요청 (stockCode 사용)");
        
        // 유효성 검사
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        if (stockCode == null || stockCode.trim().isEmpty()) {
            throw new IllegalArgumentException("종목 코드가 유효하지 않습니다.");
        }
        
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 코드: " + stockCode);
        
        // stockCode로 종목 찾기
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            System.out.println("❌ 존재하지 않는 종목: " + stockCode);
            throw new Exception("존재하지 않는 종목입니다: " + stockCode);
        }
        
        System.out.println("  - 종목 ID: " + stock.getStockId());
        System.out.println("  - 종목명: " + stock.getStockName());
        
        // WatchlistVO 생성
        WatchlistVO watchlist = new WatchlistVO();
        watchlist.setMemberId(memberId);
        watchlist.setStockId(stock.getStockId());
        
        // 추가
        addWatchlist(watchlist);
    }
    
    /**
     * 회원의 관심종목 목록 조회
     */
    @Override
    public List<WatchlistVO> getWatchlistByMember(String memberId) throws Exception {
        System.out.println("📋 관심종목 목록 조회: " + memberId);
        
        // 유효성 검사
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        List<WatchlistVO> watchlist = watchlistDAO.selectWatchlistByMember(memberId);
        
        System.out.println("✅ " + watchlist.size() + "개 관심종목 조회 완료");
        
        return watchlist;
    }
    
    /**
     * ✅ 관심종목 존재 여부 확인 (memberId, stockId 사용)
     * 
     * 주의: 중복 메서드 제거됨!
     */
    @Override
    public boolean checkExists(String memberId, Integer stockId) throws Exception {
        // 유효성 검사
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }
        
        if (stockId == null || stockId <= 0) {
            return false;
        }
        
        // ✅ DAO 호출
        int count = watchlistDAO.checkExists(memberId, stockId);
        
        return count > 0;
    }
    
    /**
     * 관심종목 존재 여부 확인 (memberId, stockCode 사용)
     * Controller에서 호출
     */
    @Override
    public boolean isInWatchlist(String memberId, String stockCode) throws Exception {
        // 유효성 검사
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }
        
        if (stockCode == null || stockCode.trim().isEmpty()) {
            return false;
        }
        
        // stockCode로 종목 찾기
        StockVO stock = stockDAO.selectByCode(stockCode);
        if (stock == null) {
            return false;
        }
        
        // stockId로 존재 여부 확인
        return checkExists(memberId, stock.getStockId());
    }
    
    /**
     * 관심종목 삭제 (memberId, stockId 사용)
     */
    @Override
    @Transactional
    public void deleteWatchlist(String memberId, Integer stockId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 관심종목 삭제 요청");
        
        // 유효성 검사
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("회원 ID가 유효하지 않습니다.");
        }
        
        if (stockId == null || stockId <= 0) {
            throw new IllegalArgumentException("종목 ID가 유효하지 않습니다.");
        }
        
        System.out.println("  - 회원 ID: " + memberId);
        System.out.println("  - 종목 ID: " + stockId);
        
        // ✅ 삭제
        watchlistDAO.deleteWatchlistByMemberAndStock(memberId, stockId);
        
        System.out.println("✅ 관심종목 삭제 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * 관심종목 삭제 (watchlistId 사용)
     * Controller에서 호출
     */
    @Override
    @Transactional
    public void removeFromWatchlist(Integer watchlistId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ 관심종목 삭제 요청 (ID 사용)");
        
        // 유효성 검사
        if (watchlistId == null || watchlistId <= 0) {
            throw new IllegalArgumentException("관심종목 ID가 유효하지 않습니다.");
        }
        
        System.out.println("  - 관심종목 ID: " + watchlistId);
        
        // ✅ watchlistId로 직접 삭제
        watchlistDAO.deleteWatchlistById(watchlistId);
        
        System.out.println("✅ 관심종목 삭제 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

	@Override
	public boolean checkWatchlist(String memberId, Integer stockId) {
		// TODO Auto-generated method stub
		return false;
	}
}
