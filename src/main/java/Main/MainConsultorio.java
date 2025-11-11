package Main; // paquete main

import Clases_Java.*;
import Enums.EstadoTurno;
import Servicios.ServicioConsultorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main del sistema. Maneja la interacción por consola.
 */
public class MainConsultorio {

    public static void main(String[] args) {

        ServicioConsultorio servicio = new ServicioConsultorio(); // creo el servicio
        Scanner sc = new Scanner(System.in); // scanner para leer
        boolean salir = false; // bandera de salida

        while (!salir) { // bucle principal

            // menú de inicio
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
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Contraseña: ");
                    String pass = sc.nextLine();

                    Persona usuario = servicio.login(email, pass);

                    if (usuario == null) {
                        System.out.println("Usuario o contraseña incorrectos, o usuario inactivo.");
                    } else {
                        System.out.println("Bienvenido " + usuario.getNombre() + " (" + usuario.getTipoUsuario() + ")");
                        // en función del tipo de usuario muestro menú
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
                    System.out.println("=== Registro de Paciente ===");
                    System.out.print("Nombre: ");
                    String nom = sc.nextLine();
                    System.out.print("Apellido: ");
                    String ape = sc.nextLine();
                    System.out.print("DNI: ");
                    int dni = Integer.parseInt(sc.nextLine());
                    System.out.print("Teléfono: ");
                    int tel = Integer.parseInt(sc.nextLine());
                    System.out.print("Email: ");
                    String mail = sc.nextLine();
                    System.out.print("Contraseña: ");
                    String contra = sc.nextLine();
                    System.out.print("Obra social: ");
                    String obra = sc.nextLine();

                    Paciente nuevo = servicio.registrarPaciente(nom, ape, dni, tel, mail, contra, obra);
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

        sc.close(); // cierro scanner
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
            System.out.println("4. Ver historial médico");
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
                    System.out.println("Médicos disponibles:");
                    for (Medico m : medicos) {
                        System.out.println(m.getId() + " - " + m.getNombre() + " (" + m.getEspecialidad() + ")");
                    }
                    System.out.print("Ingrese ID del médico: ");
                    int idMed = Integer.parseInt(sc.nextLine());
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
                    // turno para mañana ahora (por simplicidad)
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
                    System.out.println("Sus turnos:");
                    for (Turno t : turnosPac) {
                        System.out.println(t.getId() + " - " + t.getFechaHora() + " - " + t.getEstado());
                    }
                    System.out.print("ID del turno a cancelar: ");
                    int idT = Integer.parseInt(sc.nextLine());
                    boolean ok = servicio.cancelarTurnoPaciente(paciente, idT);
                    System.out.println(ok ? "Turno cancelado." : "No se pudo cancelar el turno.");
                    break;

                case "3": // consultar turnos
                    List<Turno> turnos = servicio.obtenerTurnosDePaciente(paciente);
                    if (turnos.isEmpty()) {
                        System.out.println("No tiene turnos.");
                    } else {
                        for (Turno t : turnos) {
                            System.out.println(t);
                        }
                    }
                    break;

                case "4": // ver historial médico
                    List<Consulta> historial = servicio.obtenerHistorialPaciente(paciente);
                    if (historial.isEmpty()) {
                        System.out.println("No tiene consultas registradas.");
                    } else {
                        for (Consulta c : historial) {
                            System.out.println(c);
                        }
                    }
                    break;

                case "5": // ver facturas y pagar
                    List<Facturacion> facturas = servicio.obtenerFacturasDePaciente(paciente);
                    if (facturas.isEmpty()) {
                        System.out.println("No tiene facturas.");
                        break;
                    }
                    for (Facturacion f : facturas) {
                        System.out.println("ID Factura: " + f.getId() +
                                " | Consulta: " + f.getConsulta().getId() +
                                " | Monto: " + f.getMonto() +
                                " | Pagado: " + f.isPagado());
                    }
                    System.out.print("Ingrese ID de la factura a pagar (0 para volver): ");
                    int idF = Integer.parseInt(sc.nextLine());
                    if (idF == 0) break;
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
                        for (Turno t : turnosMed) {
                            System.out.println(t);
                        }
                    }
                    break;

                case "2":
                    System.out.print("ID del turno: ");
                    int idT = Integer.parseInt(sc.nextLine());
                    Turno t = servicio.consultarTurno(idT);
                    if (t == null) {
                        System.out.println("No existe ese turno.");
                    } else {
                        System.out.println(t);
                    }
                    break;

                case "3":
                    System.out.print("ID del turno a cancelar: ");
                    int idTc = Integer.parseInt(sc.nextLine());
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
                    System.out.print("ID Paciente: ");
                    int idPac = Integer.parseInt(sc.nextLine());
                    System.out.print("ID Médico: ");
                    int idMed = Integer.parseInt(sc.nextLine());
                    LocalDateTime fecha = LocalDateTime.now().plusDays(1);
                    servicio.agendarTurnoRecepcionista(idPac, idMed, fecha);
                    System.out.println("Turno agendado para " + fecha);
                    break;

                case "2":
                    System.out.print("ID de turno a confirmar: ");
                    int idT = Integer.parseInt(sc.nextLine());
                    boolean ok = servicio.confirmarTurno(idT);
                    System.out.println(ok ? "Turno confirmado." : "No se pudo confirmar.");
                    break;

                case "3":
                    Map<Medico, List<Turno>> agenda = servicio.obtenerAgendaCompleta();
                    for (Medico m : agenda.keySet()) {
                        System.out.println("Médico: " + m.getNombre() + " " + m.getApellido());
                        for (Turno tr : agenda.get(m)) {
                            System.out.println("  - " + tr);
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
                    System.out.print("Nombre: ");
                    String nom = sc.nextLine();
                    System.out.print("Apellido: ");
                    String ape = sc.nextLine();
                    System.out.print("DNI: ");
                    int dni = Integer.parseInt(sc.nextLine());
                    System.out.print("Teléfono: ");
                    int tel = Integer.parseInt(sc.nextLine());
                    System.out.print("Email: ");
                    String email = sc.nextLine();
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
                    System.out.print("ID de médico a eliminar: ");
                    int idM = Integer.parseInt(sc.nextLine());
                    boolean ok = servicio.eliminarMedico(idM);
                    System.out.println(ok ? "Médico eliminado (baja lógica)." : "No se pudo eliminar.");
                    break;

                case "3":
                    System.out.print("Nombre: ");
                    String nr = sc.nextLine();
                    System.out.print("Apellido: ");
                    String ar = sc.nextLine();
                    System.out.print("DNI: ");
                    int dnr = Integer.parseInt(sc.nextLine());
                    System.out.print("Teléfono: ");
                    int telr = Integer.parseInt(sc.nextLine());
                    System.out.print("Email: ");
                    String er = sc.nextLine();
                    System.out.print("Contraseña: ");
                    String cr = sc.nextLine();
                    System.out.print("Legajo: ");
                    int leg = Integer.parseInt(sc.nextLine());
                    Recepcionista r = servicio.crearRecepcionista(nr, ar, dnr, telr, er, cr, leg);
                    System.out.println("Recepcionista creado con ID " + r.getId());
                    break;

                case "4":
                    System.out.print("ID de recepcionista a eliminar: ");
                    int idR = Integer.parseInt(sc.nextLine());
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
}

