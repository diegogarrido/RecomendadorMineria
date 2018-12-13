package mineria.data;

public class Algoritmo {

    private int id;
    private String nombre;
    private String tecnica;

    public Algoritmo(int id, String nombre, String tecnica) {
        this.id = id;
        this.nombre = nombre;
        this.tecnica = tecnica;
    }

    public Algoritmo() {
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

    public String getTecnica() {
        return tecnica;
    }

    public void setTecnica(String tecnica) {
        this.tecnica = tecnica;
    }

    @Override
    public String toString() {
        return "Algoritmo{" + "id=" + id + ", nombre=" + nombre + ", tecnica=" + tecnica + '}';
    }

}
