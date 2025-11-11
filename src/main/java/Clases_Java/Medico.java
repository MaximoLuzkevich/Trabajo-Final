package Clases_Java;

import Enums.TipoUsuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Medico extends Persona {
    private String especialidad;
    private String matricula;
    private List<Turno> agenda;

    public Medico(int id, String nombre, String apellido, int dni, int telefono,
                  String email, String contrasena, String especialidad, String matricula) {
        super(id, nombre, apellido, dni, telefono, email, contrasena, TipoUsuario.MEDICO);
        this.especialidad = especialidad;
        this.matricula = matricula;
        this.agenda = new ArrayList<>();
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public List<Turno> getAgenda() {
        return agenda;
    }

    public void setAgenda(List<Turno> agenda) {
        this.agenda = agenda;
    }

    public void agregarTurnoALaAgenda(Turno turno) {
        if (turno != null) {
            agenda.add(turno);
        }
    }

    public void eliminarTurnoDeLaAgenda(Turno turno) {
        agenda.remove(turno);
    }

    public void mostrarAgenda() {
        System.out.println("Agenda del Dr. " + getApellido() + " (" + especialidad + "):");
        for (Turno t : agenda) {
            System.out.println(" - " + t);
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Especialidad: " + especialidad +
                ", Matrícula: " + matricula +
                ", Turnos en agenda: " + agenda.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medico)) return false;
        Medico m = (Medico) o;
        return this.getId() == m.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
