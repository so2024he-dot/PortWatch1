package com.portwatch.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.portwatch.domain.PortfolioVO;

/**
 * 포트폴리오 DAO 구현체
 * 
 * ✅ 이전 작동 버전 기반
 * ✅ 모든 메서드 완전 구현
 * 
 * @author PortWatch
 * @version 5.0 (Spring 5.0.7 + MySQL 8.0)
 */
@Repository
public class PortfolioDAOImpl implements PortfolioDAO {
    
    private static final String NAMESPACE = "com.portwatch.persistence.PortfolioDAO";
    
    @Autowired
    private SqlSession sqlSession;
    
    @Override
    public void insertPortfolio(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💾 DAO: 포트폴리오 추가");
        System.out.println("  - memberId: " + portfolio.getMemberId());
        System.out.println("  - stockId: " + portfolio.getStockId());
        System.out.println("  - quantity: " + portfolio.getQuantity());
        System.out.println("  - avgPurchasePrice: " + portfolio.getAvgPurchasePrice());
        
        sqlSession.insert(NAMESPACE + ".insertPortfolio", portfolio);
        
        System.out.println("✅ DAO: 추가 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    public List<PortfolioVO> selectPortfolioByMember(int memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 DAO: 회원별 포트폴리오 목록 조회");
        System.out.println("  - memberId: " + memberId);
        
        List<PortfolioVO> result = sqlSession.selectList(NAMESPACE + ".selectPortfolioByMember", memberId);
        
        System.out.println("✅ DAO: " + (result != null ? result.size() : 0) + "개 조회 완료!");
        if (result != null && !result.isEmpty()) {
            for (PortfolioVO portfolio : result) {
                System.out.println("  - " + portfolio.getStockName() + " (" + portfolio.getStockCode() + "): " 
                                 + portfolio.getQuantity() + "주");
            }
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return result;
    }
    
    @Override
    public PortfolioVO selectPortfolioById(long portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 DAO: 포트폴리오 ID로 조회");
        System.out.println("  - portfolioId: " + portfolioId);
        
        PortfolioVO result = sqlSession.selectOne(NAMESPACE + ".selectPortfolioById", portfolioId);
        
        if (result != null) {
            System.out.println("✅ DAO: 조회 성공!");
            System.out.println("  - " + result.getStockName() + " (" + result.getStockCode() + ")");
        } else {
            System.out.println("⚠️ DAO: 해당 포트폴리오 없음");
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return result;
    }
    
    @Override
    public int checkDuplicate(Map<String, Object> params) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 DAO: 중복 확인");
        System.out.println("  - params: " + params);
        
        int count = sqlSession.selectOne(NAMESPACE + ".checkDuplicate", params);
        
        System.out.println("✅ DAO: 중복 개수 = " + count);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return count;
    }
    
    @Override
    public void updatePortfolio(PortfolioVO portfolio) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔄 DAO: 포트폴리오 수정");
        System.out.println("  - portfolioId: " + portfolio.getPortfolioId());
        System.out.println("  - quantity: " + portfolio.getQuantity());
        System.out.println("  - avgPurchasePrice: " + portfolio.getAvgPurchasePrice());
        
        sqlSession.update(NAMESPACE + ".updatePortfolio", portfolio);
        
        System.out.println("✅ DAO: 수정 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    public void deletePortfolio(long portfolioId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗑️ DAO: 포트폴리오 삭제");
        System.out.println("  - portfolioId: " + portfolioId);
        
        sqlSession.delete(NAMESPACE + ".deletePortfolio", portfolioId);
        
        System.out.println("✅ DAO: 삭제 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    public Map<String, Object> getPortfolioSummary(int memberId) throws Exception {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 DAO: 포트폴리오 요약 조회");
        System.out.println("  - memberId: " + memberId);
        
        Map<String, Object> result = sqlSession.selectOne(NAMESPACE + ".getPortfolioSummary", memberId);
        
        System.out.println("✅ DAO: 요약 조회 완료!");
        if (result != null) {
            System.out.println("  - 종목 수: " + result.get("stockCount"));
            System.out.println("  - 총 투자금액: " + result.get("totalInvestment"));
            System.out.println("  - 총 평가금액: " + result.get("totalValue"));
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return result;
    }
}
