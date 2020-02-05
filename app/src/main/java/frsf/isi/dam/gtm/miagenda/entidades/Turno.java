package frsf.isi.dam.gtm.miagenda.entidades;

import java.util.Date;
import java.util.Objects;

public class Turno {

    private String id;
    private String descripcion;
    private String nombrePaciente;
    private Date fechaHora;

    public Turno() {
    }

    public Turno(String descripcion, String nombrePaciente, Date fechaHora) {
        this.descripcion = descripcion;
        this.nombrePaciente = nombrePaciente;
        this.fechaHora = fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "descripcion='" + descripcion + '\'' +
                ", nombrePaciente='" + nombrePaciente + '\'' +
                ", fechaHora=" + fechaHora +
                '}';
    }

}
