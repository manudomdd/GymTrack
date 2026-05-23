package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para el inicio y arranque del backend en Spring Boot.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
@SpringBootApplication
public class BackendApplication {

	/**
	 * Procesa la operación correspondiente para main.
	 *
	 * @param args Parámetro de entrada para la operación.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
