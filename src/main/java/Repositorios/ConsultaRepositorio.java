package Repositorios;
import Clases_Java.Consulta;
import Interfaz.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class ConsultaRepositorio implements Repositorio<Consulta> {
    private List<Consulta> consultas;

    public ConsultaRepositorio() {
        this.consultas = new ArrayList<>();
    }

    @Override
    public void agregar(Consulta consulta) {
        consultas.add(consulta);
    }

    @Override
    public Consulta buscarPorId(int id) {
        for (Consulta c : consultas) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    @Override
    public void modificar(Consulta consulta) {
        for (int i = 0; i < consultas.size(); i++) {
            if (consultas.get(i).getId() == consulta.getId()) {
                consultas.set(i, consulta);
                break;
            }
        }
    }

    @Override
    public List<Consulta> listar() {
        return consultas;
    }

    @Override
    public void eliminar(int id) {
        for (Consulta c : consultas) {
            if (c.getId() == id) {
                consultas.remove(c);
                break;
            }
        }
    }
}