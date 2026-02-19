package benjaddi.ayoub.proyectofinal;

import dao.UsuarioDao;
import model.Usuario;
import model.DatosGlobales; // Asegúrate de tener esta clase para guardar la sesión

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ayoub
 */
public class InicioSesionController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    
    UsuarioDao udao = new UsuarioDao();

    @FXML
    void iniciarSesion(ActionEvent event){
        String usuario = txtUsuario.getText();
        String pass = txtPassword.getText();
            
        if(usuario.isEmpty() || pass.isEmpty()){
            lblError.setText("Rellena los campos, no vacios");
           
        } else {
            // 1. Usamos la instancia 'udao' para llamar al método
            Usuario usuarioValidado = udao.autenticar(usuario, pass);
            
            // 2. Comprobamos si el usuario existe
            if (usuarioValidado != null) {
                // Guardamos el usuario en la sesión global
                DatosGlobales.usuarioLogueado = usuarioValidado;
                
                // 3. Redirigimos según el rol [cite: 4, 5]
                if (usuarioValidado.getRol().equalsIgnoreCase("ADMIN")) {
                    cambiarVentana(event, "/fxml/adminView.fxml", "Panel de Administrador"); 
                } else if (usuarioValidado.getRol().equalsIgnoreCase("ESTUDIANTE")) {
                    cambiarVentana(event, "/fxml/estudianteView.fxml", "Portal del Estudiante"); 
                }
            } else {
                lblError.setText("Usuario o contraseña incorrectos");
            }
        }
    }
    
    /**
     * Método auxiliar para no repetir el código de cambiar de ventana
     */
    private void cambiarVentana(ActionEvent event, String rutaFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            lblError.setText("Error interno al cargar la interfaz.");
        }
    }
}