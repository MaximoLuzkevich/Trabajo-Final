package Servicios; // paquete de servicios

// imports de las clases de tu dominio
import Clases_Java.*;
import Enums.EstadoTurno;
import Enums.MetodoPago;
import Enums.TipoUsuario;
import Gestores.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio central del sistema.
 * Acá va la lógica de negocio y el acceso a los gestores.
 */
public class ServicioConsultorio {

    // ---------------- GESTORES ----------------

    private GestorPaciente gestorPaciente;          // gestor de pacientes
    private GestorMedico gestorMedico;              // gestor de médicos
    private GestorTurno gestorTurno;                // gestor de turnos
    private GestorConsulta gestorConsulta;          // gestor de consultas
    private GestorFacturacion gestorFacturacion;    // gestor de facturación
    private GestorAdministrador gestorAdministrador;// gestor de admins
    private GestorRecepcionista gestorRecepcionista;// gestor de recepcionistas

    // ---------------- COLECCIONES REQUERIDAS POR CONSIGNA ----------------

    private Set<String> especialidades;             // set para no repetir especialidades
    private Map<Integer, List<Consulta>> historialPorPaciente; // map: idPaciente -> consultas

    // --------------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------------
    public ServicioConsultorio() {
        // instancio todos los gestores
        gestorPaciente = new GestorPaciente();
        gestorMedico = new GestorMedico();
        gestorTurno = new GestorTurno(gestorPaciente, gestorMedico);
        gestorConsulta = new GestorConsulta(gestorTurno);
        gestorFacturacion = new GestorFacturacion(gestorConsulta);
        gestorAdministrador = new GestorAdministrador();
        gestorRecepcionista = new GestorRecepcionista();

        // instancio las colecciones
        especialidades = new HashSet<>();
        historialPorPaciente = new HashMap<>();

        // cargo datos auxiliares
        cargarEspecialidades();
        cargarHistorialPorPaciente();
    }

    // --------------------------------------------------------------
    // MÉTODOS AUXILIARES DE INICIALIZACIÓN
    // --------------------------------------------------------------
    private void cargarEspecialidades() { // carga las especialidades únicas
        for (Medico m : gestorMedico.listar()) { // recorro médicos
            if (m.isActivo()) { // solo activos
                especialidades.add(m.getEspecialidad()); // agrego al set
            }
        }
    }

    private void cargarHistorialPorPaciente() { // arma el map paciente -> consultas
        for (Consulta c : gestorConsulta.listar()) { // recorro consultas
            if (c.isActivo()) { // solo activas
                int idPac = c.getTurno().getPaciente().getId(); // saco el id de paciente
                historialPorPaciente.putIfAbsent(idPac, new ArrayList<>()); // si no existe lo creo
                historialPorPaciente.get(idPac).add(c); // agrego la consulta
            }
        }
    }

    // --------------------------------------------------------------
    // LOGIN
    // --------------------------------------------------------------

    // busca en todos los tipos de usuario
    public Persona login(String email, String contrasena) {
        // admin
        for (Administrador a : gestorAdministrador.listar()) {
            if (a.isActivo() && a.getEmail().equalsIgnoreCase(email) && a.getContrasena().equals(contrasena)) {
                return a;
            }
        }
        // recepcionista
        for (Recepcionista r : gestorRecepcionista.listar()) {
            if (r.isActivo() && r.getEmail().equalsIgnoreCase(email) && r.getContrasena().equals(contrasena)) {
                return r;
            }
        }
        // médico
        for (Medico m : gestorMedico.listar()) {
            if (m.isActivo() && m.getEmail().equalsIgnoreCase(email) && m.getContrasena().equals(contrasena)) {
                return m;
            }
        }
        // paciente
        for (Paciente p : gestorPaciente.listar()) {
            if (p.isActivo() && p.getEmail().equalsIgnoreCase(email) && p.getContrasena().equals(contrasena)) {
                return p;
            }
        }
        return null; // si no encontró nada
    }

    // --------------------------------------------------------------
    // REGISTRAR PACIENTE (para la opción "registrarse")
    // --------------------------------------------------------------
    public Paciente registrarPaciente(String nombre,
                                      String apellido,
                                      int dni,
                                      int telefono,
                                      String email,
                                      String contrasena,
                                      String obraSocial) {

        // genero un id básico según la cantidad actual
        int nuevoId = gestorPaciente.listar().size() + 1;

        // creo el paciente
        Paciente nuevo = new Paciente(nuevoId, nombre, apellido, dni, telefono, email, contrasena, obraSocial);

        // lo guardo
        gestorPaciente.agregar(nuevo);

        return nuevo; // lo devuelvo por si quiero loguearlo directo
    }

    // --------------------------------------------------------------
    // MÉTODOS PARA PACIENTE
    // --------------------------------------------------------------

    // listar médicos para que el paciente elija uno
    public List<Medico> obtenerMedicosActivos() {
        List<Medico> activos = new ArrayList<>();
        for (Medico m : gestorMedico.listar()) {
            if (m.isActivo()) {
                activos.add(m);
            }
        }
        return activos;
    }

    // pedir turno
    public Turno pedirTurno(Paciente paciente, Medico medico, LocalDateTime fechaHora) {
        // genero un id de turno simple
        int nuevoId = gestorTurno.listar().size() + 1;
        // creo un turno pendiente
        Turno turno = new Turno(nuevoId, paciente, medico, fechaHora, EstadoTurno.PENDIENTE);
        // lo agrego con el gestor (este guarda en json)
        gestorTurno.agregar(turno);
        // también actualizo agenda del médico
        medico.agregarTurnoALaAgenda(turno);
        // devuelvo
        return turno;
    }

    // cancelar turno del paciente
    public boolean cancelarTurnoPaciente(Paciente paciente, int idTurno) {
        Turno t = gestorTurno.buscarPorId(idTurno);
        if (t != null && t.isActivo() && t.getPaciente().getId() == paciente.getId()) {
            t.setEstado(EstadoTurno.CANCELADO);
            gestorTurno.modificar(t);
            return true;
        }
        return false;
    }

    // consultar turnos del paciente
    public List<Turno> obtenerTurnosDePaciente(Paciente paciente) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : gestorTurno.listar()) {
            if (t.isActivo() && t.getPaciente().getId() == paciente.getId()) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    // ver historial médico (consultas) del paciente
    public List<Consulta> obtenerHistorialPaciente(Paciente paciente) {
        List<Consulta> resultado = new ArrayList<>();
        for (Consulta c : gestorConsulta.listar()) {
            if (c.isActivo() && c.getTurno().getPaciente().getId() == paciente.getId()) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    // obtener facturas de un paciente (a partir de las consultas de ese paciente)
    public List<Facturacion> obtenerFacturasDePaciente(Paciente paciente) {
        List<Facturacion> resultado = new ArrayList<>();
        for (Facturacion f : gestorFacturacion.listar()) {
            if (f.isActivo()) {
                int idPacDeLaFactura = f.getConsulta().getTurno().getPaciente().getId();
                if (idPacDeLaFactura == paciente.getId()) {
                    resultado.add(f);
                }
            }
        }
        return resultado;
    }

    // pagar una factura (simulado)
    public boolean pagarFactura(int idFactura) {
        Facturacion f = gestorFacturacion.buscarPorId(idFactura);
        if (f != null && f.isActivo() && !f.isPagado()) {
            f.setPagado(true);
            gestorFacturacion.modificar(f);
            return true;
        }
        return false;
    }

    // --------------------------------------------------------------
    // MÉTODOS PARA MÉDICO
    // --------------------------------------------------------------

    // ver agenda del médico
    public List<Turno> obtenerTurnosDeMedico(Medico medico) {
        List<Turno> resultado = new ArrayList<>();
        for (Turno t : gestorTurno.listar()) {
            if (t.isActivo() && t.getMedico().getId() == medico.getId()) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    // médico cancela turno
    public boolean medicoCancelaTurno(Medico medico, int idTurno) {
        Turno t = gestorTurno.buscarPorId(idTurno);
        if (t != null && t.isActivo() && t.getMedico().getId() == medico.getId()) {
            t.setEstado(EstadoTurno.CANCELADO);
            gestorTurno.modificar(t);
            return true;
        }
        return false;
    }

    // consultar estado de un turno (médico)
    public Turno consultarTurno(int idTurno) {
        return gestorTurno.buscarPorId(idTurno);
    }

    // --------------------------------------------------------------
    // MÉTODOS PARA RECEPCIONISTA
    // --------------------------------------------------------------

    // recepcionista confirma turno
    public boolean confirmarTurno(int idTurno) {
        Turno t = gestorTurno.buscarPorId(idTurno);
        if (t != null && t.isActivo()) {
            t.setEstado(EstadoTurno.CONFIRMADO);
            gestorTurno.modificar(t);
            return true;
        }
        return false;
    }

    // recepcionista agenda un turno (similar a pedirTurno pero sin paciente logueado)
    public Turno agendarTurnoRecepcionista(int idPaciente, int idMedico, LocalDateTime fechaHora) {
        Paciente p = gestorPaciente.buscarPorId(idPaciente);
        Medico m = gestorMedico.buscarPorId(idMedico);
        return pedirTurno(p, m, fechaHora);
    }

    // recepcionista ve la agenda de todos los médicos
    public Map<Medico, List<Turno>> obtenerAgendaCompleta() {
        Map<Medico, List<Turno>> mapa = new HashMap<>();
        for (Medico m : gestorMedico.listar()) {
            if (m.isActivo()) {
                List<Turno> turnosMed = obtenerTurnosDeMedico(m);
                mapa.put(m, turnosMed);
            }
        }
        return mapa;
    }

    // --------------------------------------------------------------
    // MÉTODOS PARA ADMINISTRADOR
    // --------------------------------------------------------------

    // crear médico
    public Medico crearMedico(String nombre, String apellido, int dni, int telefono,
                              String email, String contrasena, String especialidad, String matricula) {
        int nuevoId = gestorMedico.listar().size() + 1;
        Medico m = new Medico(nuevoId, nombre, apellido, dni, telefono, email, contrasena, especialidad, matricula);
        gestorMedico.agregar(m);
        especialidades.add(especialidad); // lo agrego al set
        return m;
    }

    // eliminar médico (baja lógica)
    public boolean eliminarMedico(int id) {
        try {
            Medico m = gestorMedico.buscarPorId(id);
            m.setActivo(false);
            gestorMedico.modificar(m);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // crear recepcionista
    public Recepcionista crearRecepcionista(String nombre, String apellido, int dni, int telefono,
                                            String email, String contrasena, int legajo) {
        int nuevoId = gestorRecepcionista.listar().size() + 1;
        Recepcionista r = new Recepcionista(nuevoId, nombre, apellido, dni, telefono, email, contrasena, legajo);
        gestorRecepcionista.agregar(r);
        return r;
    }

    // eliminar recepcionista
    public boolean eliminarRecepcionista(int id) {
        try {
            Recepcionista r = gestorRecepcionista.buscarPorId(id);
            r.setActivo(false);
            gestorRecepcionista.modificar(r);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --------------------------------------------------------------
    // GETTERS (para usar desde el main si hace falta)
    // --------------------------------------------------------------

    public GestorPaciente getGestorPaciente() { return gestorPaciente; }
    public GestorMedico getGestorMedico() { return gestorMedico; }
    public GestorTurno getGestorTurno() { return gestorTurno; }
    public GestorConsulta getGestorConsulta() { return gestorConsulta; }
    public GestorFacturacion getGestorFacturacion() { return gestorFacturacion; }
    public GestorAdministrador getGestorAdministrador() { return gestorAdministrador; }
    public GestorRecepcionista getGestorRecepcionista() { return gestorRecepcionista; }
}

