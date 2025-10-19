package com.intern.userservice.repository;

import com.intern.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(value = "INSERT INTO users (name, surname, birth_date, email) VALUES (:name, :surname, :birthDate, :email)",
            nativeQuery = true)
    void createUserNative(@Param("name") String name,
                          @Param("surname") String surname,
                          @Param("birthDate") LocalDate birthDate,
                          @Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.name = :name, u.surname = :surname, u.birthDate = :birthDate, u.email = :email WHERE u.id = :id")
    int updateByIdJPQL(@Param("id") Long id,
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

//    Named methods
//    Pagination is built-in from PagingAndSortingRepository<T, ID>
//    Page<User> findAll(Pageable pageable);
//    Optional<User> findById(Long id);
//    Optional<User> findByEmail(String email);
//    void deleteById(Long id);
}
