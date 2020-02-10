package frsf.isi.dam.gtm.miagenda.entidades;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Map;

public class Paciente implements Serializable {

    private String id;
    private String nombre;
    private String apellido;
    private String obraSocial;
    private Date fechaNacimiento;
    private String dni;
    private Long telefono;
    private String fotoURL;
    private Direccion direccion;
    //El paciente no tiene una lista de turnos porque se guardan en una colección turnos dentro de cada documento paciente
    //Entonces para obtener los turnos de un paciente hay que hacer una consulta a Firestore.
    //private List<Turno> turnos;

    public Paciente() {
    }

    public Paciente(String nombre, String apellido, String obraSocial, Date fechaNacimiento, String dni, Long telefono, String pais, String provincia, String ciudad, String calle, String numero, String departamento) {
        //TODO la foto se setea después de que se guarde en Firebase Storage.
        this.nombre = nombre;
        this.apellido = apellido;
        this.obraSocial = obraSocial;
        this.fechaNacimiento = fechaNacimiento;
        this.dni = dni;
        this.telefono = telefono;
        this.direccion = new Direccion(pais, provincia, ciudad, calle, numero, departamento);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getFotoURL() {
        return fotoURL;
    }

    public void setFotoURL(String fotoURL) {
        this.fotoURL = fotoURL;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

//    public List<Turno> getTurnos() {
//        return turnos;
//    }
//
//    public void setTurnos(List<Turno> turnos) {
//        this.turnos = turnos;
//    }

    @Override
    public String toString() {
        return "Paciente{" + "'\n" +
                "nombre='" + nombre + "'\n" +
                ", apellido='" + apellido + "'\n" +
                ", obraSocial='" + obraSocial + "'\n" +
                ", fechaNacimiento=" + fechaNacimiento + '\n' +
                ", dni=" + dni + '\n' +
                ", teléfono=" + telefono + '\n' +
                ", fotoURL='" + fotoURL + "'\n" +
                ", dirección=" + direccion + "\n" +
                '}';
    }

    public String getEdad() {
        Calendar fechaNacimiento = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        fechaNacimiento.setTime(this.fechaNacimiento);
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int edad = today.get(Calendar.YEAR)-fechaNacimiento.get(Calendar.YEAR);
        if(today.get(Calendar.DAY_OF_YEAR)<fechaNacimiento.get(Calendar.DAY_OF_YEAR)){
            edad--;
        }
        return String.valueOf(edad);
    }

//    Agregar y quitar turnos también se hace en firestore
//    public boolean addTurno(Turno turno){
//        return this.turnos.add(turno);
//    }
//
//    public boolean removeTurno(Turno turno){
//        return this.turnos.remove(turno);
//    }
}
