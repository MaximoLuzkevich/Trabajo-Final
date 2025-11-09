package Clases_Java;

import Enums.TipoUsuario;

public class Recepcionista extends Persona {
    private int legajo;

    public Recepcionista(int id, String nombre, String apellido, int dni, int telefono,
                         String email, String contrasena, int legajo) {
        super(id, nombre, apellido, dni, telefono, email, contrasena, TipoUsuario.RECEPCIONISTA);
        this.legajo = legajo;
    }

    public int getLegajo() { return legajo; }
    public void setLegajo(int legajo) { this.legajo = legajo; }

    @Override
    public String toString() {
        return super.toString() + ", Legajo: " + legajo;
    }
}
