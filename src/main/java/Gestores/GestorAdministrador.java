package Gestores;

import Clases_Java.Administrador;
import Clases_Java.OperacionesLectoEscritura;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GestorAdministrador implements Gestor<Administrador> {

    private List<Administrador> administradores;
    private static final String ARCHIVO_JSON = "json/administradores.json";

    public GestorAdministrador() {
        this.administradores = new ArrayList<>();
        cargarDesdeArchivo();
    }

    // ----------------------------------------------------------
    // AGREGAR
    // ----------------------------------------------------------
    @Override
    public void agregar(Administrador administrador) {
        administradores.add(administrador);
        guardarEnArchivo();
    }

    // ----------------------------------------------------------
    // BUSCAR POR ID (con excepción)
    // ----------------------------------------------------------
    @Override
    public Administrador buscarPorId(int id) {
        for (Administrador a : administradores) {
            if (a.getId() == id && a.isActivo()) {
                return a;
            }
        }
        throw new UsuarioNoEncontradoException("Administrador no encontrado con ID " + id);
    }

    // ----------------------------------------------------------
    // MODIFICAR
    // ----------------------------------------------------------
    @Override
    public void modificar(Administrador adminModificado) {
        for (int i = 0; i < administradores.size(); i++) {
            if (administradores.get(i).getId() == adminModificado.getId()) {
                administradores.set(i, adminModificado);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("Administrador no encontrado para modificar");
    }

    // ----------------------------------------------------------
    // BAJA LÓGICA
    // ----------------------------------------------------------
    @Override
    public void eliminar(int id) {
        Administrador a = buscarPorId(id); // lanza excepción si no existe
        a.setActivo(false);
        guardarEnArchivo();
    }

    // ----------------------------------------------------------
    // LISTAR SOLO ACTIVOS
    // ----------------------------------------------------------
    @Override
    public List<Administrador> listar() {
        List<Administrador> activos = new ArrayList<>();
        for (Administrador a : administradores) {
            if (a.isActivo()) {
                activos.add(a);
            }
        }
        return activos;
    }

    // ----------------------------------------------------------
    // GUARDAR JSON
    // ----------------------------------------------------------
    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();

        for (Administrador a : administradores) {
            JSONObject obj = new JSONObject();
            obj.put("id", a.getId());
            obj.put("nombre", a.getNombre());
            obj.put("apellido", a.getApellido());
            obj.put("dni", a.getDni());
            obj.put("telefono", a.getTelefono());
            obj.put("email", a.getEmail());
            obj.put("contrasena", a.getContrasena());
            obj.put("legajo", a.getLegajo());
            obj.put("activo", a.isActivo());
            array.put(obj);
        }

        OperacionesLectoEscritura.grabar(ARCHIVO_JSON, array);
    }

    // ----------------------------------------------------------
    // CARGAR JSON
    // ----------------------------------------------------------
    private void cargarDesdeArchivo() {
        File file = new File(ARCHIVO_JSON);
        if (!file.exists()) return;

        JSONTokener tokener = OperacionesLectoEscritura.leer(ARCHIVO_JSON);
        if (tokener == null) return;

        JSONArray array = new JSONArray(tokener);

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            Administrador a = new Administrador(
                    obj.getInt("id"),
                    obj.getString("nombre"),
                    obj.getString("apellido"),
                    obj.getInt("dni"),
                    obj.getInt("telefono"),
                    obj.getString("email"),
                    obj.getString("contrasena"),
                    obj.getInt("legajo")
            );

            a.setActivo(obj.optBoolean("activo", true));

            administradores.add(a);
        }
    }
}
