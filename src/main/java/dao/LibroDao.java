/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Libro;
import util.ConexionDB;

public class LibroDao {

    // --- MÉTODOS PARA EL ESTUDIANTE Y ADMIN (Lectura y Filtros) ---

    /**
     * Obtiene libros aplicando filtros. 
     * @param filtroGenero "Todos" o el género específico.
     * @param soloDisponibles true para Estudiantes que marquen la casilla, false para Admin.
     */
    public List<Libro> obtenerLibros(String filtroGenero, boolean soloDisponibles) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE 1=1"; // 1=1 es un truco para concatenar ANDs fácilmente
        
        if (filtroGenero != null && !filtroGenero.trim().isEmpty() && !filtroGenero.equals("Todos")) {
            sql += " AND genero = ?";
        }
        if (soloDisponibles) {
            sql += " AND copias_disponibles > 0";
        }
        
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (filtroGenero != null && !filtroGenero.trim().isEmpty() && !filtroGenero.equals("Todos")) {
                ps.setString(1, filtroGenero);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro l = new Libro();
                    l.setIsbn(rs.getString("isbn"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setGenero(rs.getString("genero"));
                    l.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                    l.setRutaPortada(rs.getString("ruta_portada"));
                    l.setRutaResumen(rs.getString("ruta_resumen"));
                    l.setCopiasDisponibles(rs.getInt("copias_disponibles"));
                    lista.add(l);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener libros: " + e.getMessage());
        }
        return lista;
    }

    // --- MÉTODOS EXCLUSIVOS DEL ADMINISTRADOR (CRUD) ---

   public boolean insertarLibro(Libro libro) {
    // CORREGIDO: Cambiado ruta_texto por ruta_resumen
    String sql = "INSERT INTO libros (isbn, titulo, autor, genero, copias_disponibles, fecha_publicacion, ruta_portada, ruta_resumen) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = ConexionDB.getConexion();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, libro.getIsbn());
        pstmt.setString(2, libro.getTitulo());
        pstmt.setString(3, libro.getAutor());
        pstmt.setString(4, libro.getGenero());
        pstmt.setInt(5, libro.getCopiasDisponibles());
        pstmt.setDate(6, libro.getFechaPublicacion());
        pstmt.setString(7, libro.getRutaPortada());
        pstmt.setString(8, libro.getRutaResumen());

        int filasAfectadas = pstmt.executeUpdate();
        return filasAfectadas > 0;

    } catch (SQLException e) {
        System.out.println("Error al insertar el libro: " + e.getMessage());
        return false;
    }
}
   public boolean actualizarLibro(Libro l) {
    // Este estaba bien (usaba ruta_resumen), pero lo dejo aquí para que lo tengas completo
    String sql = "UPDATE libros SET titulo=?, autor=?, genero=?, fecha_publicacion=?, ruta_portada=?, ruta_resumen=?, copias_disponibles=? WHERE isbn=?";
    
    try (Connection con = ConexionDB.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {
         
        ps.setString(1, l.getTitulo());
        ps.setString(2, l.getAutor());
        ps.setString(3, l.getGenero());
        ps.setDate(4, l.getFechaPublicacion());
        ps.setString(5, l.getRutaPortada());
        ps.setString(6, l.getRutaResumen());
        ps.setInt(7, l.getCopiasDisponibles());
        ps.setString(8, l.getIsbn());
        
        return ps.executeUpdate() > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

   /**
     * Elimina un libro de la base de datos a partir de su ISBN.
     * @param isbn El ISBN del libro que se quiere borrar.
     * @return true si se eliminó correctamente, false si hubo un error.
     */
    public boolean eliminarLibro(String isbn) {
        String sql = "DELETE FROM libros WHERE isbn = ?";
        
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Pasamos el ISBN al interrogante de la consulta
            ps.setString(1, isbn);
            
            // executeUpdate() devuelve el número de filas borradas. 
            // Si es mayor que 0, significa que el libro se borró con éxito.
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar el libro: " + e.getMessage());
            return false;
        }
    }
    
    public Libro obtenerLibroPorIsbn(String isbn) {
        String sql = "SELECT * FROM libros WHERE isbn = ?";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro l = new Libro();
                    l.setIsbn(rs.getString("isbn"));
                    l.setCopiasDisponibles(rs.getInt("copias_disponibles"));
                    // Rellena el resto si lo necesitas, pero con esos dos basta para la comprobación
                    return l;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}