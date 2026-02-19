/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package benjaddi.ayoub.proyectofinal;


import dao.LibroDao;
import dao.PrestamoDao;
import dao.ReservaDao;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.DatosGlobales;
import model.Libro;
import model.Prestamo;

public class EstudianteViewController implements Initializable {

    // ==========================================
    // INYECCIONES FXML - CABECERA
    // ==========================================
    @FXML private Label lblBienvenida;

    // ==========================================
    // INYECCIONES FXML - PESTAÑA CATÁLOGO (Izquierda)
    // ==========================================
    @FXML private ComboBox<String> comboFiltroTipo;
    @FXML private CheckBox chkDisponibles;
    @FXML private TableView<Libro> tblCatalogo;
    @FXML private TableColumn<Libro, String> colCatTitulo, colCatAutor, colCatGenero;
    @FXML private TableColumn<Libro, Integer> colCatDisponibilidad;

    // ==========================================
    // INYECCIONES FXML - PESTAÑA CATÁLOGO (Derecha - Detalles)
    // ==========================================
    @FXML private ImageView imgPortada;
    @FXML private Label lblDetalleTitulo, lblDetalleAutor, lblDetalleResumen;
    @FXML private Button btnPrestar, btnReservar;

    // ==========================================
    // INYECCIONES FXML - PESTAÑA MIS PRÉSTAMOS
    // ==========================================
    @FXML private TableView<Prestamo> tblMisPrestamos;
    @FXML private TableColumn<Prestamo, String> colMiPrestamoTitulo, colMiPrestamoEstado;
    @FXML private TableColumn<Prestamo, Date> colMiPrestamoFecha, colMiPrestamoFin;

    // Instancias de DAOs
    private LibroDao libroDao = new LibroDao();
    private PrestamoDao prestamoDao = new PrestamoDao();
    private ReservaDao reservaDao = new ReservaDao();

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Mensaje de bienvenida personalizado
        if (DatosGlobales.usuarioLogueado != null) {
            lblBienvenida.setText("Portal de: " + DatosGlobales.usuarioLogueado.getNombre());
        }

        // 2. Configurar componentes
        configurarTablas();
        llenarComboBoxGeneros();
        
        // 3. Cargar datos iniciales
        aplicarFiltros(null);
        cargarMisPrestamos();
        
        // 4. Desactivar botones de acción hasta que seleccione un libro
        btnPrestar.setDisable(true);
        btnReservar.setDisable(true);

        // 5. Listener: Qué pasa al hacer clic en un libro del catálogo
        tblCatalogo.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesLibro(newSelection);
            }
        });
        
            // Escuchar cambios en el desplegable de categorías
        comboFiltroTipo.valueProperty().addListener((obs, oldValue, newValue) -> {
            aplicarFiltros(null);
        });

        // Escuchar cambios en el CheckBox de "Solo disponibles"
        chkDisponibles.selectedProperty().addListener((obs, oldValue, newValue) -> {
            aplicarFiltros(null);
        });
    }

    private void configurarTablas() {
        // Tabla Catálogo
        colCatTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colCatAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colCatGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colCatDisponibilidad.setCellValueFactory(new PropertyValueFactory<>("copiasDisponibles"));

        // Tabla Mis Préstamos
        // Nota Examen: Usamos 'libroIsbn' porque el POJO Prestamo no tiene el título. 
        // Mostrará el ISBN en la tabla, que para 3 horas de examen es perfectamente válido.
        colMiPrestamoTitulo.setCellValueFactory(new PropertyValueFactory<>("libroIsbn")); 
        colMiPrestamoFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colMiPrestamoFin.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
        colMiPrestamoEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void llenarComboBoxGeneros() {
        ObservableList<String> generos = FXCollections.observableArrayList(
                "Todos", "Ficción", "No Ficción", "Ciencia", "Historia", "Fantasía", "Clásico"
        );
        comboFiltroTipo.setItems(generos);
        comboFiltroTipo.setValue("Todos");
    }

    // ==========================================
    // LÓGICA DE LA PESTAÑA 1: CATÁLOGO
    // ==========================================
    @FXML
    void aplicarFiltros(ActionEvent event) {
        String genero = comboFiltroTipo.getValue();
        boolean soloDisponibles = chkDisponibles.isSelected();
        
        List<Libro> librosFiltrados = libroDao.obtenerLibros(genero, soloDisponibles);
        tblCatalogo.setItems(FXCollections.observableArrayList(librosFiltrados));
    }

    private void mostrarDetallesLibro(Libro libro) {
        lblDetalleTitulo.setText(libro.getTitulo());
        lblDetalleAutor.setText(libro.getAutor());
        lblDetalleResumen.setText(libro.getRutaResumen() != null ? "Archivo: " + libro.getRutaResumen() : "Sin resumen");

        // Cargar imagen (si hay ruta). En un examen, envuélvelo en try-catch por si falla la ruta
        try {
            if (libro.getRutaPortada() != null && !libro.getRutaPortada().isEmpty()) {
                File file = new File(libro.getRutaPortada());
                if(file.exists()){
                    imgPortada.setImage(new Image(file.toURI().toString()));
                } else {
                    imgPortada.setImage(null); // Imagen por defecto si no existe
                }
            }
        } catch (Exception e) {
            imgPortada.setImage(null);
        }

        // Lógica de botones: Prestar si hay stock, Reservar si no hay
        if (libro.getCopiasDisponibles() > 0) {
            btnPrestar.setDisable(false);
            btnReservar.setDisable(true);
        } else {
            btnPrestar.setDisable(true);
            btnReservar.setDisable(false);
        }
    }

    @FXML
    void pedirPrestado(ActionEvent event) {
        Libro seleccionado = tblCatalogo.getSelectionModel().getSelectedItem();
        if (seleccionado != null && seleccionado.getCopiasDisponibles() > 0) {
            
            String idUsuario = DatosGlobales.usuarioLogueado.getUsuario();
            boolean exito = prestamoDao.registrarPrestamo(idUsuario, seleccionado.getIsbn());
            
            if (exito) {
                mostrarAlerta("Éxito", "Acabas de pedir prestado: " + seleccionado.getTitulo());
                aplicarFiltros(null); // Refresca catálogo (para ver el nuevo stock)
                cargarMisPrestamos(); // Refresca lista de préstamos propios
                btnPrestar.setDisable(true); // Evitar doble clic
            } else {
                mostrarAlerta("Error", "No se pudo procesar el préstamo.");
            }
        }
    }

    @FXML
    void reservarLibro(ActionEvent event) {
        Libro seleccionado = tblCatalogo.getSelectionModel().getSelectedItem();
        if (seleccionado != null && seleccionado.getCopiasDisponibles() == 0) {
            
            String idUsuario = DatosGlobales.usuarioLogueado.getUsuario();
            boolean exito = reservaDao.crearReserva(idUsuario, seleccionado.getIsbn());
            
            if (exito) {
                mostrarAlerta("Éxito", "Has entrado en la lista de reservas para: " + seleccionado.getTitulo());
                btnReservar.setDisable(true);
            } else {
                mostrarAlerta("Error", "No se pudo realizar la reserva.");
            }
        }
    }

    // ==========================================
    // LÓGICA DE LA PESTAÑA 2: MIS PRÉSTAMOS
    // ==========================================
    private void cargarMisPrestamos() {
        if (DatosGlobales.usuarioLogueado != null) {
            String miUsuario = DatosGlobales.usuarioLogueado.getUsuario();
            List<Prestamo> misPrestamos = prestamoDao.obtenerPrestamosPorEstudiante(miUsuario);
            tblMisPrestamos.setItems(FXCollections.observableArrayList(misPrestamos));
        }
    }

    @FXML
    void devolverLibro(ActionEvent event) {
        Prestamo seleccionado = tblMisPrestamos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            boolean exito = prestamoDao.devolverLibro(seleccionado.getIdPrestamo(), seleccionado.getLibroIsbn());
            if (exito) {
                mostrarAlerta("Éxito", "Libro devuelto correctamente. ¡Gracias!");
                cargarMisPrestamos();
                aplicarFiltros(null); // Para que suba el stock en la otra pestaña
            } else {
                mostrarAlerta("Error", "No se pudo procesar la devolución.");
            }
        } else {
            mostrarAlerta("Aviso", "Selecciona un préstamo de la tabla para devolver.");
        }
    }

    @FXML
    void prorrogarPrestamo(ActionEvent event) {
        Prestamo seleccionado = tblMisPrestamos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (seleccionado.getEstado().equals("PRORROGADO")) {
                mostrarAlerta("Aviso", "Este libro ya ha sido prorrogado previamente. No puedes volver a hacerlo.");
                return;
            }
            
            boolean exito = prestamoDao.prorrogarPrestamo(seleccionado.getIdPrestamo());
            if (exito) {
                mostrarAlerta("Éxito", "Has prorrogado el préstamo 15 días más.");
                cargarMisPrestamos();
            } else {
                mostrarAlerta("Error", "No se pudo prorrogar el préstamo.");
            }
        } else {
            mostrarAlerta("Aviso", "Selecciona un préstamo de la tabla para prorrogar.");
        }
    }

    // ==========================================
    // SISTEMA Y UTILIDADES
    // ==========================================
    @FXML
    void cerrarSesion(ActionEvent event) {
        DatosGlobales.usuarioLogueado = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/principal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio de Sesión");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}