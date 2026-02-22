-- 1. CREACIÓN DE LA BASE DE DATOS
CREATE DATABASE IF NOT EXISTS biblioteca_examen;
USE biblioteca_examen;

-- ==========================================
-- 2. TABLA USUARIOS (Mapeada a la clase Usuario.java)
-- ==========================================
CREATE TABLE usuarios (
    usuario VARCHAR(50) PRIMARY KEY,       -- Coincide con private String usuario;
    password VARCHAR(100) NOT NULL,        -- Coincide con private String password;
    rol VARCHAR(20) NOT NULL,              -- Coincide con private String rol; ('ADMIN' o 'ESTUDIANTE')
    nombre VARCHAR(100) NOT NULL,          -- Coincide con private String nombre;
    apellidos VARCHAR(100) NOT NULL,       -- Coincide con private String apellidos;
    email VARCHAR(150) NOT NULL,           -- Coincide con private String email;
    fecha_alta DATE NOT NULL               -- Coincide con private java.sql.Date fechaAlta;
);

-- ==========================================
-- 3. TABLA LIBROS (Mapeada a la clase Libro.java)
-- ==========================================
CREATE TABLE libros (
    isbn VARCHAR(20) PRIMARY KEY,          -- Coincide con private String isbn;
    titulo VARCHAR(150) NOT NULL,          -- Coincide con private String titulo;
    autor VARCHAR(150) NOT NULL,           -- Coincide con private String autor;
    genero VARCHAR(50) NOT NULL,           -- Coincide con private String genero;
    fecha_publicacion DATE NOT NULL,       -- Coincide con private java.sql.Date fechaPublicacion;
    ruta_portada VARCHAR(255),             -- Coincide con private String rutaPortada;
    ruta_resumen VARCHAR(255),             -- Coincide con private String rutaResumen;
    copias_disponibles INT DEFAULT 1       -- Coincide con private int copiasDisponibles;
);

-- ==========================================
-- 4. TABLA PRESTAMOS (Mapeada a la clase Prestamo.java)
-- ==========================================
CREATE TABLE prestamos (
    id_prestamo INT AUTO_INCREMENT PRIMARY KEY, -- Coincide con private int idPrestamo;
    usuario_id VARCHAR(50) NOT NULL,            -- Coincide con private String usuarioId;
    libro_isbn VARCHAR(20) NOT NULL,            -- Coincide con private String libroIsbn;
    fecha_prestamo DATE NOT NULL,               -- Coincide con private java.sql.Date fechaPrestamo;
    fecha_devolucion DATE NOT NULL,             -- Coincide con private java.sql.Date fechaDevolucion;
    estado VARCHAR(20) DEFAULT 'ACTIVO',        -- Coincide con private String estado; ('ACTIVO', 'DEVUELTO', 'PRORROGADO')
    
    -- Claves foráneas para relacionar con usuarios y libros
    FOREIGN KEY (usuario_id) REFERENCES usuarios(usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (libro_isbn) REFERENCES libros(isbn) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 5. TABLA RESERVAS (Mapeada a la clase Reserva.java)
-- ==========================================
CREATE TABLE reservas (
    id_reserva INT AUTO_INCREMENT PRIMARY KEY,  -- Coincide con private int idReserva;
    usuario_id VARCHAR(50) NOT NULL,            -- Coincide con private String usuarioId;
    libro_isbn VARCHAR(20) NOT NULL,            -- Coincide con private String libroIsbn;
    fecha_reserva DATE NOT NULL,                -- Coincide con private java.sql.Date fechaReserva;
    estado VARCHAR(20) DEFAULT 'PENDIENTE',     -- Coincide con private String estado; ('PENDIENTE', 'COMPLETADA')
    
    -- Claves foráneas para relacionar con usuarios y libros
    FOREIGN KEY (usuario_id) REFERENCES usuarios(usuario) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (libro_isbn) REFERENCES libros(isbn) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ==========================================
-- 6. INSERCIÓN DE DATOS DE PRUEBA (Vital para probar el Login al instante)
-- ==========================================
-- Creamos un Administrador y un Estudiante para que puedas entrar
INSERT INTO usuarios (usuario, password, rol, nombre, apellidos, email, fecha_alta) VALUES 
('admin', '1234', 'ADMIN', 'Jefe', 'Biblioteca', 'admin@centro.edu', CURDATE()),
('ayoub', '1234', 'ESTUDIANTE', 'Ayoub', 'Benjaddi', 'ayoub@alumno.edu', CURDATE());

-- Metemos un par de libros para que haya algo que mostrar en las tablas
INSERT INTO libros (isbn, titulo, autor, genero, fecha_publicacion, ruta_portada, ruta_resumen, copias_disponibles) VALUES 
('978-84-450-0066-3', 'El Señor de los Anillos', 'J.R.R. Tolkien', 'Fantasía', '1954-07-29', 'esdla.jpg', 'esdla.pdf', 0); -- Este tiene 0 copias para probar las reservas