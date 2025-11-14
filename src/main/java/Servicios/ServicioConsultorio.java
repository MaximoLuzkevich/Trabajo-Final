package Servicios;

import Clases_Java.*;
import Enums.EstadoTurno;
import Gestores.*;

import java.time.LocalDateTime;
import java.util.*;

public class ServicioConsultorio {

    // ================================
    //          GESTORES
    // ================================
    private GestorPaciente gestorPaciente;
    private GestorMedico gestorMedico;
    private GestorTurno gestorTurno;
    private GestorConsulta gestorConsulta;
    private GestorFacturacion gestorFacturacion;
    private GestorAdministrador gestorAdministrador;
    private GestorRecepcionista gestorRecepcionista;

    // ================================
    //   COLECCIONES PEDIDAS (SET – MAP – LIST)
    // ================================
    private Set<String> especialidades;                           // SET
    private Map<Integer, List<Consulta>> historialPorPaciente;    // MAP

    // ================================
    //          CONSTRUCTOR
    // ================================
    public ServicioConsultorio() {

        // instanciar gestores
        gestorPaciente = new GestorPaciente();
        gestorMedico = new GestorMedico();
        gestorTurno = new GestorTurno(gestorPaciente, gestorMedico);
        gestorConsulta = new GestorConsulta(gestorTurno);
        gestorFacturacion = new GestorFacturacion(gestorConsulta);
        gestorAdministrador = new GestorAdministrador();
        gestorRecepcionista = new GestorRecepcionista();

        // inicializar colecciones
        especialidades = new HashSet<>();
        historialPorPaciente = new HashMap<>();

        // cargar datos del sistema
        cargarEspecialidades();
        cargarHistorialPorPaciente();
    }

    // ================================
    //   CARGA INICIAL – SET
    // ================================
    private void cargarEspecialidades() {
        for (Medico m : gestorMedico.listar()) {
            if (m.isActivo()) {
                especialidades.add(m.getEspecialidad());
            }
        }
    }

    // ================================
    //   CARGA INICIAL – MAP
    // ================================
    private void cargarHistorialPorPaciente() {
        historialPorPaciente.clear();

        for (Consulta c : gestorConsulta.listar()) {
            if (c.isActivo()) {
                int idPac = c.getTurno().getPaciente().getId();

                historialPorPaciente
                        .computeIfAbsent(idPac, k -> new ArrayList<>())
                        .add(c);
            }
        }
    }

    // ================================
    //             LOGIN
    // ================================
    public Persona login(String email, String contrasena) {

        // admin
        for (Administrador a : gestorAdministrador.listar()) {
            if (a.isActivo() && a.getEmail().equalsIgnoreCase(email)
                    && a.getContrasena().equals(contrasena)) {
                return a;
            }
        }

        // recepcionista
        for (Recepcionista r : gestorRecepcionista.listar()) {
            if (r.isActivo() && r.getEmail().equalsIgnoreCase(email)
                    && r.getContrasena().equals(contrasena)) {
                return r;
            }
        }

        // medico
        for (Medico m : gestorMedico.listar()) {
            if (m.isActivo() && m.getEmail().equalsIgnoreCase(email)
                    && m.getContrasena().equals(contrasena)) {
                return m;
            }
        }

        // paciente
        for (Paciente p : gestorPaciente.listar()) {
            if (p.isActivo() && p.getEmail().equalsIgnoreCase(email)
                    && p.getContrasena().equals(contrasena)) {
                return p;
            }
        }

        return null;
    }

    // ================================
    //   REGISTRO DE PACIENTE
    // ================================
    public Paciente registrarPaciente(String nombre, String apellido, int dni, int telefono,
                                      String email, String contrasena, String obraSocial) {

        int nuevoId = gestorPaciente.listar().size() + 1;

        Paciente nuevo = new Paciente(nuevoId, nombre, apellido, dni, telefono, email,
                contrasena, obraSocial);

        gestorPaciente.agregar(nuevo);

        return nuevo;
    }

    // ================================
    //        MÉTODOS PACIENTE
    // ================================
    public List<Medico> obtenerMedicosActivos() {
        List<Medico> activos = new ArrayList<>();
        for (Medico m : gestorMedico.listar()) {
            if (m.isActivo()) activos.add(m);
        }
        return activos;
    }

    public Turno pedirTurno(Paciente paciente, Medico medico, LocalDateTime fechaHora) {
        int nuevoId = gestorTurno.listar().size() + 1;

        Turno t = new Turno(nuevoId, paciente, medico, fechaHora, EstadoTurno.PENDIENTE);
        gestorTurno.agregar(t);

        // actualizar agenda del médico
        medico.agregarTurnoALaAgenda(t);

        return t;
    }

    public boolean cancelarTurnoPaciente(Paciente paciente, int idTurno) {
        Turno t = gestorTurno.buscarPorId(idTurno);

        if (t != null && t.isActivo() && t.getPaciente().getId() == paciente.getId()) {
            t.setEstado(EstadoTurno.CANCELADO);
            gestorTurno.modificar(t);
            return true;
        }
        return false;
    }

    public List<Turno> obtenerTurnosDePaciente(Paciente paciente) {
        List<Turno> result = new ArrayList<>();

        for (Turno t : gestorTurno.listar()) {
            if (t.isActivo() && t.getPaciente().getId() == paciente.getId()) {
                result.add(t);
            }
        }

        return result;
    }

    public List<Consulta> obtenerHistorialPaciente(Paciente paciente) {
        return historialPorPaciente.getOrDefault(paciente.getId(), new ArrayList<>());
    }

    public List<Facturacion> obtenerFacturasDePaciente(Paciente paciente) {
        List<Facturacion> result = new ArrayList<>();

        for (Facturacion f : gestorFacturacion.listar()) {
            if (f.isActivo()) {
                int idPac = f.getConsulta().getTurno().getPaciente().getId();
                if (idPac == paciente.getId()) result.add(f);
            }
        }

        return result;
    }

    public boolean pagarFactura(int idFactura) {
        Facturacion f = gestorFacturacion.buscarPorId(idFactura);

        if (f != null && f.isActivo() && !f.isPagado()) {
            f.setPagado(true);
            gestorFacturacion.modificar(f);
            return true;
        }
        return false;
    }

    // ================================
    //       REGISTRAR CONSULTA
    // ================================
    public Consulta registrarConsulta(Turno turno, String diagnostico, String observaciones) {

        int nuevoId = gestorConsulta.listar().size() + 1;

        Consulta c = new Consulta(nuevoId, turno, diagnostico, observaciones);
        gestorConsulta.agregar(c);

        int idPac = turno.getPaciente().getId();

        historialPorPaciente
                .computeIfAbsent(idPac, k -> new ArrayList<>())
                .add(c);

        return c;
    }

    // ================================
    //          MÉTODOS MÉDICO
    // ================================
    public List<Turno> obtenerTurnosDeMedico(Medico medico) {
        List<Turno> result = new ArrayList<>();

        for (Turno t : gestorTurno.listar()) {
            if (t.isActivo() && t.getMedico().getId() == medico.getId()) {
                result.add(t);
            }
        }

        return result;
    }

    public boolean medicoCancelaTurno(Medico medico, int idTurno) {
        Turno t = gestorTurno.buscarPorId(idTurno);

        if (t != null && t.isActivo() && t.getMedico().getId() == medico.getId()) {
            t.setEstado(EstadoTurno.CANCELADO);
            gestorTurno.modificar(t);
            return true;
        }

        return false;
    }

    public Turno consultarTurno(int idTurno) {
        return gestorTurno.buscarPorId(idTurno);
    }

    // ================================
    //     MÉTODOS RECEPCIONISTA
    // ================================
    public boolean confirmarTurno(int idTurno) {
        Turno t = gestorTurno.buscarPorId(idTurno);

        if (t != null && t.isActivo()) {
            t.setEstado(EstadoTurno.CONFIRMADO);
            gestorTurno.modificar(t);
            return true;
        }
        return false;
    }

    public Turno agendarTurnoRecepcionista(int idPaciente, int idMedico, LocalDateTime fechaHora) {
        Paciente p = gestorPaciente.buscarPorId(idPaciente);
        Medico m = gestorMedico.buscarPorId(idMedico);
        return pedirTurno(p, m, fechaHora);
    }

    public Map<Medico, List<Turno>> obtenerAgendaCompleta() {
        Map<Medico, List<Turno>> mapa = new HashMap<>();

        for (Medico m : gestorMedico.listar()) {
            if (m.isActivo()) {
                mapa.put(m, obtenerTurnosDeMedico(m));
            }
        }
        return mapa;
    }

    // ================================
    //      MÉTODOS ADMINISTRADOR
    // ================================
    public Medico crearMedico(String nombre, String apellido, int dni, int telefono,
                              String email, String contrasena, String especialidad, String matricula) {

        int nuevoId = gestorMedico.listar().size() + 1;

        Medico m = new Medico(nuevoId, nombre, apellido, dni, telefono, email,
                contrasena, especialidad, matricula);

        gestorMedico.agregar(m);
        especialidades.add(especialidad);

        return m;
    }

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

    public Recepcionista crearRecepcionista(String nombre, String apellido, int dni, int telefono,
                                            String email, String contrasena, int legajo) {

        int nuevoId = gestorRecepcionista.listar().size() + 1;

        Recepcionista r = new Recepcionista(nuevoId, nombre, apellido, dni,
                telefono, email, contrasena, legajo);

        gestorRecepcionista.agregar(r);
        return r;
    }

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

    // ================================
    //           GETTERS
    // ================================
    public GestorPaciente getGestorPaciente() { return gestorPaciente; }
    public GestorMedico getGestorMedico() { return gestorMedico; }
    public GestorTurno getGestorTurno() { return gestorTurno; }
    public GestorConsulta getGestorConsulta() { return gestorConsulta; }
    public GestorFacturacion getGestorFacturacion() { return gestorFacturacion; }
    public GestorAdministrador getGestorAdministrador() { return gestorAdministrador; }
    public GestorRecepcionista getGestorRecepcionista() { return gestorRecepcionista; }
}
