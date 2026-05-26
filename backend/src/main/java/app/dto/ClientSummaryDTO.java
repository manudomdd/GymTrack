package app.dto;

/**
 * Objeto de transferencia de datos (DTO) que encapsula un resumen del perfil de un cliente.
 * <p>
 * Se utiliza principalmente en el panel del entrenador para mostrar un listado
 * rápido de sus clientes vinculados, reduciendo la cantidad de datos transmitidos
 * al omitir campos sensibles (como contraseñas) o innecesarios para esa vista específica.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class ClientSummaryDTO {

    private Long id;
    private String nombre;
    private String username;
    private double peso;
    private int altura;
    private int edad;
    private String ultimoGrupoMuscular;
    private String avatar;

    public ClientSummaryDTO() {
    }

    public ClientSummaryDTO(Long id, String nombre, String username, double peso, int altura, int edad, String ultimoGrupoMuscular, String avatar) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.peso = peso;
        this.altura = altura;
        this.edad = edad;
        this.ultimoGrupoMuscular = ultimoGrupoMuscular;
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getUltimoGrupoMuscular() {
        return ultimoGrupoMuscular;
    }

    public void setUltimoGrupoMuscular(String ultimoGrupoMuscular) {
        this.ultimoGrupoMuscular = ultimoGrupoMuscular;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
