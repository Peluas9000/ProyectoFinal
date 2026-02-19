module benjaddi.ayoub.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    opens benjaddi.ayoub.proyectofinal to javafx.fxml;
    opens model to javafx.base;    
    exports benjaddi.ayoub.proyectofinal;
}
