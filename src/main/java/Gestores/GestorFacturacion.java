package Gestores;

import Clases_Java.Consulta;
import Clases_Java.Facturacion;
import Clases_Java.OperacionesLectoEscritura;
import Enums.MetodoPago;
import Excepciones.UsuarioNoEncontradoException;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GestorFacturacion implements Gestor<Facturacion> {

    private List<Facturacion> facturaciones;
    private GestorConsulta gestorConsulta;

    private static final String ARCHIVO_JSON = "json/facturaciones.json";

    public GestorFacturacion(GestorConsulta gestorConsulta) {
        this.facturaciones = new ArrayList<>();
        this.gestorConsulta = gestorConsulta;
        cargarDesdeArchivo();
    }

    // ------------------------------------------------------------
    // AGREGAR
    // ------------------------------------------------------------
    @Override
    public void agregar(Facturacion facturacion) {
        facturaciones.add(facturacion);
        guardarEnArchivo();
    }

    // ------------------------------------------------------------
    // BUSCAR POR ID
    // ------------------------------------------------------------
    @Override
    public Facturacion buscarPorId(int id) {
        for (Facturacion f : facturaciones) {
            if (f.getId() == id && f.isActivo()) {
                return f;
            }
        }
        throw new UsuarioNoEncontradoException("Factura no encontrada con ID " + id);
    }

    // ------------------------------------------------------------
    // MODIFICAR
    // ------------------------------------------------------------
    @Override
    public void modificar(Facturacion facturacionModificada) {
        for (int i = 0; i < facturaciones.size(); i++) {
            if (facturaciones.get(i).getId() == facturacionModificada.getId()) {
                facturaciones.set(i, facturacionModificada);
                guardarEnArchivo();
                return;
            }
        }
        throw new UsuarioNoEncontradoException("Facturación no encontrada para modificar");
    }

    // ------------------------------------------------------------
    // BAJA LÓGICA
    // ------------------------------------------------------------
    @Override
    public void eliminar(int id) {
        Facturacion f = buscarPorId(id);
        f.setActivo(false);
        guardarEnArchivo();
    }

    // ------------------------------------------------------------
    // LISTAR (solo activas)
    // ------------------------------------------------------------
    @Override
    public List<Facturacion> listar() {
        List<Facturacion> activas = new ArrayList<>();
        for (Facturacion f : facturaciones) {
            if (f.isActivo()) activas.add(f);
        }
        return activas;
    }

    // ------------------------------------------------------------
    // GUARDAR JSON
    // ------------------------------------------------------------
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
        facturaciones.clear();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            int idConsulta = obj.getInt("idConsulta");

            Consulta consulta;
            try {
                consulta = gestorConsulta.buscarPorId(idConsulta);
            } catch (UsuarioNoEncontradoException e) {
                throw new UsuarioNoEncontradoException(
                        "El JSON contiene una facturación cuyo turno/consulta (ID " + idConsulta + ") no existe."
                );
            }

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
    }
}
