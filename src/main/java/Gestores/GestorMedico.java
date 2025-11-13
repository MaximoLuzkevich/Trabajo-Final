package Gestores;

import Clases_Java.Medico;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorMedico implements Gestor<Medico> {

    private static final String RUTA = System.getProperty("user.dir")
            + File.separator + "json" + File.separator;

    private static final String ARCHIVO_JSON = RUTA + "medicos.json";

    private List<Medico> medicos;

    public GestorMedico() {
        medicos = new ArrayList<>();
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
    public void agregar(Medico medico) {
        medicos.add(medico);
        guardarEnArchivo();
    }

    @Override
    public Medico buscarPorId(int id) {
        for (Medico m : medicos) {
            if (m.getId() == id && m.isActivo()) {
                return m;
            }
        }
        throw new UsuarioNoEncontradoException("Médico no encontrado con ID " + id);
    }

    @Override
    public void modificar(Medico medicoModificado) {
        for (int i = 0; i < medicos.size(); i++) {
            if (medicos.get(i).getId() == medicoModificado.getId()) {
                medicos.set(i, medicoModificado);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("Médico no encontrado para modificar");
    }

    @Override
    public void eliminar(int id) {
        Medico m = buscarPorId(id);
        m.setActivo(false); // baja lógica
        guardarEnArchivo();
    }

    @Override
    public List<Medico> listar() {
        List<Medico> activos = new ArrayList<>();
        for (Medico m : medicos) {
            if (m.isActivo()) activos.add(m);
        }
        return activos;
    }

    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();

        for (Medico m : medicos) {
            JSONObject obj = new JSONObject();
            obj.put("id", m.getId());
            obj.put("nombre", m.getNombre());
            obj.put("apellido", m.getApellido());
            obj.put("dni", m.getDni());
            obj.put("telefono", m.getTelefono());
            obj.put("email", m.getEmail());
            obj.put("contrasena", m.getContrasena());
            obj.put("especialidad", m.getEspecialidad());
            obj.put("matricula", m.getMatricula());
            obj.put("activo", m.isActivo());
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

            medicos.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Medico m = new Medico(
                        obj.getInt("id"),
                        obj.getString("nombre"),
                        obj.getString("apellido"),
                        obj.getInt("dni"),
                        obj.getInt("telefono"),
                        obj.getString("email"),
                        obj.getString("contrasena"),
                        obj.getString("especialidad"),
                        obj.getString("matricula")
                );

                m.setActivo(obj.getBoolean("activo"));

                medicos.add(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
