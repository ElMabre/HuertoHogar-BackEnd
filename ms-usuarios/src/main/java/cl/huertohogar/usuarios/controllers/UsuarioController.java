package cl.huertohogar.usuarios.controllers;

import cl.huertohogar.usuarios.entities.Usuario;
import cl.huertohogar.usuarios.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    /**
     * Permite a un usuario autenticado actualizar su propio perfil.
     * No requiere ID en la URL porque lo sacamos del Token de seguridad (Authentication).
     */
    @PutMapping("/perfil")
    public ResponseEntity<Usuario> updateMiPerfil(@RequestBody Usuario usuarioDetalles) {
        // 1. Obtener el usuario que está haciendo la petición desde el contexto de seguridad (Token)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailAutenticado = auth.getName(); // Spring Security guarda el email/username aquí

        // 2. Buscar al usuario en la BD
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 3. Actualizar SOLO los campos permitidos (Dirección)
        // No permitimos cambiar rol, password o email por aquí por seguridad.
        if (usuarioDetalles.getRegion() != null) usuario.setRegion(usuarioDetalles.getRegion());
        if (usuarioDetalles.getComuna() != null) usuario.setComuna(usuarioDetalles.getComuna());
        if (usuarioDetalles.getDireccion() != null) usuario.setDireccion(usuarioDetalles.getDireccion());
        
        // Opcional: Permitir cambiar nombre/apellido si quieres
        if (usuarioDetalles.getNombre() != null) usuario.setNombre(usuarioDetalles.getNombre());
        if (usuarioDetalles.getApellido() != null) usuario.setApellido(usuarioDetalles.getApellido());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        return ResponseEntity.ok(usuarioActualizado);
    }
}