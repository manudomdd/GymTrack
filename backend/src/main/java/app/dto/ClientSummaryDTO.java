package app.dto;

public class ClientSummaryDTO {
    private Long id;
    private String nombre;
    private String email;
    private double peso;
    private int altura;
    private int edad;
    private String ultimoGrupoMuscular;

    public ClientSummaryDTO() {
    }

    public ClientSummaryDTO(Long id, String nombre, String email, double peso, int altura, int edad, String ultimoGrupoMuscular) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.peso = peso;
        this.altura = altura;
        this.edad = edad;
        this.ultimoGrupoMuscular = ultimoGrupoMuscular;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
