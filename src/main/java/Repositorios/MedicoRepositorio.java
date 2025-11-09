package Repositorios;

import Clases_Java.Medico;
import Interfaz.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class MedicoRepositorio implements Repositorio<Medico> {
    private List<Medico> medicos;

    public MedicoRepositorio() {
        this.medicos = new ArrayList<>();
    }

    @Override
    public void agregar(Medico medico) {
        medicos.add(medico);
    }

    @Override
    public Medico buscarPorId(int id) {
        for (Medico m : medicos) {
            if (m.getId() == id) return m;
        }
        return null;
    }

    @Override
    public void modificar(Medico medico) {
        for (int i = 0; i < medicos.size(); i++) {
            if (medicos.get(i).getId() == medico.getId()) {
                medicos.set(i, medico);
                break;
            }
        }
    }

    @Override
    public List<Medico> listar() {
        return medicos;
    }

    @Override
    public void eliminar(int id) {
        for (Medico m : medicos) {
            if (m.getId() == id) {
                medicos.remove(m);
                break;
            }
        }
    }
}
