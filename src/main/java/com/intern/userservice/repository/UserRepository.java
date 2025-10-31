package com.intern.userservice.repository;

import com.intern.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "INSERT INTO users (sub, name, surname, birth_date, email) " +
            "VALUES (:sub, :name, :surname, :birthDate, :email) " +
            "RETURNING *",
            nativeQuery = true)
    User createUserNative(@Param("sub") UUID sub,
                          @Param("name") String name,
                          @Param("surname") String surname,
                          @Param("birthDate") LocalDate birthDate,
                          @Param("email") String email);

    @Query(value = "UPDATE users " +
            "SET name = :name, " +
            "    surname = :surname, " +
            "    birth_date = :birthDate, " +
            "    email = :email " +
            "WHERE id = :id " +
            "RETURNING *",
            nativeQuery = true)
    User updateByIdNative(@Param("id") Long id,
                          @Param("name") String name,
                          @Param("surname") String surname,
                          @Param("birthDate") LocalDate birthDate,
                          @Param("email") String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    int deleteByIdJPQL(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdJPQL(@Param("id") Long id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u.sub FROM User u WHERE u.id = :id")
    Optional<UUID> findSubById(@Param("id") Long id);

    @Query("SELECT u.email FROM User u WHERE u.email = :email")
    Optional<UUID> findSubByEmail(@Param("email") String email);

//    Named methods
//    Pagination is built-in from PagingAndSortingRepository<T, ID>
//    Page<User> findAll(Pageable pageable);
}
