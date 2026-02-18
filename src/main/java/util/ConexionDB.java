/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author ayoub
 */



public class ConexionDB {
    
    // 1. Instancia estática privada de la propia clase
    private static Connection conexion = null;
    
    // 2. Constructor privado para que nadie pueda hacer un "new ConexionDB()"
    private ConexionDB() { }
    
    // 3. Método estático público que devuelve la conexión
    public static Connection getConexion() {
        if (conexion == null) {
            try {
                // Ajusta la URL, usuario y contraseña a los de tu XAMPP/MySQL
                String url = "jdbc:mysql://localhost:3306/biblioteca_examen";
                String user = "root";
                String password = ""; 
                
                conexion = DriverManager.getConnection(url, user, password);
                System.out.println("Conexión a BD establecida con éxito.");
            } catch (SQLException e) {
                System.err.println("Error al conectar a la BD: " + e.getMessage());
            }
        }
        return conexion;
    }
}