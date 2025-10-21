package com.intern.userservice.repository;

import com.intern.userservice.model.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    @Query(value = "INSERT INTO card_info (number, holder, expiration_date, user_id) " +
            "VALUES (:number, :holder, :expirationDate, :userId)" +
            "RETURNING *", nativeQuery = true)
    CardInfo createCardNative(@Param("number") String number,
                          @Param("holder") String holder,
                          @Param("expirationDate") LocalDate expirationDate,
                          @Param("userId") Long userId);

    @Query(value = "SELECT * FROM card_info WHERE id = :id", nativeQuery = true)
    Optional<CardInfo> findByIdNative(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM card_info WHERE id = :id", nativeQuery = true)
    int deleteByIdNative(@Param("id") Long id);

    // Named methods
    // Page<CardInfo> findAll(Pageable pageable);
    // findById is a named method
    // deleteById is a named method
}