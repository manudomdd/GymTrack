package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicializa la aplicación Spring Boot del backend de GymTrack.
 * <p>
 * Este es el punto de entrada de la API REST. Al ejecutarse, Spring Boot arranca
 * el servidor web embebido (Apache Tomcat), inicializa el contexto de aplicación 
 * de Spring, configura las conexiones a la base de datos y mapea los controladores REST.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@SpringBootApplication
public class BackendApplication {


	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
