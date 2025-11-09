package Repositorios;

import Clases_Java.Facturacion;
import Interfaz.Repositorio;

import java.util.ArrayList;
import java.util.List;

public class FacturacionRepositorio implements Repositorio<Facturacion> {
    private List<Facturacion> facturaciones;

    public FacturacionRepositorio() {
        this.facturaciones = new ArrayList<>();
    }

    @Override
    public void agregar(Facturacion facturacion) {
        facturaciones.add(facturacion);
    }

    @Override
    public Facturacion buscarPorId(int id) {
        for (Facturacion f : facturaciones) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    @Override
    public void modificar(Facturacion facturacion) {
        for (int i = 0; i < facturaciones.size(); i++) {
            if (facturaciones.get(i).getId() == facturacion.getId()) {
                facturaciones.set(i, facturacion);
                break;
            }
        }
    }

    @Override
    public List<Facturacion> listar() {
        return facturaciones;
    }

    @Override
    public void eliminar(int id) {
        for (Facturacion f : facturaciones) {
            if (f.getId() == id) {
                facturaciones.remove(f);
                break;
            }
        }
    }
}
