package Clases_Java;

import Enums.TipoUsuario;

import java.util.Objects;

public class Paciente extends Persona {
    private String obraSocial;

    public Paciente(int id, String nombre, String apellido, int dni, int telefono,
                    String email, String contrasena, String obraSocial) {
        super(id, nombre, apellido, dni, telefono, email, contrasena, TipoUsuario.PACIENTE);
        this.obraSocial = obraSocial;
    }

    public String getObraSocial() { return obraSocial; }
    public void setObraSocial(String obraSocial) { this.obraSocial = obraSocial; }

    @Override
    public String toString() {
        return super.toString() + ", Obra Social: " + obraSocial;
    }
}
