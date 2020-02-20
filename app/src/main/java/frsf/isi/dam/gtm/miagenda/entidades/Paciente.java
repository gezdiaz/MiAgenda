package frsf.isi.dam.gtm.miagenda.entidades;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Paciente implements Serializable {

    private String id; //El id es distinto del DNI para que no haya problemas al editar el DNI.
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

    public Paciente() {
    }

    public Paciente(String nombre, String apellido, String obraSocial, Date fechaNacimiento, String dni, Long telefono, String pais, String provincia, String ciudad, String calle, String numero, String departamento) {
        //la foto se setea después de que se guarde en Firebase Storage.
        this.nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        this.apellido = apellido.substring(0, 1).toUpperCase() + apellido.substring(1);
        this.obraSocial = obraSocial;
        this.fechaNacimiento = fechaNacimiento;
        this.dni = dni;
        this.telefono = telefono;
        this.direccion = new Direccion(pais, provincia, ciudad, calle, numero, departamento);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido.substring(0, 1).toUpperCase() + apellido.substring(1);
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

}
