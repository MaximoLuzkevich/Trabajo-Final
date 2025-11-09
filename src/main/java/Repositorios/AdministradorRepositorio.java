package Repositorios;
import Clases_Java.Administrador;
import Interfaz.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class AdministradorRepositorio implements Repositorio<Administrador> {
    private List<Administrador> administradores;

    public AdministradorRepositorio() {
        this.administradores = new ArrayList<>();
    }

    @Override
    public void agregar(Administrador administrador) {
        administradores.add(administrador);
    }

    @Override
    public Administrador buscarPorId(int id) {
        for (Administrador a : administradores) {
            if (a.getId() == id) return a;
        }
        return null;
    }

    @Override
    public void modificar(Administrador administrador) {
        for (int i = 0; i < administradores.size(); i++) {
            if (administradores.get(i).getId() == administrador.getId()) {
                administradores.set(i, administrador);
                break;
            }
        }
    }

    @Override
    public List<Administrador> listar() {
        return administradores;
    }

    @Override
    public void eliminar(int id) {
        for (Administrador a : administradores) {
            if (a.getId() == id) {
                administradores.remove(a);
                break;
            }
        }
    }
}
