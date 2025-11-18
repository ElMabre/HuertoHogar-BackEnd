package cl.huertohogar.usuarios.controllers;

import cl.huertohogar.usuarios.entities.Rol;
import cl.huertohogar.usuarios.entities.Usuario;
import cl.huertohogar.usuarios.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Toda la clase requiere ROL ADMIN
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint ADMIN para obtener todos los usuarios.
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    /**
     * Endpoint ADMIN para obtener un usuario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }

    /**
     * Endpoint ADMIN para crear un nuevo usuario (con rol).
     * Nota: Este es un DTO simple, pero por simplicidad usamos la entidad.
     * En un caso real, usaríamos un DTO para no exponer la contraseña.
     */
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuarioRequest) {
        if (usuarioRepository.findByEmail(usuarioRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está en uso");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .nombre(usuarioRequest.getNombre())
                .apellido(usuarioRequest.getApellido())
                .run(usuarioRequest.getRun())
                .email(usuarioRequest.getEmail())
                .password(passwordEncoder.encode(usuarioRequest.getPassword())) // Encriptar contraseña
                .region(usuarioRequest.getRegion())
                .comuna(usuarioRequest.getComuna())
                .direccion(usuarioRequest.getDireccion())
                .rol(usuarioRequest.getRol() != null ? usuarioRequest.getRol() : Rol.CLIENTE) // Asignar rol
                .build();
        
        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    /**
     * Endpoint ADMIN para eliminar un usuario por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        usuarioRepository.delete(usuario);
        return ResponseEntity.noContent().build();
    }
}