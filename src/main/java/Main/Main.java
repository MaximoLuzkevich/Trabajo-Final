package Main; // paquete main

import Clases_Java.*;
import Servicios.ServicioConsultorio;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Main del sistema. Maneja la interacción por consola.
 */
public class Main {

    public static void main(String[] args) {

        ServicioConsultorio servicio = new ServicioConsultorio();
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {

            System.out.println("====================================");
            System.out.println("      CONSULTORIO MÉDICO - INICIO   ");
            System.out.println("====================================");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse (como paciente)");
            System.out.println("3. Salir");
            System.out.print("Opción: ");
            String opcionInicio = sc.nextLine();

            switch (opcionInicio) {

                case "1": // iniciar sesión
                    System.out.println("\n=== INICIAR SESIÓN ===");
                    String emailLogin = leerEmailValido(sc, "Email: ");
                    System.out.print("Contraseña: ");
                    String passLogin = sc.nextLine();

                    Persona usuario = servicio.login(emailLogin, passLogin);

                    if (usuario == null) {
                        System.out.println("Usuario o contraseña incorrectos, o usuario inactivo.");
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

                case "2": // registro de paciente
                    System.out.println("\n=== REGISTRO DE PACIENTE ===");
                    String nom = leerSoloLetras(sc, "Nombre: ");
                    String ape = leerSoloLetras(sc, "Apellido: ");
                    int dni = leerEntero(sc, "DNI (solo números): ");
                    int tel = leerEntero(sc, "Teléfono (solo números): ");
                    String mail = leerEmailValido(sc, "Email: ");
                    System.out.print("Contraseña: ");
                    String contra = sc.nextLine();
                    System.out.print("Obra social: ");
                    String obra = sc.nextLine();

                    Paciente nuevo = servicio.registrarPaciente(
                            nom, ape, dni, tel, mail, contra, obra
                    );
                    System.out.println("Paciente registrado con ID: " + nuevo.getId());
                    // luego del registro lo mando al menú de paciente
                    menuPaciente(nuevo, servicio, sc);
                    break;

                case "3": // salir
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }

        sc.close();
    }

    // ----------------------------------------------------------
    // MENÚ PACIENTE
    // ----------------------------------------------------------
    private static void menuPaciente(Paciente paciente, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ PACIENTE ---");
            System.out.println("1. Pedir turno");
            System.out.println("2. Cancelar turno");
            System.out.println("3. Consultar mis turnos");
            System.out.println("4. Ver historial médico (con estado de pago)");
            System.out.println("5. Ver facturas y pagar");
            System.out.println("6. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine();

            switch (op) {
                case "1": // pedir turno
                    List<Medico> medicos = servicio.obtenerMedicosActivos();
                    if (medicos.isEmpty()) {
                        System.out.println("No hay médicos activos.");
                        break;
                    }
                    System.out.println("\nMédicos disponibles:");
                    for (Medico m : medicos) {
                        System.out.println(m.getId() + " - " + m.getNombre() + " " + m.getApellido()
                                + " (" + m.getEspecialidad() + ")");
                    }
                    int idMed = leerEntero(sc, "Ingrese ID del médico: ");
                    Medico medicoElegido = null;
                    for (Medico m : medicos) {
                        if (m.getId() == idMed) {
                            medicoElegido = m;
                            break;
                        }
                    }
                    if (medicoElegido == null) {
                        System.out.println("Médico no encontrado.");
                        break;
                    }

                    // por simplicidad, turno para mañana a la misma hora
                    LocalDateTime fechaTurno = LocalDateTime.now().plusDays(1);
                    servicio.pedirTurno(paciente, medicoElegido, fechaTurno);
                    System.out.println("Turno pedido para " + fechaTurno);
                    break;

                case "2": // cancelar turno
                    List<Turno> turnosPac = servicio.obtenerTurnosDePaciente(paciente);
                    if (turnosPac.isEmpty()) {
                        System.out.println("No tiene turnos.");
                        break;
                    }
                    System.out.println("\nSus turnos:");
                    for (Turno t : turnosPac) {
                        System.out.println(t.getId() + " - " + t.getFechaHora()
                                + " - " + t.getEstado());
                    }
                    int idT = leerEntero(sc, "ID del turno a cancelar: ");
                    boolean ok = servicio.cancelarTurnoPaciente(paciente, idT);
                    System.out.println(ok ? "Turno cancelado." : "No se pudo cancelar el turno.");
                    break;

                case "3": // consultar turnos
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

                case "4": // ver historial médico + estado de pago
                    List<Consulta> historial = servicio.obtenerHistorialPaciente(paciente);
                    if (historial.isEmpty()) {
                        System.out.println("No tiene consultas registradas.");
                    } else {
                        System.out.println("\n--- HISTORIAL MÉDICO ---");

                        // Mapeo consultaId -> facturación (si existe)
                        List<Facturacion> facturasPaciente = servicio.obtenerFacturasDePaciente(paciente);
                        Map<Integer, Facturacion> factPorConsulta = new HashMap<>();
                        for (Facturacion f : facturasPaciente) {
                            factPorConsulta.put(f.getConsulta().getId(), f);
                        }

                        for (Consulta c : historial) {
                            Facturacion f = factPorConsulta.get(c.getId());
                            String estadoPago;
                            String infoFactura = "";
                            if (f != null) {
                                estadoPago = f.isPagado() ? "PAGADA" : "PENDIENTE";
                                infoFactura = " | ID Factura: " + f.getId();
                            } else {
                                estadoPago = "SIN FACTURA";
                            }
                            System.out.println("Consulta ID: " + c.getId()
                                    + " | Diagnóstico: " + c.getDiagnostico()
                                    + " | Observaciones: " + c.getObservaciones()
                                    + " | Estado pago: " + estadoPago
                                    + infoFactura);
                        }

                        System.out.print("\n¿Desea pagar alguna consulta pendiente? (s/n): ");
                        String resp = sc.nextLine().trim().toLowerCase();
                        if (resp.equals("s")) {
                            int idFacturaPagar = leerEntero(sc, "Ingrese ID de la factura a pagar: ");

                            // Simulamos ingreso de tarjeta (solo para cumplir requerimiento descriptivo)
                            System.out.print("Ingrese número de tarjeta (solo simulación): ");
                            String tarjeta = sc.nextLine();

                            boolean pagoOk = servicio.pagarFactura(idFacturaPagar);
                            System.out.println(pagoOk ? "Factura pagada correctamente."
                                    : "No se pudo pagar la factura (verifique el ID o el estado).");
                        }
                    }
                    break;

                case "5": // ver facturas y pagar
                    List<Facturacion> facturas = servicio.obtenerFacturasDePaciente(paciente);
                    if (facturas.isEmpty()) {
                        System.out.println("No tiene facturas.");
                        break;
                    }
                    System.out.println("\n--- FACTURAS ---");
                    for (Facturacion f : facturas) {
                        System.out.println("ID Factura: " + f.getId() +
                                " | Consulta: " + f.getConsulta().getId() +
                                " | Monto: " + f.getMonto() +
                                " | Pagado: " + f.isPagado());
                    }
                    int idF = leerEntero(sc,
                            "Ingrese ID de la factura a pagar (0 para volver): ");
                    if (idF == 0) break;

                    System.out.print("Ingrese número de tarjeta (solo simulación): ");
                    String tarjeta2 = sc.nextLine();

                    boolean pago = servicio.pagarFactura(idF);
                    System.out.println(pago ? "Factura pagada." : "No se pudo pagar la factura.");
                    break;

                case "6":
                    volver = true;
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // ----------------------------------------------------------
    // MENÚ MÉDICO
    // ----------------------------------------------------------
    private static void menuMedico(Medico medico, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ MÉDICO ---");
            System.out.println("1. Ver mi agenda de turnos");
            System.out.println("2. Consultar un turno");
            System.out.println("3. Cancelar un turno");
            System.out.println("4. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine();

            switch (op) {
                case "1":
                    List<Turno> turnosMed = servicio.obtenerTurnosDeMedico(medico);
                    if (turnosMed.isEmpty()) {
                        System.out.println("No tiene turnos.");
                    } else {
                        System.out.println("\n--- MI AGENDA ---");
                        for (Turno t : turnosMed) {
                            System.out.println(t);
                        }
                    }
                    break;

                case "2":
                    int idT = leerEntero(sc, "ID del turno: ");
                    Turno t = servicio.consultarTurno(idT);
                    if (t == null) {
                        System.out.println("No existe ese turno.");
                    } else {
                        System.out.println(t);
                    }
                    break;

                case "3":
                    int idTc = leerEntero(sc, "ID del turno a cancelar: ");
                    boolean ok = servicio.medicoCancelaTurno(medico, idTc);
                    System.out.println(ok ? "Turno cancelado." : "No se pudo cancelar.");
                    break;

                case "4":
                    volver = true;
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // ----------------------------------------------------------
    // MENÚ RECEPCIONISTA
    // ----------------------------------------------------------
    private static void menuRecepcionista(Recepcionista recep, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ RECEPCIONISTA ---");
            System.out.println("1. Agendar turno");
            System.out.println("2. Confirmar turno");
            System.out.println("3. Ver agendas de todos los médicos");
            System.out.println("4. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine();

            switch (op) {
                case "1":
                    int idPac = leerEntero(sc, "ID Paciente: ");
                    int idMed = leerEntero(sc, "ID Médico: ");
                    LocalDateTime fecha = LocalDateTime.now().plusDays(1);
                    servicio.agendarTurnoRecepcionista(idPac, idMed, fecha);
                    System.out.println("Turno agendado para " + fecha);
                    break;

                case "2":
                    int idT = leerEntero(sc, "ID de turno a confirmar: ");
                    boolean ok = servicio.confirmarTurno(idT);
                    System.out.println(ok ? "Turno confirmado." : "No se pudo confirmar.");
                    break;

                case "3":
                    Map<Medico, List<Turno>> agenda = servicio.obtenerAgendaCompleta();
                    System.out.println("\n--- AGENDAS DE MÉDICOS ---");
                    for (Medico m : agenda.keySet()) {
                        System.out.println("Médico: " + m.getNombre() + " " + m.getApellido());
                        List<Turno> listaTurnos = agenda.get(m);
                        if (listaTurnos.isEmpty()) {
                            System.out.println("  (Sin turnos)");
                        } else {
                            for (Turno tr : listaTurnos) {
                                System.out.println("  - " + tr);
                            }
                        }
                    }
                    break;

                case "4":
                    volver = true;
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // ----------------------------------------------------------
    // MENÚ ADMINISTRADOR
    // ----------------------------------------------------------
    private static void menuAdministrador(Administrador admin, ServicioConsultorio servicio, Scanner sc) {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- MENÚ ADMINISTRADOR ---");
            System.out.println("1. Crear médico");
            System.out.println("2. Eliminar médico");
            System.out.println("3. Crear recepcionista");
            System.out.println("4. Eliminar recepcionista");
            System.out.println("5. Volver");
            System.out.print("Opción: ");
            String op = sc.nextLine();

            switch (op) {
                case "1":
                    System.out.println("\n=== Crear médico ===");
                    String nom = leerSoloLetras(sc, "Nombre: ");
                    String ape = leerSoloLetras(sc, "Apellido: ");
                    int dni = leerEntero(sc, "DNI (solo números): ");
                    int tel = leerEntero(sc, "Teléfono (solo números): ");
                    String email = leerEmailValido(sc, "Email: ");
                    System.out.print("Contraseña: ");
                    String pass = sc.nextLine();
                    System.out.print("Especialidad: ");
                    String esp = sc.nextLine();
                    System.out.print("Matrícula: ");
                    String mat = sc.nextLine();
                    Medico m = servicio.crearMedico(nom, ape, dni, tel, email, pass, esp, mat);
                    System.out.println("Médico creado con ID " + m.getId());
                    break;

                case "2":
                    int idM = leerEntero(sc, "ID de médico a eliminar: ");
                    boolean ok = servicio.eliminarMedico(idM);
                    System.out.println(ok ? "Médico eliminado (baja lógica)." : "No se pudo eliminar.");
                    break;

                case "3":
                    System.out.println("\n=== Crear recepcionista ===");
                    String nr = leerSoloLetras(sc, "Nombre: ");
                    String ar = leerSoloLetras(sc, "Apellido: ");
                    int dnr = leerEntero(sc, "DNI (solo números): ");
                    int telr = leerEntero(sc, "Teléfono (solo números): ");
                    String er = leerEmailValido(sc, "Email: ");
                    System.out.print("Contraseña: ");
                    String cr = sc.nextLine();
                    int leg = leerEntero(sc, "Legajo (solo números): ");
                    Recepcionista r = servicio.crearRecepcionista(nr, ar, dnr, telr, er, cr, leg);
                    System.out.println("Recepcionista creado con ID " + r.getId());
                    break;

                case "4":
                    int idR = leerEntero(sc, "ID de recepcionista a eliminar: ");
                    boolean okr = servicio.eliminarRecepcionista(idR);
                    System.out.println(okr ? "Recepcionista eliminado (baja lógica)." : "No se pudo eliminar.");
                    break;

                case "5":
                    volver = true;
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // ----------------------------------------------------------
    // MÉTODOS DE VALIDACIÓN / INPUT SEGURO
    // ----------------------------------------------------------

    /**
     * Lee un entero por consola. Si el usuario escribe algo que no es numérico,
     * vuelve a pedirlo.
     */
    private static int leerEntero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Error: debe ingresar solo números. Intente nuevamente.");
            }
        }
    }

    /**
     * Lee una cadena que contenga solo letras (y espacios).
     */
    private static String leerSoloLetras(Scanner sc, String mensaje) {
        Pattern patron = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$");
        while (true) {
            System.out.print(mensaje);
            String linea = sc.nextLine().trim();
            if (patron.matcher(linea).matches()) {
                return linea;
            }
            System.out.println("Error: solo se permiten letras. Intente nuevamente.");
        }
    }

    /**
     * Lee un email que contenga al menos un '@'.
     */
    private static String leerEmailValido(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String email = sc.nextLine().trim();
            if (email.contains("@")) {
                return email;
            }
            System.out.println("Error: el email debe contener '@'. Intente nuevamente.");
        }
    }
}

