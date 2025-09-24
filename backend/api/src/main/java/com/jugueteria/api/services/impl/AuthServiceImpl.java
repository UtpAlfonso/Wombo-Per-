package com.jugueteria.api.services.impl;
import com.jugueteria.api.dto.request.LoginRequest;
import com.jugueteria.api.dto.request.RegisterRequest;
import com.jugueteria.api.dto.response.AuthResponse;
import com.jugueteria.api.entity.Role;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.repository.RoleRepository;
import com.jugueteria.api.repository.UsuarioRepository;
import com.jugueteria.api.security.JwtService;
import com.jugueteria.api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.jugueteria.api.exception.ResourceNotFoundException; // Importa tu excepción personalizada
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
private final UsuarioRepository usuarioRepository;
private final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
private final AuthenticationManager authenticationManager;
 private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Intento de registro con email duplicado: {}", request.getEmail());
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        logger.info("Buscando el rol ROLE_CLIENT para el nuevo usuario.");
        Role userRole = roleRepository.findByNombre("ROLE_CLIENT")
                .orElseThrow(() -> {
                    // Este log es crucial para la depuración
                    logger.error("¡CONFIGURACIÓN INCORRECTA! El rol por defecto 'ROLE_CLIENT' no existe en la base de datos.");
                    return new IllegalStateException("Error de configuración interna del sistema.");
                });
        
        logger.info("Rol encontrado. Creando nuevo usuario con email: {}", request.getEmail());

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();
        
        usuarioRepository.save(usuario);
        logger.info("Usuario con email {} guardado exitosamente.", request.getEmail());

        String jwtToken = jwtService.generateToken(usuario);
        return AuthResponse.builder().token(jwtToken).build();
    }

@Override
public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
    String jwtToken = jwtService.generateToken(usuario);
    return AuthResponse.builder().token(jwtToken).build();
}
}