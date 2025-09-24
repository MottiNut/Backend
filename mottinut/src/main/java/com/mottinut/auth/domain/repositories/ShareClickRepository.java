package com.mottinut.auth.domain.repositories;

import com.mottinut.auth.domain.entities.ShareClick;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShareClickRepository extends JpaRepository<ShareClick, Long> {

    @Query("SELECT COUNT(sc) FROM ShareClick sc WHERE sc.shareCodeId = :shareCodeId")
    long countByShareCodeId(@Param("shareCodeId") Long shareCodeId);

    @Query("SELECT sc FROM ShareClick sc WHERE sc.shareCodeId = :shareCodeId ORDER BY sc.clickedAt DESC")
    List<ShareClick> findByShareCodeIdOrderByClickedAtDesc(@Param("shareCodeId") Long shareCodeId);

    @Query("SELECT sc FROM ShareClick sc WHERE sc.clickedAt >= :startDate AND sc.clickedAt <= :endDate")
    List<ShareClick> findByClickedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}