package Gestores;

import Clases_Java.Consulta;
import Clases_Java.OperacionesLectoEscritura;
import Clases_Java.Turno;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GestorConsulta implements Gestor<Consulta> {

    private List<Consulta> consultas;
    private static final String ARCHIVO_JSON = "consultas.json";
    private GestorTurno gestorTurno;

    public GestorConsulta(GestorTurno gestorTurno) {
        this.consultas = new ArrayList<>();
        this.gestorTurno = gestorTurno;
        cargarDesdeArchivo();
    }

    public void agregar(Consulta consulta) {
        consultas.add(consulta);
        guardarEnArchivo();
    }

    public Consulta buscarPorId(int id) {
        for (Consulta c : consultas) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public void modificar(Consulta consultaModificada) {
        for (int i = 0; i < consultas.size(); i++) {
            if (consultas.get(i).getId() == consultaModificada.getId()) {
                consultas.set(i, consultaModificada);
                guardarEnArchivo();
                return;
            }
        }
    }

    public void eliminar(int id) {
        Consulta c = buscarPorId(id);
        if (c != null) {
            consultas.remove(c);
            guardarEnArchivo();
        }
    }

    public List<Consulta> listar() {
        return consultas;
    }

    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();
        for (Consulta c : consultas) {
            JSONObject obj = new JSONObject();
            obj.put("id", c.getId());
            obj.put("idTurno", c.getTurno().getId());
            obj.put("diagnostico", c.getDiagnostico());
            obj.put("observaciones", c.getObservaciones());
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
            int id = obj.getInt("id");
            int idTurno = obj.getInt("idTurno");
            Turno turno = gestorTurno.buscarPorId(idTurno);
            if (turno != null) {
                Consulta c = new Consulta(
                        id,
                        turno,
                        obj.optString("diagnostico", ""),
                        obj.optString("observaciones", "")
                );
                consultas.add(c);
            }
        }
    }
}

