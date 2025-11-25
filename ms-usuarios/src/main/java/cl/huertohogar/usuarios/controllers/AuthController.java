package cl.huertohogar.usuarios.controllers;

import cl.huertohogar.usuarios.dto.AuthResponse;
import cl.huertohogar.usuarios.dto.LoginRequest;
import cl.huertohogar.usuarios.dto.RegisterRequest;
import cl.huertohogar.usuarios.entities.Rol;
import cl.huertohogar.usuarios.entities.Usuario;
import cl.huertohogar.usuarios.repositories.UsuarioRepository;
import cl.huertohogar.usuarios.services.JwtService;
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
        // Delega la validación de credenciales a Spring Security.
        // Si la password no coincide, esto lanza una excepción automática (BadCredentialsException) y corta el flujo.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Si llegamos aquí, las credenciales son válidas. Recuperamos el usuario para la respuesta.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener datos de usuario post-login"));

        // Generamos la "llave maestra" (Token) para las siguientes peticiones.
        String token = jwtService.generateToken(usuario);

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .usuario(usuario)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está en uso");
        }

        // LÓGICA DE BOOTSTRAP / INICIO DE SISTEMA:
        // Si la base de datos está vacía, asumimos que el primer usuario registrado es el dueño/admin.
        // Esto evita tener que insertar el primer admin manualmente por SQL.
        boolean isFirstUser = usuarioRepository.count() == 0;
        Rol rolAsignado = isFirstUser ? Rol.ADMIN : Rol.CLIENTE;


        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .run(request.getRun())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Seguridad: Hash obligatorio.
                .region(request.getRegion())
                .comuna(request.getComuna())
                .direccion(request.getDireccion())
                .rol(rolAsignado) 
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // UX (Experiencia de Usuario): Login automático.
        // En lugar de obligar al usuario a loguearse después de registrarse, le damos el token de inmediato.
        String token = jwtService.generateToken(usuarioGuardado);

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .token(token)
                .usuario(usuarioGuardado)
                .build());
    }
}