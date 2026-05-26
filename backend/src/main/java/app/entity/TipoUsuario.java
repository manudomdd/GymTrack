package app.entity;

/**
 * Enumerado que define los roles de usuario disponibles en el sistema.
 * Cada valor determina el nivel de acceso y las pantallas a las que
 * puede navegar el usuario tras autenticarse.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public enum TipoUsuario {

    /** Usuario registrado como cliente de un entrenador. */
    CLIENTE,

    /** Usuario registrado como entrenador personal. */
    ENTRENADOR,

    /** Usuario con acceso total al panel de administración del sistema. */
    ADMIN
}
