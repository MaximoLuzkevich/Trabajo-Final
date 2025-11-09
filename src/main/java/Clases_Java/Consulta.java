package Clases_Java;

import java.util.Objects;

public class Consulta {
    private int id;
    private Turno turno;
    private String diagnostico;
    private String observaciones;

    public Consulta(int id, Turno turno, String diagnostico, String observaciones) {
        this.id = id;
        this.turno = turno;
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    @Override
    public String toString() {
        return "Consulta ID: " + id +
                ", Turno: " + turno.getId() +
                ", Diagnóstico: " + diagnostico +
                ", Observaciones: " + observaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consulta)) return false;
        Consulta consulta = (Consulta) o;
        return id == consulta.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
