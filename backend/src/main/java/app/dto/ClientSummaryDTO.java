package app.dto;

/**
 * DTO para representar el resumen de datos de un cliente en la lista del entrenador.
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 23/05/2026
 */
public class ClientSummaryDTO {
    // Identificador único autoincremental de la entidad.
    private Long id;
    // Nombre completo del usuario.
    private String nombre;
    // Nombre de usuario único (dirección de correo electrónico).
    private String username;
    // Peso corporal actual del cliente en kilogramos.
    private double peso;
    // Altura del cliente en centímetros.
    private int altura;
    // Atributo de tipo int para almacenar edad.
    private int edad;
    // Atributo de tipo String para almacenar ultimoGrupoMuscular.
    private String ultimoGrupoMuscular;
    // Nombre de archivo o URL del avatar del usuario.
    private String avatar;

    /**
     * Constructor de la clase ClientSummaryDTO con inyección de dependencias.
     *
     */
    public ClientSummaryDTO() {
    }

    /**
     * Constructor de la clase ClientSummaryDTO con inyección de dependencias.
     *
     * @param id Parámetro de entrada para la operación.
     * @param nombre Parámetro de entrada para la operación.
     * @param username Parámetro de entrada para la operación.
     * @param peso Parámetro de entrada para la operación.
     * @param altura Parámetro de entrada para la operación.
     * @param edad Parámetro de entrada para la operación.
     * @param ultimoGrupoMuscular Parámetro de entrada para la operación.
     * @param avatar Parámetro de entrada para la operación.
     */
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

    /**
     * Recupera el valor actual de id.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el valor de id.
     *
     * @param id Parámetro de entrada para la operación.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Recupera el valor actual de nombre.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el valor de nombre.
     *
     * @param nombre Parámetro de entrada para la operación.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Recupera el valor actual de username.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el valor de username.
     *
     * @param username Parámetro de entrada para la operación.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Recupera el valor actual de peso.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public double getPeso() {
        return peso;
    }

    /**
     * Establece el valor de peso.
     *
     * @param peso Parámetro de entrada para la operación.
     */
    public void setPeso(double peso) {
        this.peso = peso;
    }

    /**
     * Recupera el valor actual de altura.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getAltura() {
        return altura;
    }

    /**
     * Establece el valor de altura.
     *
     * @param altura Parámetro de entrada para la operación.
     */
    public void setAltura(int altura) {
        this.altura = altura;
    }

    /**
     * Recupera el valor actual de edad.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public int getEdad() {
        return edad;
    }

    /**
     * Establece el valor de edad.
     *
     * @param edad Parámetro de entrada para la operación.
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * Recupera el valor actual de ultimogrupomuscular.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getUltimoGrupoMuscular() {
        return ultimoGrupoMuscular;
    }

    /**
     * Establece el valor de ultimogrupomuscular.
     *
     * @param ultimoGrupoMuscular Parámetro de entrada para la operación.
     */
    public void setUltimoGrupoMuscular(String ultimoGrupoMuscular) {
        this.ultimoGrupoMuscular = ultimoGrupoMuscular;
    }

    /**
     * Recupera el valor actual de avatar.
     *
     * @return El resultado o estado devuelto tras procesar la petición.
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Establece el valor de avatar.
     *
     * @param avatar Parámetro de entrada para la operación.
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
