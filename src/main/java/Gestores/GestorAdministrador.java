package Gestores;

import Clases_Java.Administrador;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorAdministrador implements Gestor<Administrador> {

    private static final String RUTA = System.getProperty("user.dir")
            + File.separator + "json" + File.separator;

    private static final String ARCHIVO_JSON = RUTA + "administradores.json";

    private List<Administrador> administradores;

    public GestorAdministrador() {
        administradores = new ArrayList<>();
        inicializarArchivos();
        cargarDesdeArchivo();
    }

    private void inicializarArchivos() {

        File carpeta = new File(RUTA);
        if (!carpeta.exists()) carpeta.mkdirs();

        File archivo = new File(ARCHIVO_JSON);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
                FileWriter fw = new FileWriter(archivo);
                fw.write("[]");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void agregar(Administrador administrador) {
        administradores.add(administrador);
        guardarEnArchivo();
    }

    @Override
    public Administrador buscarPorId(int id) {
        for (Administrador a : administradores) {
            if (a.getId() == id && a.isActivo()) {
                return a;
            }
        }
        throw new UsuarioNoEncontradoException("Administrador no encontrado con ID " + id);
    }

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

    @Override
    public void eliminar(int id) {
        Administrador a = buscarPorId(id);
        a.setActivo(false); // baja lógica
        guardarEnArchivo();
    }

    @Override
    public List<Administrador> listar() {
        List<Administrador> activos = new ArrayList<>();
        for (Administrador a : administradores) {
            if (a.isActivo()) activos.add(a);
        }
        return activos;
    }

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

        try (FileWriter fw = new FileWriter(ARCHIVO_JSON)) {
            fw.write(array.toString(4)); // indentado prolijo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDesdeArchivo() {
        try {
            FileReader reader = new FileReader(ARCHIVO_JSON);
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray array = new JSONArray(tokener);

            administradores.clear();

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

                a.setActivo(obj.getBoolean("activo"));

                administradores.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

