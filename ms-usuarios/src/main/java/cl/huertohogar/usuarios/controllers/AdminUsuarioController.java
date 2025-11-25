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
// Seguridad Robusta: Esta anotación protege TODOS los métodos de la clase.
// Cualquier intento de acceso sin el token con claim "rol: ADMIN" será rechazado con 403 Forbidden.
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {

    private final UsuarioRepository usuarioRepository;
    // Inyectamos el encoder para hashear contraseñas si el admin crea o modifica usuarios manualmente.
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuarioRequest) {
        // Validación de negocio: Evitamos excepciones de SQL (Unique Constraint) validando antes manualmente.
        if (usuarioRepository.findByEmail(usuarioRequest.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email ya está en uso");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .nombre(usuarioRequest.getNombre())
                .apellido(usuarioRequest.getApellido())
                .run(usuarioRequest.getRun())
                .email(usuarioRequest.getEmail())
                // SEGURIDAD CRÍTICA: Nunca guardar la password tal cual viene del JSON. Siempre pasarla por el encoder.
                .password(passwordEncoder.encode(usuarioRequest.getPassword()))
                .region(usuarioRequest.getRegion())
                .comuna(usuarioRequest.getComuna())
                .direccion(usuarioRequest.getDireccion())
                // Si el JSON no trae rol, asignamos CLIENTE por defecto para evitar nulos.
                .rol(usuarioRequest.getRol() != null ? usuarioRequest.getRol() : Rol.CLIENTE)
                .build();
        
        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetalles) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        usuario.setNombre(usuarioDetalles.getNombre());
        usuario.setApellido(usuarioDetalles.getApellido());
        usuario.setEmail(usuarioDetalles.getEmail());
        usuario.setRun(usuarioDetalles.getRun());
        usuario.setRol(usuarioDetalles.getRol());
        usuario.setRegion(usuarioDetalles.getRegion());
        usuario.setComuna(usuarioDetalles.getComuna());
        usuario.setDireccion(usuarioDetalles.getDireccion());

        // Lógica condicional de contraseña:
        // Solo la actualizamos (y encriptamos) si el admin envió una nueva.
        // Si viene nula o vacía, mantenemos la contraseña antigua para no bloquear al usuario.
        if (usuarioDetalles.getPassword() != null && !usuarioDetalles.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioDetalles.getPassword()));
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        usuarioRepository.delete(usuario);
        return ResponseEntity.noContent().build();
    }
}