package frsf.isi.dam.gtm.miagenda.entidades;

import java.io.Serializable;

public class Direccion implements Serializable {

    private String pais, provincia, ciudad, calle, numero, departamento = "";

    public Direccion() {
    }

    public Direccion(String pais, String provincia, String ciudad, String calle, String numero, String departamento) {
        this.pais = pais;
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.calle = calle;
        this.numero = numero;
        this.departamento = departamento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public String toString() {
        return "Direccion{" + '\n' +
                "pais='" + pais + "'\n" +
                ", provincia='" + provincia + "'\n" +
                ", ciudad='" + ciudad + "'\n" +
                ", calle='" + calle + "'\n" +
                ", numero='" + numero + "'\n" +
                ", departamento='" + departamento + "'\n" +
                '}';
    }

    public String getStringForMap(){
        return pais+", "+provincia+", "+ciudad+", "+calle+" "+numero;
    }

    public String getStringToShow(){
        return calle+" "+numero+" Dpto: "+departamento+", "+ciudad;
    }

}
