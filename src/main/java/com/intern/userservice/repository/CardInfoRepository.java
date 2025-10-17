package com.intern.userservice.repository;

import com.intern.userservice.model.CardInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    // Named method
    Optional<CardInfo> findByNumber(String number);

    // JPQL query
    @Query("SELECT c FROM CardInfo c WHERE c.id = :id")
    Optional<CardInfo> findCardByIdJPQL(@Param("id") Long id);

    // Native SQL query
    @Query(value = "SELECT * FROM card_info c WHERE c.user_id = :userId", nativeQuery = true)
    Page<CardInfo> findCardsByUserIdNative(@Param("userId") Long userId, Pageable pageable);

    // Page<CardInfo> findAll(Pageable pageable);

    // DeleteById is a named method
}