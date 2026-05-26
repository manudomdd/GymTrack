package app.controller;

import app.dto.UserAdminDTO;
import app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST que expone los endpoints del panel de administración.
 * <p>
 * Todos los métodos de este controlador están protegidos mediante
 * {@code @PreAuthorize("hasRole('ADMIN')")}, de modo que cualquier petición
 * procedente de un usuario sin el rol {@code ADMIN} recibirá un
 * {@code 403 Forbidden} antes de llegar a la lógica de negocio.
 * </p>
 * <p>
 * Base URL: {@code /api/admin}
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Obtiene el listado completo de usuarios registrados en el sistema,
     * excluyendo las cuentas con rol administrador.
     *
     * @return {@code 200 OK} con la lista de {@link UserAdminDTO}
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Elimina un entrenador por su identificador.
     * Los clientes vinculados a ese entrenador quedan activos pero desvinculados.
     *
     * @param id identificador del entrenador a eliminar
     * @return {@code 204 No Content} si la operación tiene éxito
     */
    @DeleteMapping("/users/trainer/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        adminService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un cliente por su identificador junto con todos sus
     * registros de entrenamiento, sueño y pasos asociados.
     *
     * @param id identificador del cliente a eliminar
     * @return {@code 204 No Content} si la operación tiene éxito
     */
    @DeleteMapping("/users/client/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        adminService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
