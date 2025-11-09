package Repositorios;

import Clases_Java.Recepcionista;
import Interfaz.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class RecepcionistaRepositorio implements Repositorio<Recepcionista> {
    private List<Recepcionista> recepcionistas;

    public RecepcionistaRepositorio() {
        this.recepcionistas = new ArrayList<>();
    }

    @Override
    public void agregar(Recepcionista recepcionista) {
        recepcionistas.add(recepcionista);
    }

    @Override
    public Recepcionista buscarPorId(int id) {
        for (Recepcionista r : recepcionistas) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    @Override
    public void modificar(Recepcionista recepcionista) {
        for (int i = 0; i < recepcionistas.size(); i++) {
            if (recepcionistas.get(i).getId() == recepcionista.getId()) {
                recepcionistas.set(i, recepcionista);
                break;
            }
        }
    }

    @Override
    public List<Recepcionista> listar() {
        return recepcionistas;
    }

    @Override
    public void eliminar(int id) {
        for (Recepcionista r : recepcionistas) {
            if (r.getId() == id) {
                recepcionistas.remove(r);
                break;
            }
        }
    }
}