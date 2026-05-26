package app.config;

import app.service.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad que intercepta cada petición HTTP para validar el token JWT.
 * <p>
 * Extiende {@link OncePerRequestFilter} para garantizar su ejecución única por petición.
 * Verifica la presencia de la cabecera {@code Authorization} con el prefijo {@code Bearer },
 * extrae el token, lo valida y, si es correcto, establece el contexto de seguridad
 * ({@link SecurityContextHolder}) permitiendo el acceso al endpoint solicitado.
 * </p>
 * <p>
 * Incluye una optimización específica para limpiar el contexto de persistencia 
 * de Hibernate prematuramente, evitando así posibles fugas de conexiones JDBC 
 * originadas por el patrón Open-Session-In-View durante conexiones persistentes (SSE).
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Inyectamos el EntityManager para poder gestionar el contexto de persistencia manualmente
    @PersistenceContext
    private EntityManager entityManager;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Cargamos el usuario de la BBDD
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("JWT válido para usuario: {} | Ruta: {} {}",
                            userEmail, request.getMethod(), request.getRequestURI());

                    // --- SOLUCIÓN A LA FUGA DE CONEXIONES ---
                    // Limpiamos el contexto de persistencia. Esto desprende (detach) las entidades
                    // y fuerza a Hibernate (OpenSessionInView) a devolver la conexión JDBC 
                    // al HikariPool DE INMEDIATO antes de pasar al SseEmitter.
                    if (entityManager != null) {
                        entityManager.clear();
                    }
                    
                } else {
                    log.warn("JWT inválido o expirado para usuario: {}", userEmail);
                }
            }
        } catch (Exception ex) {
            log.error("Error procesando JWT en ruta {} {}: {}",
                    request.getMethod(), request.getRequestURI(), ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
