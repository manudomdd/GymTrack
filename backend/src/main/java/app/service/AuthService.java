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
 * Servicio encargado de gestionar la lógica de negocio para la autenticación y el registro.
 * <p>
 * Se encarga de validar los datos del nuevo usuario, generar contraseñas encriptadas 
 * usando BCrypt, asignar avatares por defecto y verificar las credenciales 
 * durante el proceso de login, interactuando con {@link JwtService} para la emisión de tokens.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@Service
public class AuthService {


    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, TrainerRepository trainerRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

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