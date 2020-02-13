package frsf.isi.dam.gtm.miagenda.entidades;

import java.util.Date;

public class Turno {

    private String id;
    private String descripcion;
    private String nombrePaciente;
    private String dniPaciente;
    private Date fecha;
    private String propietario;
    private int posicion;
    private boolean disponible;

    public Turno() {
        disponible = true;
    }

    public Turno(String descripcion, String nombrePaciente, String dniPaciente, Date fecha) {
        this.descripcion = descripcion;
        this.nombrePaciente = nombrePaciente;
        this.dniPaciente = dniPaciente;
        this.fecha = fecha;
        this.disponible = false;
    }

    public String getDniPaciente() {
        return dniPaciente;
    }

    public void setDniPaciente(String dniPaciente) {
        this.dniPaciente = dniPaciente;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
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

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Turno{" +
                "id='" + id + "\'\n" +
                ", descripcion='" + descripcion + "\'\n" +
                ", nombrePaciente='" + nombrePaciente + "\'\n" +
                ", fecha=" + fecha + '\n' +
                ", propietario='" + propietario + "\'\n" +
                ", posicion=" + posicion + '\n' +
                ", disponible=" + disponible + '\n' +
                "}\n";
    }
}
