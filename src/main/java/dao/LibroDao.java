/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

/**
 *
 * @author ayoub
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    // Método 2 en 1: Lista todos, o filtra por género si le pasas un String 
    public List<Libro> getLibros(String filtroGenero) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        
        // Si nos pasan un filtro, añadimos el WHERE 
        if (filtroGenero != null && !filtroGenero.isEmpty()) {
            sql += " WHERE genero = ?";
        }
        
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (filtroGenero != null && !filtroGenero.isEmpty()) {
                ps.setString(1, filtroGenero);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro l = new Libro();
                    l.setIsbn(rs.getString("isbn"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setGenero(rs.getString("genero"));
                    l.setCopiasDisponibles(rs.getInt("copias_disponibles"));
                    l.setRutaPortada(rs.getString("ruta_portada"));
                    // Añadir a la lista
                    lista.add(l);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // Aquí iría el public boolean insertarLibro(Libro l) con el INSERT INTO... [cite: 7]
}