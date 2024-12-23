package com.demo.standardcrud.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.demo.standardcrud.dto.UserDTO;

public interface UserService {
    List<UserDTO> getAllUsers();

    Page<UserDTO> getAllUsersPaginated(int page, int size);

    UserDTO getUserById(Long id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);
}
