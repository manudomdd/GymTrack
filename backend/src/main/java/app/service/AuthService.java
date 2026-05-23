package app.service;

import app.dto.LoginRequest;
import app.dto.LoginResponse;
import app.dto.RegisterRequest;
import app.entity.TipoUsuario;
import app.entity.User;
import app.entity.Client;
import app.entity.Trainer;
import app.repository.UserRepository;
import app.repository.TrainerRepository;
import app.repository.ClientRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio para la gestión del flujo de autenticación y registro de usuarios.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@Service
public class AuthService {

    // Repositorio para operaciones de persistencia de la entidad User.
    private final UserRepository userRepository;
    // Repositorio para operaciones de persistencia de la entidad Trainer.
    private final TrainerRepository trainerRepository;
    // Atributo de tipo PasswordEncoder para almacenar passwordEncoder.
    private final PasswordEncoder passwordEncoder;
    // Servicio para la gestión de la lógica de negocio de Jwt.
    private final JwtService jwtService;
    // Gestor de persistencia de JPA (EntityManager) para transacciones.
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor de la clase AuthService con inyección de dependencias.
     *
     * @param userRepository Parámetro de entrada para la operación.
     * @param trainerRepository Parámetro de entrada para la operación.
     * @param passwordEncoder Parámetro de entrada para la operación.
     * @param jwtService Parámetro de entrada para la operación.
     * @param authenticationManager Parámetro de entrada para la operación.
     */
    public AuthService(UserRepository userRepository, TrainerRepository trainerRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Procesa la operación correspondiente para registrar.
     *
     * @param request Estructura de datos con la solicitud del cliente.
     */
    public void registrar(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El username ya está registrado");
        }

        User newUser;

        if (request.getTipoUsuario() == TipoUsuario.ENTRENADOR) {
            Trainer trainer = new Trainer();
            trainer.setTrainerCode("TR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            newUser = trainer;
        } else {
            Client client = new Client();
            client.setAltura(request.getAltura());
            client.setPeso(request.getPeso());
            if (request.getFechaNacimiento() != null && !request.getFechaNacimiento().isEmpty()) {
                client.setFechaNacimiento(java.time.LocalDate.parse(request.getFechaNacimiento()));
            }
            if (request.getTrainerCode() != null) {
                trainerRepository.findByTrainerCode(request.getTrainerCode()).ifPresent(client::setTrainer);
            }
            newUser = client;
        }

        newUser.setNombre(request.getNombre());
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setTipoUsuario(request.getTipoUsuario());
        
        if (request.getAvatar() != null && !request.getAvatar().trim().isEmpty()) {
            newUser.setAvatar(request.getAvatar());
        } else {
            String[] avatars = {"avatar_1", "avatar_2", "avatar_3"};
            int randomIndex = new java.util.Random().nextInt(avatars.length);
            newUser.setAvatar(avatars[randomIndex]);
        }

        userRepository.save(newUser);
    }

    public app.dto.LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User usuario = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario);
        return new LoginResponse(token, usuario.getTipoUsuario());
    }
}