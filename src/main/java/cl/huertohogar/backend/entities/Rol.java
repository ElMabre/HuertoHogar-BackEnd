package cl.huertohogar.backend.entities;

/**
 * Enum para definir los roles de usuario en el sistema.
 * Esto asegura que solo podamos usar valores definidos (ADMIN, CLIENTE, VENDEDOR).
 */
public enum Rol {
    ADMIN,
    CLIENTE,
    VENDEDOR
}