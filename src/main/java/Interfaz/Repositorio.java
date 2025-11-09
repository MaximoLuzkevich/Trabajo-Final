package Interfaz;
import java.util.List;


public interface Repositorio<T> {
    void agregar(T elemento);
    T buscarPorId(int id);
    void modificar(T elemento);
    List<T> listar();
    void eliminar(int id);
}
