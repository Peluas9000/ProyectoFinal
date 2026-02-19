/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;


// IMPORTANTE: Asegúrate de que esta ruta coincida con la carpeta donde tienes tu clase Usuario


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import model.Usuario;
import util.ConexionDB;

public class UsuarioDao {

    /**
     * Comprueba las credenciales en la base de datos.
     * @param usuario El nombre de usuario introducido en el Login
     * @param password La contraseña introducida
     * @return El objeto Usuario completo si es correcto (con su rol), o null si falla.
     */
    
    public UsuarioDao(){}
    public Usuario autenticar(String usuario, String password) {
        Usuario usuarioValidado = null;
        
        // Consulta SQL para buscar al usuario. Usamos ? para evitar Inyección SQL.
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
        
        // Usamos try-with-resources para que la conexión y la consulta se cierren automáticamente
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Sustituimos las interrogaciones (?) por los valores que nos pasan
            ps.setString(1, usuario);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                // Si rs.next() es true, significa que encontró una coincidencia exacta
                if (rs.next()) {
                    usuarioValidado = new Usuario();
                    
                    // Rellenamos el POJO con los datos de la base de datos
                    usuarioValidado.setUsuario(rs.getString("usuario"));
                    usuarioValidado.setPassword(rs.getString("password"));
                    usuarioValidado.setRol(rs.getString("rol")); // Esto es vital para saber si es ADMIN o ESTUDIANTE
                    usuarioValidado.setNombre(rs.getString("nombre"));
                    usuarioValidado.setApellidos(rs.getString("apellidos"));
                    usuarioValidado.setEmail(rs.getString("email"));
                    usuarioValidado.setFechaAlta(rs.getDate("fecha_alta"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en la autenticación: " + e.getMessage());
        }
        
        // Devolvemos el usuario (si las credenciales estaban mal, devolverá null)
        return usuarioValidado;
    }
    
    
    public boolean insertarUsuario(Usuario alumno) {
    // CORREGIDO: Cambiado "alumnos" por "usuarios"
    String sql = "INSERT INTO usuarios (nombre,usuario, password, email, rol, fecha_alta,apellidos) VALUES (?, ? ,?, ?, ?, ?, ?)";
    
    try (Connection con = ConexionDB.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
         
        ps.setString(1, alumno.getNombre());
        ps.setString(2, alumno.getUsuario());
        ps.setString(3, alumno.getPassword());
        ps.setString(4, alumno.getEmail());
        ps.setString(5, alumno.getRol()); 
        ps.setDate(6, Date.valueOf(LocalDate.now())); // Esto está perfecto
        ps.setString(7,alumno.getApellidos());
        int filasAfectadas = ps.executeUpdate();
        return filasAfectadas > 0;
        
    } catch (SQLException e) {
        System.out.println("Error al insertar el alumno en la BD: " + e.getMessage());
        return false;
    }
}
    
    
}