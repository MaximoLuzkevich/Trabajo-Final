package Interfaz;

import java.util.List;

public interface Gestor<T> {

    void agregar(T elemento);

    T buscarPorId(int id);

    void modificar(T elemento);

    void eliminar(int id);

    List<T> listar();
}
