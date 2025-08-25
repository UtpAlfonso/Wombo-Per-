package com.jugueteria.api.services;
import com.jugueteria.api.dto.response.UserResponse;
import com.jugueteria.api.entity.Usuario;
import java.util.List;
public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse getProfile(Usuario usuario);
    void deleteById(Long id);
}