package frsf.isi.dam.gtm.miagenda.interfaces.listahistoriaclinica;


// ESTA CLASE SE CREO SOLO PARA PROBAR EL RECYCLER VIEW DE LA HISTORIA CLINICA
//TODO hacer una entidad turno bien

public class Turno {

    private String fecha;
    private String descripcion;
    private String paciente;

    public Turno(String f, String d, String p){
        fecha=f;
        paciente=p;
        descripcion = d;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

}
