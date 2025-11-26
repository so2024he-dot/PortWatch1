package com.portwatch.persistence;

import com.portwatch.domain.WatchlistVO;
import java.util.List;
import java.util.Map;

public interface WatchlistDAO {
    public void insertWatchlist(WatchlistVO watchlist) throws Exception;
    public List<WatchlistVO> selectWatchlistByMember(int memberId) throws Exception;
    public int checkExists(Map<String, Object> params) throws Exception;
    public void deleteWatchlist(Map<String, Object> params) throws Exception;
}
