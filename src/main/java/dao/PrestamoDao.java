package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Prestamo;
import util.ConexionDB;

public class PrestamoDao {

    // --- MÉTODOS DEL ADMINISTRADOR ---

    public List<Prestamo> obtenerTodosLosPrestamos(String filtroEstudiante) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE estado != 'DEVUELTO'";
        if (filtroEstudiante != null && !filtroEstudiante.trim().isEmpty()) {
            sql += " AND usuario_id LIKE ?";
        }
        
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filtroEstudiante != null && !filtroEstudiante.trim().isEmpty()) {
                ps.setString(1, "%" + filtroEstudiante + "%"); 
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrestamo(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // --- MÉTODOS DEL ESTUDIANTE ---

    public List<Prestamo> obtenerPrestamosPorEstudiante(String usuarioId) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE usuario_id = ? AND estado != 'DEVUELTO'";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrestamo(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    /**
     * TRANSACCIÓN: Registra el préstamo y resta 1 copia disponible.
     */
    public boolean registrarPrestamo(String usuarioId, String isbnLibro) {
        String sqlInsert = "INSERT INTO prestamos (usuario_id, libro_isbn, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'ACTIVO')";
        String sqlUpdate = "UPDATE libros SET copias_disponibles = copias_disponibles - 1 WHERE isbn = ? AND copias_disponibles > 0";
        
        Connection con = ConexionDB.getConexion();
        try {
            con.setAutoCommit(false); // Inicia transacción
            
            // 1. Restar stock
            try (PreparedStatement psUpd = con.prepareStatement(sqlUpdate)) {
                psUpd.setString(1, isbnLibro);
                if (psUpd.executeUpdate() == 0) {
                    con.rollback(); // Si no hay stock, cancela
                    return false;
                }
            }
            // 2. Insertar préstamo
            try (PreparedStatement psIns = con.prepareStatement(sqlInsert)) {
                psIns.setString(1, usuarioId);
                psIns.setString(2, isbnLibro);
                psIns.executeUpdate();
            }
            
            con.commit(); // Confirma los cambios
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    /**
     * TRANSACCIÓN: Cambia estado a DEVUELTO y suma 1 copia disponible.
     */
    public boolean devolverLibro(int idPrestamo, String isbnLibro) {
        String sqlUpdatePrestamo = "UPDATE prestamos SET estado = 'DEVUELTO' WHERE id_prestamo = ?";
        String sqlUpdateLibro = "UPDATE libros SET copias_disponibles = copias_disponibles + 1 WHERE isbn = ?";
        
        Connection con = ConexionDB.getConexion();
        try {
            con.setAutoCommit(false);
            
            try (PreparedStatement ps1 = con.prepareStatement(sqlUpdatePrestamo)) {
                ps1.setInt(1, idPrestamo);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement(sqlUpdateLibro)) {
                ps2.setString(1, isbnLibro);
                ps2.executeUpdate();
            }
            
            con.commit();
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    /**
     * Suma 15 días a la fecha de devolución y cambia estado a PRORROGADO
     */
    public boolean prorrogarPrestamo(int idPrestamo) {
        String sql = "UPDATE prestamos SET fecha_devolucion = DATE_ADD(fecha_devolucion, INTERVAL 15 DAY), estado = 'PRORROGADO' WHERE id_prestamo = ? AND estado = 'ACTIVO'";
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // Método auxiliar para no repetir código
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setIdPrestamo(rs.getInt("id_prestamo"));
        p.setUsuarioId(rs.getString("usuario_id"));
        p.setLibroIsbn(rs.getString("libro_isbn"));
        p.setFechaPrestamo(rs.getDate("fecha_prestamo"));
        p.setFechaDevolucion(rs.getDate("fecha_devolucion"));
        p.setEstado(rs.getString("estado"));
        return p;
    }
}