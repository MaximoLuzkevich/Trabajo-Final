package Gestores;

import Clases_Java.Paciente;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorPaciente implements Gestor<Paciente> {

    private static final String RUTA = System.getProperty("user.dir")
            + File.separator + "json" + File.separator;

    private static final String ARCHIVO_JSON = RUTA + "pacientes.json";

    private List<Paciente> pacientes;

    public GestorPaciente() {
        pacientes = new ArrayList<>();
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
    public void agregar(Paciente paciente) {
        pacientes.add(paciente);
        guardarEnArchivo();
    }

    @Override
    public Paciente buscarPorId(int id) {
        for (Paciente p : pacientes) {
            if (p.getId() == id && p.isActivo()) {
                return p;
            }
        }
        throw new UsuarioNoEncontradoException("Paciente no encontrado con ID " + id);
    }

    @Override
    public void modificar(Paciente pacienteModificado) {
        for (int i = 0; i < pacientes.size(); i++) {
            if (pacientes.get(i).getId() == pacienteModificado.getId()) {
                pacientes.set(i, pacienteModificado);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("Paciente no encontrado para modificar");
    }

    @Override
    public void eliminar(int id) {
        Paciente p = buscarPorId(id);
        p.setActivo(false); // BAJA LÓGICA
        guardarEnArchivo();
    }

    @Override
    public List<Paciente> listar() {
        List<Paciente> activos = new ArrayList<>();
        for (Paciente p : pacientes) {
            if (p.isActivo()) activos.add(p);
        }
        return activos;
    }

    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();

        for (Paciente p : pacientes) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("nombre", p.getNombre());
            obj.put("apellido", p.getApellido());
            obj.put("dni", p.getDni());
            obj.put("telefono", p.getTelefono());
            obj.put("email", p.getEmail());
            obj.put("contrasena", p.getContrasena());
            obj.put("obraSocial", p.getObraSocial());
            obj.put("activo", p.isActivo());
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

            pacientes.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Paciente p = new Paciente(
                        obj.getInt("id"),
                        obj.getString("nombre"),
                        obj.getString("apellido"),
                        obj.getInt("dni"),
                        obj.getInt("telefono"),
                        obj.getString("email"),
                        obj.getString("contrasena"),
                        obj.getString("obraSocial")
                );

                p.setActivo(obj.getBoolean("activo"));

                pacientes.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

