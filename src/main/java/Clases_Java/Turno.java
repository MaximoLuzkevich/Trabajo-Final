package Clases_Java;

import Enums.EstadoTurno;

import java.time.LocalDateTime;
import java.util.Objects;

public class Turno {
    private int id;
    private Paciente paciente;
    private Medico medico;
    private LocalDateTime fechaHora;
    private EstadoTurno estado;

    public Turno(int id, Paciente paciente, Medico medico, LocalDateTime fechaHora, EstadoTurno estado) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public EstadoTurno getEstado() { return estado; }
    public void setEstado(EstadoTurno estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Turno ID: " + id +
                ", Paciente: " + paciente.getNombre() +
                ", Médico: " + medico.getNombre() +
                ", Fecha: " + fechaHora +
                ", Estado: " + estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Turno)) return false;
        Turno turno = (Turno) o;
        return id == turno.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
