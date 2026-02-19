/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author ayoub
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    
    // 1. Instancia estática y privada de la conexión
    private static Connection conexion = null;
    
    // 2. Constructor privado para evitar que alguien haga "new ConexionDB()"
    private ConexionDB() {
    }
    
    // 3. Método público y estático para obtener la única conexión
    public static Connection getConexion() {
        // Si la conexión no existe o se ha cerrado, la creamos
        try {
            if (conexion == null || conexion.isClosed()) {
                // Ajusta el nombre de la base de datos según tu script ("biblioteca_examen")
                String url = "jdbc:mysql://localhost:3306/biblioteca_examen";
                String user = "root";
                String password = "9r4tePrP7"; 
                
                conexion = DriverManager.getConnection(url, user, password);
                System.out.println("✅ Conexión a la base de datos establecida.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la BD: " + e.getMessage());
        }
        return conexion;
    }
}