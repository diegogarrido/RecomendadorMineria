package mineria.data;

import java.util.Arrays;

public class Dataset {

    private int id;
    private String nombre;
    private double porcentaje_nulos;
    private double porcentaje_correlacion;
    private double desbalance;
    private int atributos_nominales;
    private int atributos_discretos;
    private int instancias;
    private int atributo_obj;
    private String tipo_variable_objetivo;
    private Atributo[] atributos;
    private Instancia[] datos;
    private int mejorAlgoritmo;

    public Dataset(int id, String nombre, double porcentaje_nulos, double porcentaje_correlacion, double desbalance, int atributos_nominales, int atributos_discretos, int instancias, int atributo_obj, String tipo_variable_objetivo, Atributo[] atributos, Instancia[] datos, int mejorAlgoritmo) {
        this.id = id;
        this.nombre = nombre;
        this.porcentaje_nulos = porcentaje_nulos;
        this.porcentaje_correlacion = porcentaje_correlacion;
        this.desbalance = desbalance;
        this.atributos_nominales = atributos_nominales;
        this.atributos_discretos = atributos_discretos;
        this.instancias = instancias;
        this.atributo_obj = atributo_obj;
        this.tipo_variable_objetivo = tipo_variable_objetivo;
        this.atributos = atributos;
        this.datos = datos;
        this.mejorAlgoritmo = mejorAlgoritmo;
    }

    public Dataset() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPorcentaje_nulos() {
        return porcentaje_nulos;
    }

    public void setPorcentaje_nulos(double porcentaje_nulos) {
        this.porcentaje_nulos = porcentaje_nulos;
    }

    public double getPorcentaje_correlacion() {
        return porcentaje_correlacion;
    }

    public void setPorcentaje_correlacion(double porcentaje_correlacion) {
        this.porcentaje_correlacion = porcentaje_correlacion;
    }

    public double getDesbalance() {
        return desbalance;
    }

    public void setDesbalance(double desvalance) {
        this.desbalance = desvalance;
    }

    public int getAtributos_nominales() {
        return atributos_nominales;
    }

    public void setAtributos_nominales(int atributos_nominales) {
        this.atributos_nominales = atributos_nominales;
    }

    public int getAtributos_discretos() {
        return atributos_discretos;
    }

    public void setAtributos_discretos(int atributos_discretos) {
        this.atributos_discretos = atributos_discretos;
    }

    public int getInstancias() {
        return instancias;
    }

    public void setInstancias(int instancias) {
        this.instancias = instancias;
    }

    public String getTipo_variable_objetivo() {
        return tipo_variable_objetivo;
    }

    public void setTipo_variable_objetivo(String tipo_variable_objetivo) {
        this.tipo_variable_objetivo = tipo_variable_objetivo;
    }

    public Atributo[] getAtributos() {
        return atributos;
    }

    public void setAtributos(Atributo[] atributos) {
        this.atributos = atributos;
    }

    public Instancia[] getDatos() {
        return datos;
    }

    public void setDatos(Instancia[] datos) {
        this.datos = datos;
    }

    public int getAtributo_obj() {
        return atributo_obj;
    }

    public void setAtributo_obj(int atributo_obj) {
        this.atributo_obj = atributo_obj;
    }

    public int getTotal_atributos() {
        return getAtributos_discretos() + getAtributos_nominales();
    }

    public int getMejorAlgoritmo() {
        return mejorAlgoritmo;
    }

    public void setMejorAlgoritmo(int mejorAlgoritmo) {
        this.mejorAlgoritmo = mejorAlgoritmo;
    }

    @Override
    public String toString() {
        return "Dataset{" + "id=" + id + ", nombre=" + nombre + ", porcentaje_nulos=" + porcentaje_nulos + ", porcentaje_correlacion=" + porcentaje_correlacion + ", desbalance=" + desbalance + ", atributos_nominales=" + atributos_nominales + ", atributos_discretos=" + atributos_discretos + ", instancias=" + instancias + ", atributo_obj=" + atributo_obj + ", tipo_variable_objetivo=" + tipo_variable_objetivo + ", atributos=" + atributos + ", datos=" + datos + ", mejorAlgoritmo=" + mejorAlgoritmo + '}';
    }
 
}
