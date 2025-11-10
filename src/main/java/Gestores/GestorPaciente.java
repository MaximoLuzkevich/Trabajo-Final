package Gestores;

import Clases_Java.Paciente;
import Clases_Java.OperacionesLectoEscritura;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class GestorPaciente implements Gestor<Paciente> {

    private List<Paciente> pacientes;
    private static final String ARCHIVO_JSON = "pacientes.json";

    public GestorPaciente() {
        this.pacientes = new ArrayList<>();
        cargarDesdeArchivo();
    }

    public void agregar(Paciente paciente) {
        pacientes.add(paciente);
        guardarEnArchivo();
    }

    public Paciente buscarPorId(int id) {
        for (Paciente p : pacientes) {
            if (p.getId() == id) return p;
        }
        throw new UsuarioNoEncontradoException("Paciente no encontrado con ID " + id);
    }

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

    public void eliminar(int id) {
        Paciente p = buscarPorId(id);
        pacientes.remove(p);
        guardarEnArchivo();
    }

    public List<Paciente> listar() {
        return pacientes;
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
            pacientes.add(p);
        }
    }
}