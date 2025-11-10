package Clases_Java;

import Enums.TipoUsuario;
import java.util.ArrayList;
import java.util.List;

public class Paciente extends Persona {
    private String obraSocial;
    private List<Consulta> historialMedico;

    public Paciente(int id, String nombre, String apellido, int dni, int telefono,
                    String email, String contrasena, String obraSocial) {
        super(id, nombre, apellido, dni, telefono, email, contrasena, TipoUsuario.PACIENTE);
        this.obraSocial = obraSocial;
        this.historialMedico = new ArrayList<>();
    }

    // Getters y Setters
    public String getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }

    public List<Consulta> getHistorialMedico() {
        return historialMedico;
    }

    public void setHistorialMedico(List<Consulta> historialMedico) {
        this.historialMedico = historialMedico;
    }

    // Métodos específicos
    public void agregarConsultaAlHistorial(Consulta consulta) {
        if (consulta != null) {
            historialMedico.add(consulta);
        }
    }

    public void eliminarConsultaDelHistorial(Consulta consulta) {
        historialMedico.remove(consulta);
    }

    public void mostrarHistorialMedico() {
        System.out.println("Historial médico de " + getNombre() + " " + getApellido() + ":");
        for (Consulta c : historialMedico) {
            System.out.println(" - " + c);
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Obra Social: " + obraSocial +
                ", Consultas en historial: " + historialMedico.size();
    }
}