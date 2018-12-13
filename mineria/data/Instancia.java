package mineria.data;

public class Instancia {

    private int id;
    private String valor;
    private int id_dataset;

    public Instancia(int id, String valor, int id_atributo, int id_dataset) {
        this.id = id;
        this.valor = valor;
        this.id_dataset = id_dataset;
    }

    public Instancia() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        valor = valor.replace("'", "");
        valor = valor.replace("?", "-");
        this.valor = valor;
    }

    public int getId_dataset() {
        return id_dataset;
    }

    public void setId_dataset(int id_dataset) {
        this.id_dataset = id_dataset;
    }

    @Override
    public String toString() {
        return "Instancia{" + "id=" + id + ", valor=" + valor + ", id_dataset=" + id_dataset + '}';
    }

}
