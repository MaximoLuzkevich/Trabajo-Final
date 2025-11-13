package Gestores;

import Clases_Java.Consulta;
import Clases_Java.Facturacion;
import Enums.MetodoPago;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorFacturacion implements Gestor<Facturacion> {

    private static final String RUTA = System.getProperty("user.dir")
            + File.separator + "json" + File.separator;

    private static final String ARCHIVO_JSON = RUTA + "facturaciones.json";

    private List<Facturacion> facturaciones;
    private GestorConsulta gestorConsulta;

    public GestorFacturacion(GestorConsulta gestorConsulta) {
        this.facturaciones = new ArrayList<>();
        this.gestorConsulta = gestorConsulta;
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
    public void agregar(Facturacion facturacion) {
        facturaciones.add(facturacion);
        guardarEnArchivo();
    }

    @Override
    public Facturacion buscarPorId(int id) {
        for (Facturacion f : facturaciones) {
            if (f.getId() == id && f.isActivo())
                return f;
        }
        throw new UsuarioNoEncontradoException("❌ Factura no encontrada con ID " + id);
    }

    @Override
    public void modificar(Facturacion facturacionModificada) {
        for (int i = 0; i < facturaciones.size(); i++) {
            if (facturaciones.get(i).getId() == facturacionModificada.getId()) {
                facturaciones.set(i, facturacionModificada);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("❌ Facturación no encontrada para modificar");
    }

    @Override
    public void eliminar(int id) {
        Facturacion f = buscarPorId(id);
        f.setActivo(false); // baja lógica
        guardarEnArchivo();
    }

    @Override
    public List<Facturacion> listar() {
        List<Facturacion> activas = new ArrayList<>();
        for (Facturacion f : facturaciones) {
            if (f.isActivo()) activas.add(f);
        }
        return activas;
    }

    private void guardarEnArchivo() {
        JSONArray array = new JSONArray();

        for (Facturacion f : facturaciones) {
            JSONObject obj = new JSONObject();
            obj.put("id", f.getId());
            obj.put("idConsulta", f.getConsulta().getId());
            obj.put("monto", f.getMonto());
            obj.put("metodoPago", f.getMetodoPago().name());
            obj.put("pagado", f.isPagado());
            obj.put("activo", f.isActivo());
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

            facturaciones.clear();

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                int idConsulta = obj.getInt("idConsulta");

                Consulta consulta = gestorConsulta.buscarPorId(idConsulta);

                Facturacion f = new Facturacion(
                        obj.getInt("id"),
                        consulta,
                        obj.getDouble("monto"),
                        MetodoPago.valueOf(obj.getString("metodoPago")),
                        obj.getBoolean("pagado")
                );

                f.setActivo(obj.optBoolean("activo", true));

                facturaciones.add(f);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}