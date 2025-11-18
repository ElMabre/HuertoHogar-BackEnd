package cl.huertohogar.backend.controllers;

import cl.huertohogar.backend.dto.AuthResponse;
import cl.huertohogar.backend.dto.LoginRequest;
import cl.huertohogar.backend.dto.RegisterRequest;
import cl.huertohogar.backend.entities.Rol;
import cl.huertohogar.backend.entities.Usuario;
import cl.huertohogar.backend.repositories.UsuarioRepository;
import cl.huertohogar.backend.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // 1. Autenticar al usuario usando el AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Si la autenticación es exitosa, obtener los detalles del usuario
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener datos de usuario post-login"));

        // 3. Generar el token JWT
        String token = jwtService.generateToken(usuario);

        // 4. Devolver el token y los datos del usuario
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .usuario(usuario)
                .build());
    }

    // Este es el método register() modificado
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    
    // 1. Verificar si el email o RUN ya existen
    if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está en uso");
    }

    // Revisa si hay usuarios en la BD. Si no hay (count = 0), este es el primer usuario.
    boolean isFirstUser = usuarioRepository.count() == 0;
    Rol rolAsignado = isFirstUser ? Rol.ADMIN : Rol.CLIENTE;


    // 2. Crear el nuevo usuario
    Usuario usuario = Usuario.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .run(request.getRun())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword())) // ¡Encriptar la contraseña!
            .region(request.getRegion())
            .comuna(request.getComuna())
            .direccion(request.getDireccion())
            .rol(rolAsignado) // <-- USAMOS LA VARIABLE DE ROL
            .build();

    // 3. Guardar el usuario en la BD
    Usuario usuarioGuardado = usuarioRepository.save(usuario);

    // 4. Generar el token para el nuevo usuario (login automático)
    String token = jwtService.generateToken(usuarioGuardado);

    // 5. Devolver la respuesta
    return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
            .token(token)
            .usuario(usuarioGuardado)
            .build());
}
}