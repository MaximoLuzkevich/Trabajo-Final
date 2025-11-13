package Gestores;

import Clases_Java.Medico;
import Clases_Java.Paciente;
import Clases_Java.Turno;
import Enums.EstadoTurno;
import Excepciones.TurnoNoDisponibleException;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorTurno implements Gestor<Turno> {

    private static final String RUTA = System.getProperty("user.dir")
            + File.separator + "json" + File.separator;

    private static final String ARCHIVO_JSON = RUTA + "turnos.json";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private List<Turno> turnos;
    private GestorPaciente gestorPaciente;
    private GestorMedico gestorMedico;

    public GestorTurno(GestorPaciente gestorPaciente, GestorMedico gestorMedico) {
        this.turnos = new ArrayList<>();
        this.gestorPaciente = gestorPaciente;
        this.gestorMedico = gestorMedico;
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
    public void agregar(Turno turno) {
        for (Turno t : turnos) {
            if (t.isActivo() &&
                    t.getMedico().getId() == turno.getMedico().getId() &&
                    t.getFechaHora().equals(turno.getFechaHora())) {

                throw new TurnoNoDisponibleException("❌ Turno no disponible para ese médico en esa fecha/hora.");
            }
        }

        turnos.add(turno);
        guardarEnArchivo();
    }

    @Override
    public Turno buscarPorId(int id) {
        for (Turno t : turnos) {
            if (t.getId() == id && t.isActivo()) return t;
        }
        throw new UsuarioNoEncontradoException("❌ Turno no encontrado con ID " + id);
    }

    @Override
    public void modificar(Turno turnoModificado) {
        for (int i = 0; i < turnos.size(); i++) {
            if (turnos.get(i).getId() == turnoModificado.getId()) {
                turnos.set(i, turnoModificado);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("❌ Turno no encontrado para modificar");
    }

    @Override
    public void eliminar(int id) {
        Turno t = buscarPorId(id);
        t.setActivo(false); // baja lógica
        guardarEnArchivo();
    }

    @Override
    public List<Turno> listar() {
        List<Turno> activos = new ArrayList<>();
        for (Turno t : turnos) {
            if (t.isActivo()) activos.add(t);
        }
        return activos;
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
            obj.put("activo", t.isActivo());
            array.put(obj);
        }

        try (FileWriter fw = new FileWriter(ARCHIVO_JSON)) {
            fw.write(array.toString(4)); // indentado hermoso
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDesdeArchivo() {

        try {
            FileReader reader = new FileReader(ARCHIVO_JSON);
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray array = new JSONArray(tokener);

            turnos.clear();

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

                t.setActivo(obj.getBoolean("activo"));
                turnos.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
