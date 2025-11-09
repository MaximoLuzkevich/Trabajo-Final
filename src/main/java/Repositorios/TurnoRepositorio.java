package Repositorios;

import Clases_Java.Turno;
import Interfaz.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class TurnoRepositorio implements Repositorio<Turno> {
    private List<Turno> turnos;

    public TurnoRepositorio() {
        this.turnos = new ArrayList<>();
    }

    @Override
    public void agregar(Turno turno) {
        turnos.add(turno);
    }

    @Override
    public Turno buscarPorId(int id) {
        for (Turno t : turnos) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    @Override
    public void modificar(Turno turno) {
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).getId() == turno.getId()) {
                turnos.set(i, turno);
                break;
            }
        }
    }

    @Override
    public List<Turno> listar() {
        return turnos;
    }

    @Override
    public void eliminar(int id) {
        for (Turno t : turnos) {
            if (t.getId() == id) {
                turnos.remove(t);
                break;
            }
        }
    }
}
