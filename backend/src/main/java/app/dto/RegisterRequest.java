package app.dto;

import app.entity.TipoUsuario;

public class RegisterRequest {
	
	private String nombre; 
	private String username; 
	private String password;
	private String fechaNacimiento; 
	private TipoUsuario tipoUsuario; 
	private double peso; 
	private int altura; 
	private int neat;
	private String trainerCode;

	public RegisterRequest() {}

	public String getTrainerCode() {
		return trainerCode;
	}

	public void setTrainerCode(String trainerCode) {
		this.trainerCode = trainerCode;
	}
	
	public RegisterRequest(String nombre, String username, String password, TipoUsuario tipoUsuario, double peso, int altura,
			int neat) {
		super();
		this.nombre = nombre;
		this.username = username;
		this.password = password;
		this.tipoUsuario = tipoUsuario;
		this.peso = peso;
		this.altura = altura;
		this.neat = neat;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
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

	public int getNeat() {
		return neat;
	}

	public void setNeat(int neat) {
		this.neat = neat;
	}

	public String getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	} 
	
	
	
	

	
}
