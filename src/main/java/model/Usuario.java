package model;

import java.sql.Date;

public class Usuario {
    
    // Atributos mínimos exigidos por el enunciado
    private String usuario;
    private String nombre;
    private String apellidos;
    private String email;
    private Date fechaAlta;
    
    // Atributos lógicos para la aplicación
    private String password;
    private String rol; // 'ADMIN' o 'ESTUDIANTE'

    public Usuario() {
    }

    public Usuario(String usuario, String password, String rol, String nombre, String apellidos, String email, Date fechaAlta) {
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.fechaAlta = fechaAlta;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Date fechaAlta) { this.fechaAlta = fechaAlta; }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " (" + usuario + ")";
    }
}