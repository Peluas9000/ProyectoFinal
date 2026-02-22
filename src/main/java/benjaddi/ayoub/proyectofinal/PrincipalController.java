/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package benjaddi.ayoub.proyectofinal;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class PrincipalController {

    // Este es el StackPane de la derecha que dejamos vacío en el FXML
    @FXML
    private StackPane panelContenedor;

    // Al iniciar la aplicación, podemos cargar el Login por defecto
    @FXML
    public void initialize() {
        cargarVista("inicioSesion.fxml"); 
    }

    @FXML
    void mostrarLogin(ActionEvent event) {
        cargarVista("inicioSesion.fxml"); // Asegúrate de que el nombre del archivo coincida con el tuyo
    }

    @FXML
    void mostrarRegistro(ActionEvent event) {
        cargarVista("registro.fxml");
    }

    
    private void cargarVista(String archivoFxml) {
        try {
            // Carga el archivo FXML
            Parent vista = FXMLLoader.load(getClass().getResource("/fxml/" + archivoFxml)); // Ajusta la ruta si está en una carpeta como /vistas/
            
            // Limpia lo que haya actualmente en la pantalla derecha
            panelContenedor.getChildren().clear();
            
            // Añade la nueva vista
            panelContenedor.getChildren().add(vista);
            
        } catch (IOException e) {
            System.out.println("Error al cargar la vista " + archivoFxml + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}