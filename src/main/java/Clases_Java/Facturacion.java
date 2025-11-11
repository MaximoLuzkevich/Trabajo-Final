package Clases_Java;

import Enums.MetodoPago;
import java.util.Objects;

public class Facturacion {
    private int id;
    private Consulta consulta;
    private double monto;
    private MetodoPago metodoPago;
    private boolean pagado;
    private boolean activo;

    public Facturacion(int id, Consulta consulta, double monto, MetodoPago metodoPago, boolean pagado) {
        this.id = id;
        this.consulta = consulta;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.pagado = pagado;
        this.activo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Consulta getConsulta() { return consulta; }
    public void setConsulta(Consulta consulta) { this.consulta = consulta; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Factura ID: " + id +
                ", Consulta: " + consulta.getId() +
                ", Monto: $" + monto +
                ", Método de pago: " + metodoPago +
                ", Pagado: " + pagado +
                ", Activo: " + activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facturacion)) return false;
        Facturacion f = (Facturacion) o;
        return id == f.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
