package Repositorios;

import Clases_Java.Paciente;
import Interfaz.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class PacienteRepositorio implements Repositorio<Paciente> {
    private List<Paciente> pacientes;

    public PacienteRepositorio() {
        this.pacientes = new ArrayList<>();
    }

    @Override
    public void agregar(Paciente paciente) {
        pacientes.add(paciente);
    }

    @Override
    public Paciente buscarPorId(int id) {
        for (Paciente p : pacientes) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    @Override
    public void modificar(Paciente paciente) {
        for (int i = 0; i < pacientes.size(); i++) {
            if (pacientes.get(i).getId() == paciente.getId()) {
                pacientes.set(i, paciente);
                break;
            }
        }
    }

    @Override
    public List<Paciente> listar() {
        return pacientes;
    }

    @Override
    public void eliminar(int id) {
        for (Paciente p : pacientes) {
            if (p.getId() == id) {
                pacientes.remove(p);
                break;
            }
        }
    }
}
