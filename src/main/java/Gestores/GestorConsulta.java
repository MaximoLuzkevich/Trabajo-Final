package Gestores;

import Clases_Java.Consulta;
import Clases_Java.Turno;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorConsulta implements Gestor<Consulta> {

    private static final String RUTA = System.getProperty("user.dir")
            + File.separator + "json" + File.separator;

    private static final String ARCHIVO_JSON = RUTA + "consultas.json";

    private List<Consulta> consultas;
    private GestorTurno gestorTurno;

    public GestorConsulta(GestorTurno gestorTurno) {
        this.consultas = new ArrayList<>();
        this.gestorTurno = gestorTurno;
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
    public void agregar(Consulta consulta) {
        consultas.add(consulta);
        guardarEnArchivo();
    }

    @Override
    public Consulta buscarPorId(int id) {
        for (Consulta c : consultas) {
            if (c.getId() == id && c.isActivo()) return c;
        }
        throw new UsuarioNoEncontradoException("❌ Consulta no encontrada con ID " + id);
    }

    @Override
    public void modificar(Consulta consultaModificada) {
        for (int i = 0; i < consultas.size(); i++) {
            if (consultas.get(i).getId() == consultaModificada.getId()) {
                consultas.set(i, consultaModificada);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("❌ Consulta no encontrada para modificar");
    }

    @Override
    public void eliminar(int id) {
        Consulta c = buscarPorId(id);
        c.setActivo(false); // baja lógica
        guardarEnArchivo();
    }

    @Override
    public List<Consulta> listar() {
        List<Consulta> activas = new ArrayList<>();
        for (Consulta c : consultas) {
            if (c.isActivo()) activas.add(c);
        }
        return activas;
    }

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

        try (FileWriter fw = new FileWriter(ARCHIVO_JSON)) {
            fw.write(array.toString(4)); // indentación linda
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDesdeArchivo() {
        try {
            FileReader reader = new FileReader(ARCHIVO_JSON);
            JSONTokener tokener = new JSONTokener(reader);
            JSONArray array = new JSONArray(tokener);

            consultas.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int idTurno = obj.getInt("idTurno");
                Turno turno = gestorTurno.buscarPorId(idTurno);

                if (turno == null)
                    continue; // si el turno no existe, se ignora la consulta corrupta

                Consulta c = new Consulta(
                        obj.getInt("id"),
                        turno,
                        obj.optString("diagnostico", ""),
                        obj.optString("observaciones", "")
                );

                c.setActivo(obj.optBoolean("activo"));
                consultas.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
