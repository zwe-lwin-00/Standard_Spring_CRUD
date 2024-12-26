package com.demo.standardcrud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.standardcrud.dto.ApiResponse;
import com.demo.standardcrud.dto.UserDTO;
import com.demo.standardcrud.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            
            if (users.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, users, "No users found."));
            }
            
            return ResponseEntity.ok(new ApiResponse<>(true, users, "Users fetched successfully"));
        } catch (DataAccessException e) {
            if (e.getMessage().contains("Table 'standardcrud.users' doesn't exist")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(false, null, "Database table 'users' doesn't exist."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "An error occurred while fetching users."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "An unexpected error occurred."));
        }
    }


    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (page < 0 || size <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, null, "Page number and size must be positive."));
            }

            Page<UserDTO> users = userService.getAllUsersPaginated(page, size);
            
            if (users.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(true, users, "No users found."));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, users, "Users fetched successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "An unexpected error occurred while fetching users."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            UserDTO userDTO = userService.getUserById(id);
            
            if (userDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, null, "User with ID " + id + " not found."));
            }
            
            return ResponseEntity.ok(new ApiResponse<>(true, userDTO, "User fetched successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "An unexpected error occurred while fetching the user."));
        }
    }


    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, createdUser, "User created successfully"));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, null, "A user with this email already exists."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, null, "Data integrity violation occurred."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "An unexpected error occurred."));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, updatedUser, "User updated successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, "User with ID " + id + " not found."));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, null, "A user with this email already exists."));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, null, "Data integrity violation occurred during user update."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "An unexpected error occurred while updating the user."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, "User with ID " + id + " not found."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, null, "An unexpected error occurred while deleting the user."));
        }
    }

}
