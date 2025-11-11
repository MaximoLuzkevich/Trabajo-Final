package Clases_Java;

import java.util.Objects;

public class Consulta {
    private int id;
    private Turno turno;
    private String diagnostico;
    private String observaciones;
    private boolean activo;

    public Consulta(int id, Turno turno, String diagnostico, String observaciones) {
        this.id = id;
        this.turno = turno;
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
        this.activo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consulta)) return false;
        Consulta c = (Consulta) o;
        return id == c.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Consulta ID: " + id +
                ", Turno: " + turno.getId() +
                ", Diagnóstico: " + diagnostico +
                ", Observaciones: " + observaciones +
                ", Activo: " + activo;
    }
}
