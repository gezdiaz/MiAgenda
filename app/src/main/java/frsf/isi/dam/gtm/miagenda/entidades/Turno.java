package frsf.isi.dam.gtm.miagenda.entidades;

import java.util.Date;
import java.util.Objects;

public class Turno {

    private String id;
    private String descripcion;
    private String nombrePaciente;
    private Date fecha;
    private String propietario;

    public Turno() {
    }

    public Turno(String descripcion, String nombrePaciente, Date fechaHora) {
        this.descripcion = descripcion;
        this.nombrePaciente = nombrePaciente;
        this.fecha = fechaHora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fechaHora) {
        this.fecha = fechaHora;
    }

    @Override
    public String toString() {
        return "Turno{" + '\n' +
                "id='" + id + "'\n" +
                ", descripcion='" + descripcion + "'\n" +
                ", nombrePaciente='" + nombrePaciente + "'\n" +
                ", fechaHora=" + fecha + '\n' +
                ", propietario='" + propietario + "'\n" +
                '}';
    }

}
