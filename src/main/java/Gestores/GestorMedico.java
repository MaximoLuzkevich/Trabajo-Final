package Gestores;

import Clases_Java.Medico;
import Clases_Java.OperacionesLectoEscritura;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GestorMedico implements Gestor<Medico> {

    private List<Medico> medicos;
    private static final String ARCHIVO_JSON = "medicos.json";

    public GestorMedico() {
        this.medicos = new ArrayList<>();
        cargarDesdeArchivo();
    }

    public void agregar(Medico medico) {
        medicos.add(medico);
        guardarEnArchivo();
    }

    public Medico buscarPorId(int id) {
        for (Medico m : medicos) {
            if (m.getId() == id) return m;
        }
        throw new UsuarioNoEncontradoException("Médico no encontrado con ID " + id);
    }

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

    public void eliminar(int id) {
        Medico m = buscarPorId(id);
        m.setActivo(false);
        guardarEnArchivo();
    }

    public List<Medico> listar() {
        return medicos;
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
            m.setActivo(obj.optBoolean("activo", true));
            medicos.add(m);
        }
    }
}
