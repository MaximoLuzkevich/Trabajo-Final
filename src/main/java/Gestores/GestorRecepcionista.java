package Gestores;

import Clases_Java.Recepcionista;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorRecepcionista implements Gestor<Recepcionista> {

    private static final String RUTA = System.getProperty("user.dir")
            + File.separator + "json" + File.separator;

    private static final String ARCHIVO_JSON = RUTA + "recepcionistas.json";

    private List<Recepcionista> recepcionistas;

    public GestorRecepcionista() {
        recepcionistas = new ArrayList<>();
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
    public void agregar(Recepcionista recepcionista) {
        recepcionistas.add(recepcionista);
        guardarEnArchivo();
    }

    @Override
    public Recepcionista buscarPorId(int id) {
        for (Recepcionista r : recepcionistas) {
            if (r.getId() == id && r.isActivo()) {
                return r;
            }
        }
        throw new UsuarioNoEncontradoException("Recepcionista no encontrado con ID " + id);
    }

    @Override
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

    @Override
    public void eliminar(int id) {
        Recepcionista r = buscarPorId(id);
        r.setActivo(false); // baja lógica
        guardarEnArchivo();
    }

    @Override
    public List<Recepcionista> listar() {
        List<Recepcionista> activos = new ArrayList<>();
        for (Recepcionista r : recepcionistas) {
            if (r.isActivo()) activos.add(r);
        }
        return activos;
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
            obj.put("activo", r.isActivo());
            array.put(obj);
        }

        try (FileWriter fw = new FileWriter(ARCHIVO_JSON)) {
            fw.write(array.toString(4)); // indentado profesional
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDesdeArchivo() {
        try {
            FileReader reader = new FileReader(ARCHIVO_JSON);
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray array = new JSONArray(tokener);

            recepcionistas.clear();

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

                r.setActivo(obj.getBoolean("activo"));

                recepcionistas.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}