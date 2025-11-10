package Gestores;

import Clases_Java.Consulta;
import Clases_Java.Facturacion;
import Clases_Java.OperacionesLectoEscritura;
import Enums.MetodoPago;
import Interfaz.Gestor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GestorFacturacion implements Gestor<Facturacion> {

    private List<Facturacion> facturaciones;
    private static final String ARCHIVO_JSON = "facturaciones.json";
    private GestorConsulta gestorConsulta;

    public GestorFacturacion(GestorConsulta gestorConsulta) {
        this.facturaciones = new ArrayList<>();
        this.gestorConsulta = gestorConsulta;
        cargarDesdeArchivo();
    }

    public void agregar(Facturacion facturacion) {
        facturaciones.add(facturacion);
        guardarEnArchivo();
    }

    public Facturacion buscarPorId(int id) {
        for (Facturacion f : facturaciones) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    public void modificar(Facturacion facturacionModificada) {
        for (int i = 0; i < facturaciones.size(); i++) {
            if (facturaciones.get(i).getId() == facturacionModificada.getId()) {
                facturaciones.set(i, facturacionModificada);
                guardarEnArchivo();
                return;
            }
        }
    }

    public void eliminar(int id) {
        Facturacion f = buscarPorId(id);
        if (f != null) {
            facturaciones.remove(f);
            guardarEnArchivo();
        }
    }

    public List<Facturacion> listar() {
        return facturaciones;
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
            int idConsulta = obj.getInt("idConsulta");
            double monto = obj.getDouble("monto");
            MetodoPago metodo = MetodoPago.valueOf(obj.getString("metodoPago"));
            boolean pagado = obj.getBoolean("pagado");

            Consulta consulta = gestorConsulta.buscarPorId(idConsulta);
            if (consulta != null) {
                Facturacion fact = new Facturacion(id, consulta, monto, metodo, pagado);
                facturaciones.add(fact);
            }
        }
    }
}
