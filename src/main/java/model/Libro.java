/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author ayoub
 */

import java.sql.Date;


public class Libro {
    
    // Atributos mínimos exigidos por el enunciado
    private String isbn;
    private String titulo;
    private String autor;
    private String genero;
    private Date fechaPublicacion;
    private String rutaPortada;
    private String rutaResumen;
    
    // Atributo lógico para gestionar si se puede prestar o reservar
    private int copiasDisponibles;

    public Libro() {
    }

    public Libro(String isbn, String titulo, String autor, String genero, Date fechaPublicacion, String rutaPortada, String rutaResumen, int copiasDisponibles) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.fechaPublicacion = fechaPublicacion;
        this.rutaPortada = rutaPortada;
        this.rutaResumen = rutaResumen;
        this.copiasDisponibles = copiasDisponibles;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public String getRutaPortada() { return rutaPortada; }
    public void setRutaPortada(String rutaPortada) { this.rutaPortada = rutaPortada; }

    public String getRutaResumen() { return rutaResumen; }
    public void setRutaResumen(String rutaResumen) { this.rutaResumen = rutaResumen; }

    public int getCopiasDisponibles() { return copiasDisponibles; }
    public void setCopiasDisponibles(int copiasDisponibles) { this.copiasDisponibles = copiasDisponibles; }

    @Override
    public String toString() {
        return titulo + " - " + autor;
    }
}