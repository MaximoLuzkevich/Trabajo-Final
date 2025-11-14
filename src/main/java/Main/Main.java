package Main;

import Clases_Java.*;
import Excepciones.TurnoNoDisponibleException;
import Excepciones.UsuarioNoEncontradoException;
import Servicios.ServicioConsultorio;

import java.time.LocalDateTime;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        ServicioConsultorio servicio = new ServicioConsultorio();
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {

            System.out.println("====================================");
            System.out.println("      CONSULTORIO MEDICO - INICIO   ");
            System.out.println("====================================");
            System.out.println("1. Iniciar sesion");
            System.out.println("2. Registrarse (paciente)");
            System.out.println("3. Salir");

            int opcionInicio = leerEntero(sc, "Opcion: ");

            switch (opcionInicio) {

                case 1:
                    System.out.println("\n=== INICIAR SESION ===");
                    String emailLogin = leerEmailValido(sc, "Email: ");
                    System.out.print("Contrasena: ");
                    String passLogin = sc.nextLine();

                    Persona usuario = servicio.login(emailLogin, passLogin);

                    if (usuario == null) {
                        System.out.println("Usuario o contrasena incorrectos, o usuario inactivo.");
                    } else {
                        System.out.println("Bienvenido " + usuario.getNombre() +
                                " (" + usuario.getTipoUsuario() + ")");

                        if (usuario instanceof Paciente) {
                            menuPaciente((Paciente) usuario, servicio, sc);
                        } else if (usuario instanceof Medico) {
                            menuMedico((Medico) usuario, servicio, sc);
                        } else if (usuario instanceof Recepcionista) {
                            menuRecepcionista((Recepcionista) usuario, servicio, sc);
                        } else if (usuario instanceof Administrador) {
                            menuAdministrador((Administrador) usuario, servicio, sc);
                        }
                    }
                    break;

                case 2:
                    System.out.println("\n=== REGISTRO DE PACIENTE ===");
                    String nom = leerSoloLetras(sc, "Nombre: ");
                    String ape = leerSoloLetras(sc, "Apellido: ");
                    int dni = leerEntero(sc, "DNI (solo numeros): ");
                    int tel = leerEntero(sc, "Telefono (solo numeros): ");
                    String mail = leerEmailValido(sc, "Email: ");
                    System.out.print("Contrasena: ");
                    String contra = sc.nextLine();
                    System.out.print("Obra social: ");
                    String obra = sc.nextLine();

                    Paciente nuevo = servicio.registrarPaciente(
                            nom, ape, dni, tel, mail, contra, obra
                    );

                    System.out.println("Paciente registrado con ID: " + nuevo.getId());
                    menuPaciente(nuevo, servicio, sc);
                    break;

                case 3:
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }
        }

        sc.close();
    }


    // ===================== MENU PACIENTE =====================

    private static void menuPaciente(Paciente paciente, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n--- MENU PACIENTE ---");
            System.out.println("1. Pedir turno");
            System.out.println("2. Cancelar turno");
            System.out.println("3. Ver mis turnos");
            System.out.println("4. Ver historial medico");
            System.out.println("5. Ver facturas y pagar");
            System.out.println("6. Volver");

            int op = leerEntero(sc, "Opcion: ");

            switch (op) {

                case 1:
                    List<Medico> medicos = servicio.obtenerMedicosActivos();
                    if (medicos.isEmpty()) {
                        System.out.println("No hay medicos activos.");
                        break;
                    }

                    System.out.println("\nMedicos disponibles:");
                    for (Medico m : medicos) {
                        System.out.println(m.getId() + " - " + m.getNombre() + " " + m.getApellido()
                                + " (" + m.getEspecialidad() + ")");
                    }

                    int idMed = leerEntero(sc, "Ingrese ID del medico: ");
                    Medico medicoElegido = null;

                    for (Medico m : medicos) {
                        if (m.getId() == idMed) {
                            medicoElegido = m;
                            break;
                        }
                    }

                    if (medicoElegido == null) {
                        System.out.println("Medico no encontrado.");
                        break;
                    }

                    LocalDateTime fechaTurno = LocalDateTime.now().plusDays(1);

                    try {
                        servicio.pedirTurno(paciente, medicoElegido, fechaTurno);
                        System.out.println("Turno pedido para " + fechaTurno);
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 2:
                    List<Turno> turnosPac = servicio.obtenerTurnosDePaciente(paciente);

                    if (turnosPac.isEmpty()) {
                        System.out.println("No tiene turnos.");
                        break;
                    }

                    System.out.println("\nSus turnos:");
                    for (Turno t : turnosPac) {
                        System.out.println(t.getId() + " - " + t.getFechaHora() + " - " + t.getEstado());
                    }

                    int idT = leerEntero(sc, "ID del turno a cancelar: ");

                    try {
                        boolean ok = servicio.cancelarTurnoPaciente(paciente, idT);
                        System.out.println(ok ? "Turno cancelado." : "No se pudo cancelar el turno.");
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 3:
                    List<Turno> turnos = servicio.obtenerTurnosDePaciente(paciente);

                    if (turnos.isEmpty()) {
                        System.out.println("No tiene turnos.");
                    } else {
                        System.out.println("\n--- MIS TURNOS ---");
                        for (Turno t : turnos) {
                            System.out.println(t);
                        }
                    }
                    break;

                case 4:
                    List<Consulta> historial = servicio.obtenerHistorialPaciente(paciente);

                    if (historial.isEmpty()) {
                        System.out.println("No tiene consultas registradas.");
                    } else {
                        System.out.println("\n--- HISTORIAL MEDICO ---");

                        List<Facturacion> facturasPaciente = servicio.obtenerFacturasDePaciente(paciente);
                        Map<Integer, Facturacion> factPorConsulta = new HashMap<>();

                        for (Facturacion f : facturasPaciente) {
                            factPorConsulta.put(f.getConsulta().getId(), f);
                        }

                        for (Consulta c : historial) {
                            Facturacion f = factPorConsulta.get(c.getId());
                            String estadoPago = (f != null && f.isPagado()) ? "PAGADA" : (f != null ? "PENDIENTE" : "SIN FACTURA");
                            String infoFactura = (f != null) ? " | ID Factura: " + f.getId() : "";

                            System.out.println("Consulta: " + c.getId()
                                    + " | Diagnostico: " + c.getDiagnostico()
                                    + " | Observaciones: " + c.getObservaciones()
                                    + " | Estado pago: " + estadoPago
                                    + infoFactura);
                        }

                        System.out.print("\nDesea pagar una factura pendiente? (s/n): ");
                        String resp = sc.nextLine().trim().toLowerCase();

                        if (resp.equals("s")) {
                            int idFacturaPagar = leerEntero(sc, "ID de factura: ");

                            System.out.print("Ingrese numero de tarjeta (simulado): ");
                            String tarjeta = sc.nextLine();

                            boolean pagoOk = servicio.pagarFactura(idFacturaPagar);
                            System.out.println(pagoOk ? "Factura pagada." : "No se pudo pagar.");
                        }
                    }
                    break;

                case 5:
                    List<Facturacion> facturas = servicio.obtenerFacturasDePaciente(paciente);

                    if (facturas.isEmpty()) {
                        System.out.println("No tiene facturas.");
                        break;
                    }

                    System.out.println("\n--- FACTURAS ---");
                    for (Facturacion f : facturas) {
                        System.out.println("ID: " + f.getId()
                                + " | Consulta: " + f.getConsulta().getId()
                                + " | Monto: " + f.getMonto()
                                + " | Pagado: " + f.isPagado());
                    }

                    int idF = leerEntero(sc, "ID de factura a pagar (0 para volver): ");
                    if (idF == 0) break;

                    System.out.print("Ingrese numero de tarjeta (simulado): ");
                    sc.nextLine();

                    boolean pago = servicio.pagarFactura(idF);
                    System.out.println(pago ? "Factura pagada." : "No se pudo pagar.");
                    break;

                case 6:
                    volver = true;
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }


    // ===================== MENU MEDICO =====================

    private static void menuMedico(Medico medico, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n--- MENU MEDICO ---");
            System.out.println("1. Ver agenda");
            System.out.println("2. Consultar un turno");
            System.out.println("3. Cancelar turno");
            System.out.println("4. Volver");

            int op = leerEntero(sc, "Opcion: ");

            switch (op) {

                case 1:
                    List<Turno> turnosMed = servicio.obtenerTurnosDeMedico(medico);
                    if (turnosMed.isEmpty()) System.out.println("No tiene turnos.");
                    else {
                        System.out.println("\n--- AGENDA ---");
                        for (Turno t : turnosMed) System.out.println(t);
                    }
                    break;

                case 2:
                    int idT = leerEntero(sc, "ID del turno: ");
                    try {
                        Turno t = servicio.consultarTurno(idT);
                        System.out.println(t != null ? t : "No existe ese turno.");
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 3:
                    int idTc = leerEntero(sc, "ID del turno a cancelar: ");
                    try {
                        boolean ok = servicio.medicoCancelaTurno(medico, idTc);
                        System.out.println(ok ? "Turno cancelado." : "No se pudo cancelar.");
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 4:
                    volver = true;
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }


    // ===================== MENU RECEPCIONISTA =====================

    private static void menuRecepcionista(Recepcionista recep, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n--- MENU RECEPCIONISTA ---");
            System.out.println("1. Agendar turno");
            System.out.println("2. Confirmar turno");
            System.out.println("3. Ver agendas");
            System.out.println("4. Volver");

            int op = leerEntero(sc, "Opcion: ");

            switch (op) {

                case 1:
                    int idPac = leerEntero(sc, "ID Paciente: ");
                    int idMed = leerEntero(sc, "ID Medico: ");
                    LocalDateTime fecha = LocalDateTime.now().plusDays(1);

                    try {
                        servicio.agendarTurnoRecepcionista(idPac, idMed, fecha);
                        System.out.println("Turno agendado para " + fecha);
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 2:
                    int idT = leerEntero(sc, "ID de turno a confirmar: ");
                    try {
                        boolean ok = servicio.confirmarTurno(idT);
                        System.out.println(ok ? "Turno confirmado." : "No se pudo confirmar.");
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 3:
                    Map<Medico, List<Turno>> agenda = servicio.obtenerAgendaCompleta();

                    System.out.println("\n--- AGENDAS ---");
                    for (Medico m : agenda.keySet()) {
                        System.out.println("Medico: " + m.getNombre() + " " + m.getApellido());
                        List<Turno> lista = agenda.get(m);

                        if (lista.isEmpty()) System.out.println("  Sin turnos");
                        else for (Turno tr : lista) System.out.println("  - " + tr);
                    }
                    break;

                case 4:
                    volver = true;
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }


    // ===================== MENU ADMINISTRADOR =====================

    private static void menuAdministrador(Administrador admin, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;

        while (!volver) {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("1. Crear medico");
            System.out.println("2. Eliminar medico");
            System.out.println("3. Crear recepcionista");
            System.out.println("4. Eliminar recepcionista");
            System.out.println("5. Volver");

            int op = leerEntero(sc, "Opcion: ");

            switch (op) {

                case 1:
                    System.out.println("\n=== Crear medico ===");
                    String nom = leerSoloLetras(sc, "Nombre: ");
                    String ape = leerSoloLetras(sc, "Apellido: ");
                    int dni = leerEntero(sc, "DNI (solo numeros): ");
                    int tel = leerEntero(sc, "Telefono (solo numeros): ");
                    String email = leerEmailValido(sc, "Email: ");
                    System.out.print("Contrasena: ");
                    String pass = sc.nextLine();
                    System.out.print("Especialidad: ");
                    String esp = sc.nextLine();
                    System.out.print("Matricula: ");
                    String mat = sc.nextLine();

                    Medico m = servicio.crearMedico(nom, ape, dni, tel, email, pass, esp, mat);
                    System.out.println("Medico creado con ID " + m.getId());
                    break;

                case 2:
                    int idM = leerEntero(sc, "ID de medico a eliminar: ");
                    try {
                        boolean ok = servicio.eliminarMedico(idM);
                        System.out.println(ok ? "Medico eliminado." : "No se pudo eliminar.");
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("\n=== Crear recepcionista ===");
                    String nr = leerSoloLetras(sc, "Nombre: ");
                    String ar = leerSoloLetras(sc, "Apellido: ");
                    int dnr = leerEntero(sc, "DNI (solo numeros): ");
                    int telr = leerEntero(sc, "Telefono (solo numeros): ");
                    String er = leerEmailValido(sc, "Email: ");
                    System.out.print("Contrasena: ");
                    String cr = sc.nextLine();
                    int leg = leerEntero(sc, "Legajo (solo numeros): ");

                    Recepcionista r = servicio.crearRecepcionista(nr, ar, dnr, telr, er, cr, leg);
                    System.out.println("Recepcionista creado con ID " + r.getId());
                    break;

                case 4:
                    int idR = leerEntero(sc, "ID de recepcionista a eliminar: ");
                    try {
                        boolean okr = servicio.eliminarRecepcionista(idR);
                        System.out.println(okr ? "Recepcionista eliminado." : "No se pudo eliminar.");
                    } catch (Exception e) {
                        System.out.println("ERROR " + e.getMessage());
                    }
                    break;

                case 5:
                    volver = true;
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }


    // ===================== VALIDACIONES =====================

    private static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();

            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Error: ingrese solo numeros.");
            }
        }
    }

    private static String leerSoloLetras(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();

            if (linea.isEmpty()) {
                System.out.println("Error: ingrese texto.");
                continue;
            }

            boolean valido = true;
            for (char c : linea.toCharArray()) {
                if (!Character.isLetter(c) && c != ' ') {
                    valido = false;
                    break;
                }
            }

            if (valido) return linea;

            System.out.println("Error: solo letras y espacios.");
        }
    }

    private static String leerEmailValido(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String email = sc.nextLine().trim();

            if (email.contains("@")) return email;

            System.out.println("Email invalido.");
        }
    }
}
