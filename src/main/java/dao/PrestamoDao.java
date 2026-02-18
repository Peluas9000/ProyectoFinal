/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author ayoub
 */
public class PrestamoDAO {

    public boolean registrarPrestamo(String idUsuario, String isbnLibro) {
        String sqlInsert = "INSERT INTO prestamos (usuario_id, libro_isbn, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'ACTIVO')";
        String sqlUpdateStock = "UPDATE libros SET copias_disponibles = copias_disponibles - 1 WHERE isbn = ? AND copias_disponibles > 0";
        
        Connection con = ConexionDB.getConexion();
        try {
            // Desactivamos el autocommit para hacer una Transacción
            con.setAutoCommit(false); 
            
            // 1. Restar stock
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateStock)) {
                psUpdate.setString(1, isbnLibro);
                int filasAfectadas = psUpdate.executeUpdate();
                
                if (filasAfectadas == 0) {
                    con.rollback(); // Si no hay stock, deshacemos y cancelamos
                    return false;
                }
            }
            
            // 2. Registrar el préstamo 
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setString(1, idUsuario);
                psInsert.setString(2, isbnLibro);
                psInsert.executeUpdate();
            }
            
            // Si todo ha ido bien, guardamos los cambios
            con.commit();
            return true;
            
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { } // Deshacer en caso de error
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ex) { } // Devolver estado original
        }
    }
}