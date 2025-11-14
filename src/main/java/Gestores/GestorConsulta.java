package Gestores;

import Clases_Java.Consulta;
import Clases_Java.OperacionesLectoEscritura;
import Clases_Java.Turno;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GestorConsulta implements Gestor<Consulta> {

    private List<Consulta> consultas;
    private GestorTurno gestorTurno;

    private static final String ARCHIVO_JSON = "json/consultas.json";

    public GestorConsulta(GestorTurno gestorTurno) {
        this.consultas = new ArrayList<>();
        this.gestorTurno = gestorTurno;
        cargarDesdeArchivo();
    }

    // ------------------------------------------------------------
    // AGREGAR
    // ------------------------------------------------------------
    @Override
    public void agregar(Consulta consulta) {
        consultas.add(consulta);
        guardarEnArchivo();
    }

    // ------------------------------------------------------------
    // BUSCAR POR ID
    // ------------------------------------------------------------
    @Override
    public Consulta buscarPorId(int id) {
        for (Consulta c : consultas) {
            if (c.getId() == id && c.isActivo()) {
                return c;
            }
        }
        throw new UsuarioNoEncontradoException("Consulta no encontrada con ID " + id);
    }

    // ------------------------------------------------------------
    // MODIFICAR
    // ------------------------------------------------------------
    @Override
    public void modificar(Consulta consultaModificada) {
        for (int i = 0; i < consultas.size(); i++) {
            if (consultas.get(i).getId() == consultaModificada.getId()) {
                consultas.set(i, consultaModificada);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("Consulta no encontrada para modificar");
    }

    // ------------------------------------------------------------
    // BAJA LÓGICA
    // ------------------------------------------------------------
    @Override
    public void eliminar(int id) {
        Consulta c = buscarPorId(id);
        c.setActivo(false);
        guardarEnArchivo();
    }

    // ------------------------------------------------------------
    // LISTAR (solo activas)
    // ------------------------------------------------------------
    @Override
    public List<Consulta> listar() {
        List<Consulta> activas = new ArrayList<>();
        for (Consulta c : consultas) {
            if (c.isActivo()) activas.add(c);
        }
        return activas;
    }

    // ------------------------------------------------------------
    // GUARDAR JSON
    // ------------------------------------------------------------
    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();

        for (Consulta c : consultas) {
            JSONObject obj = new JSONObject();
            obj.put("id", c.getId());
            obj.put("idTurno", c.getTurno().getId());
            obj.put("diagnostico", c.getDiagnostico());
            obj.put("observaciones", c.getObservaciones());
            obj.put("activo", c.isActivo());
            array.put(obj);
        }

        OperacionesLectoEscritura.grabar(ARCHIVO_JSON, array);
    }

    // ------------------------------------------------------------
    // CARGAR JSON
    // ------------------------------------------------------------
    private void cargarDesdeArchivo() {
        File file = new File(ARCHIVO_JSON);
        if (!file.exists()) return;

        JSONTokener tokener = OperacionesLectoEscritura.leer(ARCHIVO_JSON);
        if (tokener == null) return;

        JSONArray array = new JSONArray(tokener);

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            int idTurno = obj.getInt("idTurno");
            Turno turno = gestorTurno.buscarPorId(idTurno);

            if (turno == null) {
                throw new UsuarioNoEncontradoException(
                        "El JSON contiene una consulta cuyo turno (ID " + idTurno + ") no existe."
                );
            }

            Consulta c = new Consulta(
                    obj.getInt("id"),
                    turno,
                    obj.optString("diagnostico", ""),
                    obj.optString("observaciones", "")
            );

            c.setActivo(obj.optBoolean("activo", true));

            consultas.add(c);
        }
    }
}
