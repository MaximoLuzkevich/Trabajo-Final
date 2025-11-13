package Gestores;

import Clases_Java.Medico;
import Clases_Java.Paciente;
import Clases_Java.Turno;
import Clases_Java.OperacionesLectoEscritura;
import Enums.EstadoTurno;
import Excepciones.TurnoNoDisponibleException;
import Excepciones.UsuarioNoEncontradoException;
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

    private static final String ARCHIVO_JSON = "json/turnos.json";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public GestorTurno(GestorPaciente gestorPaciente, GestorMedico gestorMedico) {
        this.turnos = new ArrayList<>();
        this.gestorPaciente = gestorPaciente;
        this.gestorMedico = gestorMedico;
        cargarDesdeArchivo();
    }

    // ----------------------------------------------------------
    // AGREGAR
    // ----------------------------------------------------------
    @Override
    public void agregar(Turno turno) {

        // validación de disponibilidad
        for (Turno t : turnos) {
            if (t.isActivo()
                    && t.getMedico().getId() == turno.getMedico().getId()
                    && t.getFechaHora().equals(turno.getFechaHora())) {

                throw new TurnoNoDisponibleException(
                        "Turno no disponible para ese médico en ese horario."
                );
            }
        }

        turnos.add(turno);
        guardarEnArchivo();
    }

    // ----------------------------------------------------------
    // BUSCAR POR ID (CON EXCEPCIÓN)
    // ----------------------------------------------------------
    @Override
    public Turno buscarPorId(int id) {
        for (Turno t : turnos) {
            if (t.getId() == id && t.isActivo()) {
                return t;
            }
        }
        throw new UsuarioNoEncontradoException("Turno no encontrado con ID " + id);
    }

    // ----------------------------------------------------------
    // MODIFICAR
    // ----------------------------------------------------------
    @Override
    public void modificar(Turno turnoModificado) {
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).getId() == turnoModificado.getId()) {
                turnos.set(i, turnoModificado);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("Turno no encontrado para modificar.");
    }

    // ----------------------------------------------------------
    // ELIMINAR (BAJA LÓGICA)
    // ----------------------------------------------------------
    @Override
    public void eliminar(int id) {
        Turno t = buscarPorId(id); // lanza excepción si no existe
        t.setActivo(false);
        guardarEnArchivo();
    }

    // ----------------------------------------------------------
    // LISTAR SOLO ACTIVOS
    // ----------------------------------------------------------
    @Override
    public List<Turno> listar() {
        List<Turno> activos = new ArrayList<>();
        for (Turno t : turnos) {
            if (t.isActivo()) {
                activos.add(t);
            }
        }
        return activos;
    }

    // ----------------------------------------------------------
    // GUARDAR JSON
    // ----------------------------------------------------------
    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();

        for (Turno t : turnos) {
            JSONObject obj = new JSONObject();
            obj.put("id", t.getId());
            obj.put("idPaciente", t.getPaciente().getId());
            obj.put("idMedico", t.getMedico().getId());
            obj.put("fechaHora", t.getFechaHora().format(FORMATTER));
            obj.put("estado", t.getEstado().name());
            obj.put("activo", t.isActivo());
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

            Paciente p = gestorPaciente.buscarPorId(obj.getInt("idPaciente"));
            Medico m = gestorMedico.buscarPorId(obj.getInt("idMedico"));

            Turno t = new Turno(
                    obj.getInt("id"),
                    p,
                    m,
                    LocalDateTime.parse(obj.getString("fechaHora"), FORMATTER),
                    EstadoTurno.valueOf(obj.getString("estado"))
            );

            t.setActivo(obj.optBoolean("activo", true));
            turnos.add(t);
        }
    }
}