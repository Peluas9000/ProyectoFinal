/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

/**
 *
 * @author ayoub
 */


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {
    
    public Usuario login(String username, String password) {
        Usuario user = null;
        // La consulta comprueba el usuario y la contraseña a la vez
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";
        
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new Usuario();
                    user.setUsuario(rs.getString("usuario"));
                    user.setRol(rs.getString("rol")); // Devuelve ADMIN o ESTUDIANTE 
                    user.setNombre(rs.getString("nombre"));
                    // Rellenar el resto de atributos...
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user; // Si devuelve null, el login es incorrecto
    }
}