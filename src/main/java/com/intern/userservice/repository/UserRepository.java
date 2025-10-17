package com.intern.userservice.repository;

import com.intern.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Named method
    Optional<User> findByEmail(String email);

    // JPQL query
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserByIdJPQL(@Param("id") Long id);

    // Native SQL query
    @Query(value = "SELECT * FROM users u WHERE u.email = :email", nativeQuery = true)
    Optional<User> findUserByEmailNative(@Param("email") String email);

    // Pagination is built-in from PagingAndSortingRepository<T, ID>
    // Page<User> findAll(Pageable pageable);

    // DeleteById is a named method
    // UpdateUserById will be a service method
}
