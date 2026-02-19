/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package benjaddi.ayoub.proyectofinal;

import dao.UsuarioDao;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author ayoub
 */import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Usuario;

public class RegistroController {

    @FXML
    private TextField txtNombreRegistro;

    @FXML
    private TextField txtUsuarioRegistro;

    @FXML
    private PasswordField txtPasswordRegistro;

    @FXML
    private TextField txtEmailRegistro;
    @FXML
    private CheckBox admin;
    @FXML
    private TextField txtapellidos;
    // Supongo que tienes una clase DAO para los usuarios, ajústalo a tu nombre real
    private UsuarioDao usuarioDao = new UsuarioDao();
    
    @FXML
    void registrarEstudiante(ActionEvent event) {
        // 1. Obtener los textos de los campos
        String nombre = txtNombreRegistro.getText();
        String usuario = txtUsuarioRegistro.getText();
        String password = txtPasswordRegistro.getText();
        String email = txtEmailRegistro.getText();
        String apellidos=txtapellidos.getText();
        // 2. Validar que no haya campos vacíos
        if (nombre.isEmpty() || usuario.isEmpty() || password.isEmpty() || email.isEmpty()||apellidos.isEmpty() ) {
            mostrarAlerta("Error de validación", "Por favor, rellena todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        // 3. Crear el objeto Usuario (Ajusta los atributos a tu clase modelo real)
        Usuario nuevoEstudiante = new Usuario();
        nuevoEstudiante.setNombre(nombre);
        nuevoEstudiante.setUsuario(usuario);
        nuevoEstudiante.setPassword(password); // Nota: En un proyecto real, la contraseña debería ir encriptada
        nuevoEstudiante.setEmail(email);
        nuevoEstudiante.setApellidos(email);
        if(admin.isSelected()){
        nuevoEstudiante.setRol("ADMIN"); // Le asignamos el rol por defecto

        }else{
            nuevoEstudiante.setRol("ESTUDIANTE");
        }
        
        // 4. Llamar al DAO para guardarlo en la base de datos
        boolean exito = usuarioDao.insertarUsuario(nuevoEstudiante);

        // 5. Comprobar si se guardó correctamente
        if (exito) {
            mostrarAlerta("¡Éxito!", "Usuario registrado correctamente. Ya puedes iniciar sesión.", Alert.AlertType.INFORMATION);
            limpiarFormulario();
        } else {
            mostrarAlerta("Error", "No se pudo registrar. ¿Quizás el usuario o correo ya existen?", Alert.AlertType.ERROR);
        }
    }

    // --- Métodos Auxiliares ---

    private void limpiarFormulario() {
        txtNombreRegistro.clear();
        txtUsuarioRegistro.clear();
        txtPasswordRegistro.clear();
        txtEmailRegistro.clear();
        admin.setSelected(false); // Desmarca la casilla
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}