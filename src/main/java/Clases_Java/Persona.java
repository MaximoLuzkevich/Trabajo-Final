package Clases_Java;

import java.util.Objects;
import Enums.TipoUsuario;

public abstract class Persona {
    protected int id;
    protected String nombre;
    protected String apellido;
    protected int dni;
    protected int telefono;
    protected String email;
    protected String contrasena;
    protected TipoUsuario tipoUsuario; // agregado

    public Persona(int id, String nombre, String apellido, int dni, int telefono,
                   String email, String contrasena, TipoUsuario tipoUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public int getDni() { return dni; }
    public void setDni(int dni) { this.dni = dni; }

    public int getTelefono() { return telefono; }
    public void setTelefono(int telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Nombre: " + nombre +
                ", Apellido: " + apellido +
                ", DNI: " + dni +
                ", Teléfono: " + telefono +
                ", Email: " + email +
                ", Tipo: " + tipoUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Persona)) return false;
        Persona persona = (Persona) o;
        return dni == persona.dni;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }
}
