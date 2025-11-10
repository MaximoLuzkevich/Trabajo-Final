package Gestores;

import Clases_Java.Medico;
import Clases_Java.Paciente;
import Clases_Java.Turno;
import Clases_Java.OperacionesLectoEscritura;
import Enums.EstadoTurno;
import Excepciones.TurnoNoDisponibleException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorTurno implements Gestor<Turno> {

    private List<Turno> turnos;
    private GestorPaciente gestorPaciente;
    private GestorMedico gestorMedico;
    private static final String ARCHIVO_JSON = "turnos.json";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public GestorTurno(GestorPaciente gestorPaciente, GestorMedico gestorMedico) {
        this.turnos = new ArrayList<>();
        this.gestorPaciente = gestorPaciente;
        this.gestorMedico = gestorMedico;
        cargarDesdeArchivo();
    }

    public void agregar(Turno turno) {
        for (Turno t : turnos) {
            if (t.getMedico().getId() == turno.getMedico().getId() &&
                    t.getFechaHora().equals(turno.getFechaHora())) {
                throw new TurnoNoDisponibleException("Turno no disponible");
            }
        }
        turnos.add(turno);
        guardarEnArchivo();
    }

    public Turno buscarPorId(int id) {
        for (Turno t : turnos) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public void modificar(Turno turnoModificado) {
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).getId() == turnoModificado.getId()) {
                turnos.set(i, turnoModificado);
                guardarEnArchivo();
                return;
            }
        }
    }

    public void eliminar(int id) {
        Turno t = buscarPorId(id);
        if (t != null) {
            turnos.remove(t);
            guardarEnArchivo();
        }
    }

    public List<Turno> listar() {
        return turnos;
    }

    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();
        for (Turno t : turnos) {
            JSONObject obj = new JSONObject();
            obj.put("id", t.getId());
            obj.put("idPaciente", t.getPaciente().getId());
            obj.put("idMedico", t.getMedico().getId());
            obj.put("fechaHora", t.getFechaHora().format(FORMATTER));
            obj.put("estado", t.getEstado().name());
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
            Paciente p = gestorPaciente.buscarPorId(obj.getInt("idPaciente"));
            Medico m = gestorMedico.buscarPorId(obj.getInt("idMedico"));
            Turno t = new Turno(
                    obj.getInt("id"),
                    p,
                    m,
                    LocalDateTime.parse(obj.getString("fechaHora"), FORMATTER),
                    EstadoTurno.valueOf(obj.getString("estado"))
            );
            turnos.add(t);
        }
    }
}

