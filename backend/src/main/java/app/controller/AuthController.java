package app.controller;

import app.dto.LoginRequest;
import app.dto.LoginResponse;
import app.dto.RegisterRequest;
import app.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * Endpoint principal para el registro de nuevos usuarios. 
     * @param request Datos necesarios para el registro del usuario.
     * @return Mensaje de confirmacion o error.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.registrar(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("mensaje", "Usuario registrado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint para incio de sesión tanto de usuarios como entrenadores.
     * @param request Datos necesarios para el inicio de sesión.
     * @return Tokan y datos del usuario en caso de encontrar una coincidencia. 
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas o usuario no encontrado"));
        }
    }
}