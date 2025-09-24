package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.response.UserResponse;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.UsuarioRepository;
import com.jugueteria.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UserResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return modelMapper.map(usuario, UserResponse.class);
    }

    @Override
    public UserResponse getProfile(Usuario usuario) {
        return modelMapper.map(usuario, UserResponse.class);
    }

    @Override
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar, usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}