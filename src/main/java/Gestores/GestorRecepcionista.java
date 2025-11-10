package Gestores;

import Clases_Java.OperacionesLectoEscritura;
import Clases_Java.Recepcionista;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GestorRecepcionista implements Gestor<Recepcionista> {

    private List<Recepcionista> recepcionistas;
    private static final String ARCHIVO_JSON = "recepcionistas.json";

    public GestorRecepcionista() {
        this.recepcionistas = new ArrayList<>();
        cargarDesdeArchivo();
    }

    public void agregar(Recepcionista recepcionista) {
        recepcionistas.add(recepcionista);
        guardarEnArchivo();
    }

    public Recepcionista buscarPorId(int id) {
        for (Recepcionista r : recepcionistas) {
            if (r.getId() == id) return r;
        }
        throw new UsuarioNoEncontradoException("Recepcionista no encontrado con ID " + id);
    }

    public void modificar(Recepcionista recepcionistaModificado) {
        for (int i = 0; i < recepcionistas.size(); i++) {
            if (recepcionistas.get(i).getId() == recepcionistaModificado.getId()) {
                recepcionistas.set(i, recepcionistaModificado);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("Recepcionista no encontrado para modificar");
    }

    public void eliminar(int id) {
        Recepcionista r = buscarPorId(id);
        recepcionistas.remove(r);
        guardarEnArchivo();
    }

    public List<Recepcionista> listar() {
        return recepcionistas;
    }

    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();
        for (Recepcionista r : recepcionistas) {
            JSONObject obj = new JSONObject();
            obj.put("id", r.getId());
            obj.put("nombre", r.getNombre());
            obj.put("apellido", r.getApellido());
            obj.put("dni", r.getDni());
            obj.put("telefono", r.getTelefono());
            obj.put("email", r.getEmail());
            obj.put("contrasena", r.getContrasena());
            obj.put("legajo", r.getLegajo());
            array.put(obj);
        }
        OperacionesLectoEscritura.grabar(ARCHIVO_JSON, array);
    }

    private void cargarDesdeArchivo() {
        File f = new File(ARCHIVO_JSON);
        if (!f.exists()) return;
        JSONTokener tokener = OperacionesLectoEscritura.leer(ARCHIVO_JSON);
        if (tokener == null) return;

        JSONArray array = new JSONArray(tokener);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Recepcionista r = new Recepcionista(
                    obj.getInt("id"),
                    obj.getString("nombre"),
                    obj.getString("apellido"),
                    obj.getInt("dni"),
                    obj.getInt("telefono"),
                    obj.getString("email"),
                    obj.getString("contrasena"),
                    obj.getInt("legajo")
            );
            recepcionistas.add(r);
        }
    }
}
