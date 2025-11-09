package Clases_Java;

import Enums.TipoUsuario;

import java.util.Objects;

public class Medico extends Persona {
    private String especialidad;
    private String matricula; // opcional, si querés agregar

    public Medico(int id, String nombre, String apellido, int dni, int telefono,
                  String email, String contrasena, String especialidad, String matricula) {
        super(id, nombre, apellido, dni, telefono, email, contrasena, TipoUsuario.MEDICO);
        this.especialidad = especialidad;
        this.matricula = matricula;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    @Override
    public String toString() {
        return super.toString() + ", Especialidad: " + especialidad + (matricula != null ? " | Matrícula: " + matricula : "");
    }
}
