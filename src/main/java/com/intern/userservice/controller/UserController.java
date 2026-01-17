package com.intern.userservice.controller;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.dto.UserUpdateDto;
import com.intern.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "Users", description = "User Management API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Registers a new user in the system. Requires admin role or matching subject ID.")
    public ResponseEntity<UserResponse> createUser(@Validated @RequestBody UserCreateDto request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves profile details for a specific user ID. Accessible by admins or the account owner.")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        Optional<UserResponse> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns a paginated list of all registered users. Restricted to admin users.")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    @Operation(summary = "Search user by email", description = "Finds a user profile using their email address. Accessible by admins or the account owner.")
    public ResponseEntity<UserResponse> getUserByEmail(@Email @RequestParam String email) {
        Optional<UserResponse> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates the profile information for an existing user. Accessible by admins or the account owner.")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Validated @RequestBody UserUpdateDto request) {
        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Permanently removes a user account from the system. Accessible by admins or the account owner.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}