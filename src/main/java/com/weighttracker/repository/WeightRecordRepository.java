package com.weighttracker.repository;

import com.weighttracker.entity.WeightRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeightRecordRepository extends JpaRepository<WeightRecord, Long> {
    
    // タイムスタンプでの降順ソートを追加
    List<WeightRecord> findByUserIdOrderByTimestampDesc(Integer userId);
    
    // 記録日付での降順ソートを追加
    List<WeightRecord> findByUserIdOrderByRecordedDateDesc(Integer userId);
    
    // 日付で降順ソートしたデータの一部を取得（ページング対応）
    List<WeightRecord> findByUserIdOrderByRecordedDateDesc(Integer userId, Pageable pageable);
    
    @Query("SELECT AVG(w.weight) FROM WeightRecord w WHERE w.userId = :userId")
    Double calculateAverageWeight(@Param("userId") Integer userId);
    
    // 直近の指定された数のレコードから平均体重を計算するクエリ
    @Query(value = "SELECT AVG(w.weight) FROM WeightRecord w WHERE w.userId = :userId " +
           "ORDER BY w.recordedDate DESC LIMIT :limit")
    Double calculateRecentAverageWeight(@Param("userId") Integer userId, @Param("limit") Integer limit);
}