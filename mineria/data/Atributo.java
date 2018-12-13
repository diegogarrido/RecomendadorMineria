package mineria.data;

public class Atributo {

    private int id;
    private String nombre;
    private int id_dataset;

    public Atributo(int id, String nombre, int id_dataset) {
        this.id = id;
        this.nombre = nombre;
        this.id_dataset = id_dataset;
    }

    public Atributo() {
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

    public int getId_dataset() {
        return id_dataset;
    }

    public void setId_dataset(int id_dataset) {
        this.id_dataset = id_dataset;
    }

    @Override
    public String toString() {
        return "Atributo{" + "id=" + id + ", nombre=" + nombre + ", id_dataset=" + id_dataset + '}';
    }
    
    
}
