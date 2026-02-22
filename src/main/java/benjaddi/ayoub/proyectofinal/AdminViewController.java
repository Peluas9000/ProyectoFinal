/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package benjaddi.ayoub.proyectofinal;



import dao.LibroDao;
import dao.PrestamoDao;
import dao.ReservaDao;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DatosGlobales;
import model.Libro;
import model.Prestamo;
import model.Reserva;

public class AdminViewController implements Initializable {

    // ==========================================
    // INYECCIONES FXML - PESTAÑA LIBROS
    // ==========================================
    @FXML private ComboBox<String> comboFiltroGenero;
    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colIsbn, colTitulo, colAutor, colGenero;
    @FXML private TableColumn<Libro, Integer> colStock;

    @FXML private TextField txtIsbn, txtTitulo, txtAutor, txtStock,txtRutaPortada,txtArchivoTxt,txtResumen;
    @FXML private ComboBox<String> comboGeneroForm;
    @FXML private DatePicker dpFechaPublicacion;

    // ==========================================
    // INYECCIONES FXML - PESTAÑA PRÉSTAMOS
    // ==========================================
    @FXML private TextField txtFiltroEstudiante;
    @FXML private TableView<Prestamo> tblPrestamos;
    @FXML private TableColumn<Prestamo, Integer> colPrestamoId;
    @FXML private TableColumn<Prestamo, String> colPrestamoUsuario, colPrestamoIsbn, colPrestamoEstado;
    @FXML private TableColumn<Prestamo, Date> colPrestamoFecha, colPrestamoDevolucion;

    // ==========================================
    // INYECCIONES FXML - PESTAÑA RESERVAS
    // ==========================================
    @FXML private TableView<Reserva> tblReservas;
    @FXML private TableColumn<Reserva, Integer> colReservaId;
    @FXML private TableColumn<Reserva, String> colReservaUsuario, colReservaIsbn, colReservaEstado;
    @FXML private TableColumn<Reserva, Date> colReservaFecha;

    // Instancias de los DAOs
    private LibroDao libroDao = new LibroDao();
    private PrestamoDao prestamoDao = new PrestamoDao();
    private ReservaDao reservaDao = new ReservaDao();

    // ==========================================
    // MÉTODO DE INICIO (Se ejecuta al abrir la ventana)
    // ==========================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablas();
        llenarComboBoxes();
        cargarTodasLasTablas();
        
        comboFiltroGenero.valueProperty().addListener((obs, oldValue, newValue) -> {
            refrescarTablaLibros();                       
        });
        
        // Detectar clics en la tabla de Libros para pasar los datos al formulario
        tblLibros.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                pasarDatosAFormulario(newSelection);
            }
        });

        // Detectar escritura en el buscador de estudiantes para filtrar en tiempo real
        txtFiltroEstudiante.textProperty().addListener((obs, oldText, newText) -> {
            buscarPrestamosPorEstudiante();
        });
    }

    // ==========================================
    // MÉTODOS DE CONFIGURACIÓN
    // ==========================================
    private void configurarTablas() {
        // Enlazar columnas de Libros con atributos del POJO
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("copiasDisponibles"));

        // Enlazar columnas de Préstamos
        colPrestamoId.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        colPrestamoUsuario.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
        colPrestamoIsbn.setCellValueFactory(new PropertyValueFactory<>("libroIsbn"));
        colPrestamoFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPrestamo"));
        colPrestamoDevolucion.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
        colPrestamoEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Enlazar columnas de Reservas
        colReservaId.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colReservaUsuario.setCellValueFactory(new PropertyValueFactory<>("usuarioId"));
        colReservaIsbn.setCellValueFactory(new PropertyValueFactory<>("libroIsbn"));
        colReservaFecha.setCellValueFactory(new PropertyValueFactory<>("fechaReserva"));
        colReservaEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void llenarComboBoxes() {
        ObservableList<String> generos = FXCollections.observableArrayList(
                "Todos", "Ficción", "No Ficción", "Ciencia", "Historia", "Fantasía", "Clásico"
        );
        comboFiltroGenero.setItems(generos);
        comboFiltroGenero.setValue("Todos"); // Valor por defecto
        
        // El del formulario no necesita "Todos"
        ObservableList<String> generosForm = FXCollections.observableArrayList(
                "Ficción", "No Ficción", "Ciencia", "Historia", "Fantasía", "Clásico"
        );
        comboGeneroForm.setItems(generosForm);
    }

    private void cargarTodasLasTablas() {
        refrescarTablaLibros();
        buscarPrestamosPorEstudiante(); // Al estar el texto vacío, carga todos
        cargarTablaReservas();
    }

    // ==========================================
    // ACCIONES DE LA INTERFAZ - PESTAÑA LIBROS
    // ==========================================
    @FXML
void guardarLibro(ActionEvent event) {
    // Validación básica
    if (txtIsbn.getText().isEmpty() || txtTitulo.getText().isEmpty()) {
        mostrarAlerta("Error", "El ISBN y el Título son obligatorios.");
        return;
    }

    // 1. GENERACIÓN DEL ARCHIVO TXT (Resumen)
    String rutaTxt = txtArchivoTxt.getText();
    String contenidoResumen = txtResumen.getText();

    if (rutaTxt != null && !rutaTxt.isEmpty()) {
        try {
            // Se asume que el usuario pone "libro1.txt" y nosotros le añadimos la carpeta
            File archivoTxt = new File("resumen/" + rutaTxt);
            
            // Medida de seguridad: Si la carpeta "resumen" no existe, la crea.
            if (archivoTxt.getParentFile() != null) {
                archivoTxt.getParentFile().mkdirs(); 
            }
            
            // El PrintWriter dentro de los paréntesis (try-with-resources) se cierra y guarda automáticamente
            try (PrintWriter fr = new PrintWriter(archivoTxt)) {
                fr.println(contenidoResumen);
            }
            
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo generar el archivo de resumen: " + e.getMessage());
            return; // Detenemos el guardado si falla la creación del archivo
        }
    }

    // 2. CREAR OBJETO LIBRO
    Libro libro = new Libro();
    libro.setIsbn(txtIsbn.getText());
    libro.setTitulo(txtTitulo.getText());
    libro.setAutor(txtAutor.getText());
    libro.setGenero(comboGeneroForm.getValue());
    
    // Controlar posibles errores si el stock no es un número
    try {
        libro.setCopiasDisponibles(Integer.parseInt(txtStock.getText()));
    } catch (NumberFormatException e) {
        libro.setCopiasDisponibles(0); // Valor por defecto si está vacío o mal escrito
    }
    
    // Nuevos campos de rutas
    libro.setRutaPortada(txtRutaPortada.getText());
    libro.setRutaResumen(rutaTxt); 
    
    // Convertir LocalDate (del DatePicker) a java.sql.Date
    if (dpFechaPublicacion.getValue() != null) {
        libro.setFechaPublicacion(java.sql.Date.valueOf(dpFechaPublicacion.getValue()));
    } else {
        libro.setFechaPublicacion(java.sql.Date.valueOf("2000-01-01"));
    }

    // 3. COMPROBAR SI ES NUEVO O ACTUALIZACIÓN
    boolean esNuevo = true;
    for (Libro l : tblLibros.getItems()) {
        if (l.getIsbn().equals(libro.getIsbn())) {
            esNuevo = false;
            break;
        }
    }

    // 4. GUARDAR EN BASE DE DATOS
    boolean exito;
    if (esNuevo) {
        exito = libroDao.insertarLibro(libro);
    } else {
        exito = libroDao.actualizarLibro(libro);
    }

    if (exito) {
        mostrarAlerta("Éxito", "Libro y archivos guardados correctamente.");
        // refrescarTablaLibros(); // Descomenta cuando lo tengas implementado
        // limpiarFormulario(null);
    } else {
        mostrarAlerta("Error", "No se pudo guardar el libro en la base de datos.");
    }
}

    @FXML
    void eliminarLibro(ActionEvent event) {
        Libro seleccionado = tblLibros.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (libroDao.eliminarLibro(seleccionado.getIsbn())) {
                mostrarAlerta("Éxito", "Libro eliminado.");
                refrescarTablaLibros();
                limpiarFormulario(null);
            }
        } else {
            mostrarAlerta("Aviso", "Selecciona un libro de la tabla primero.");
        }
    }

    @FXML
    void limpiarFormulario(ActionEvent event) {
        txtIsbn.clear();
        txtTitulo.clear();
        txtAutor.clear();
        txtStock.clear();
        txtRutaPortada.clear();
        txtArchivoTxt.clear();
        txtResumen.clear();
        comboGeneroForm.setValue(null);
        dpFechaPublicacion.setValue(null);
        tblLibros.getSelectionModel().clearSelection();
    }

    @FXML
    void aplicarFiltroLibros(ActionEvent event) {
        refrescarTablaLibros();
    }

    private void pasarDatosAFormulario(Libro libro) {
        txtIsbn.setText(libro.getIsbn());
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtStock.setText(String.valueOf(libro.getCopiasDisponibles()));
        comboGeneroForm.setValue(libro.getGenero());
        
        if (libro.getFechaPublicacion() != null) {
            dpFechaPublicacion.setValue(libro.getFechaPublicacion().toLocalDate());
        }
    }

    private void refrescarTablaLibros() {
        String filtro = comboFiltroGenero.getValue();
        List<Libro> lista = libroDao.obtenerLibros(filtro, false);
        tblLibros.setItems(FXCollections.observableArrayList(lista));
    }

    // ==========================================
    // ACCIONES DE LA INTERFAZ - PRÉSTAMOS Y RESERVAS
    // ==========================================
    private void buscarPrestamosPorEstudiante() {
        String filtro = txtFiltroEstudiante.getText();
        List<Prestamo> lista = prestamoDao.obtenerTodosLosPrestamos(filtro);
        tblPrestamos.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarTablaReservas() {
        List<Reserva> lista = reservaDao.obtenerTodasLasReservas();
        tblReservas.setItems(FXCollections.observableArrayList(lista));
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
    @FXML
    void limpiarFiltroLibros(ActionEvent event) {
        // Ponemos el ComboBox en "Todos"
        if (comboFiltroGenero != null) {
            comboFiltroGenero.setValue("Todos");
        }
        // Refrescamos la tabla para que muestre todos los libros
        refrescarTablaLibros();
    }
    @FXML
    void convertirReservaEnPrestamo(ActionEvent event) {
        // 1. Obtener la reserva seleccionada en la tabla
        Reserva seleccionada = tblReservas.getSelectionModel().getSelectedItem();
        
        if (seleccionada != null) {
            // 2. Comprobar que el libro tiene copias disponibles antes de prestar
            Libro libro = libroDao.obtenerLibroPorIsbn(seleccionada.getLibroIsbn()); // Necesitarás añadir este método a LibroDao
            
            if (libro != null && libro.getCopiasDisponibles() > 0) {
                // 3. Registrar el préstamo (esto ya resta stock internamente)
                boolean exitoPrestamo = prestamoDao.registrarPrestamo(seleccionada.getUsuarioId(), seleccionada.getLibroIsbn());
                
                if (exitoPrestamo) {
                    // 4. Marcar la reserva como completada (necesitarás este método en ReservaDao) o eliminarla
                    reservaDao.eliminarReserva(seleccionada.getIdReserva()); 
                    
                    mostrarAlerta("Éxito", "Reserva convertida en préstamo correctamente.");
                    // 5. Refrescar todas las tablas
                    cargarTodasLasTablas();
                } else {
                    mostrarAlerta("Error", "No se pudo registrar el préstamo.");
                }
            } else {
                mostrarAlerta("Aviso", "No se puede prestar. El libro actualmente no tiene copias disponibles.");
            }
        } else {
            mostrarAlerta("Aviso", "Selecciona una reserva de la tabla primero.");
        }
    }
    

   
    
}

