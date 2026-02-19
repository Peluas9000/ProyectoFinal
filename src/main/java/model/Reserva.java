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

public class Reserva {
    private int idReserva;
    private String usuarioId;
    private String libroIsbn;
    private Date fechaReserva;
    private String estado; // Ejemplo: "PENDIENTE", "COMPLETADA"

    public Reserva() {
    }

    public Reserva(int idReserva, String usuarioId, String libroIsbn, Date fechaReserva, String estado) {
        this.idReserva = idReserva;
        this.usuarioId = usuarioId;
        this.libroIsbn = libroIsbn;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
    }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getLibroIsbn() { return libroIsbn; }
    public void setLibroIsbn(String libroIsbn) { this.libroIsbn = libroIsbn; }

    public Date getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(Date fechaReserva) { this.fechaReserva = fechaReserva; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}