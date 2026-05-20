package app.service;

import app.dto.LoginRequest;
import app.dto.LoginResponse;
import app.dto.RegisterRequest;
import app.entity.TipoUsuario;
import app.entity.User;
import app.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public void registrar(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        User newUser = new User();
        newUser.setNombre(request.getNombre());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEdad(request.getEdad());
        newUser.setAltura(request.getAltura());
        newUser.setPeso(request.getPeso());
        newUser.setTipoUsuario(request.getTipoUsuario());

        if (request.getTipoUsuario() == TipoUsuario.ENTRENADOR) {
            String code = "TR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            newUser.setTrainerCode(code);
        } else if (request.getTipoUsuario() == TipoUsuario.CLIENTE && request.getTrainerCode() != null) {
            userRepository.findByTrainerCode(request.getTrainerCode()).ifPresent(newUser::setTrainer);
        }

        userRepository.save(newUser);
    }

    public app.dto.LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User usuario = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, usuario.getTipoUsuario());
    }
}