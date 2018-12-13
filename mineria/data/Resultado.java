package mineria.data;

public class Resultado {

    private int id;
    private double p_correctas;
    private double p_incorrectas;
    private double kappa;
    private double error_absoluto_medio;
    private double error_cuadratico_medio;
    private double error_relativo_absoluto;
    private double error_cuadratico_relativo;
    private int instancias;
    private int id_dataset;
    private int id_algoritmo;

    public Resultado(int id, double p_correctas, double p_incorrectas, double kappa, double error_absoluto_medio, double error_cuadratico_medio, double error_relativo_absoluto, double error_cuadratico_relativo, int instancias, int id_dataset, int id_algoritmo) {
        this.id = id;
        this.p_correctas = p_correctas;
        this.p_incorrectas = p_incorrectas;
        this.kappa = kappa;
        this.error_absoluto_medio = error_absoluto_medio;
        this.error_cuadratico_medio = error_cuadratico_medio;
        this.error_relativo_absoluto = error_relativo_absoluto;
        this.error_cuadratico_relativo = error_cuadratico_relativo;
        this.instancias = instancias;
        this.id_dataset = id_dataset;
        this.id_algoritmo = id_algoritmo;
    }

    public Resultado() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getP_correctas() {
        return p_correctas;
    }

    public void setP_correctas(double p_correctas) {
        this.p_correctas = p_correctas;
    }

    public double getP_incorrectas() {
        return p_incorrectas;
    }

    public void setP_incorrectas(double p_incorrectas) {
        this.p_incorrectas = p_incorrectas;
    }

    public double getKappa() {
        return kappa;
    }

    public void setKappa(double kappa) {
        this.kappa = kappa;
    }

    public double getError_absoluto_medio() {
        return error_absoluto_medio;
    }

    public void setError_absoluto_medio(double error_absoluto_medio) {
        this.error_absoluto_medio = error_absoluto_medio;
    }

    public double getError_cuadratico_medio() {
        return error_cuadratico_medio;
    }

    public void setError_cuadratico_medio(double error_cuadratico_medio) {
        this.error_cuadratico_medio = error_cuadratico_medio;
    }

    public double getError_relativo_absoluto() {
        return error_relativo_absoluto;
    }

    public void setError_relativo_absoluto(double error_relativo_absoluto) {
        this.error_relativo_absoluto = error_relativo_absoluto;
    }

    public double getError_cuadratico_relativo() {
        return error_cuadratico_relativo;
    }

    public void setError_cuadratico_relativo(double error_cuadratico_relativo) {
        this.error_cuadratico_relativo = error_cuadratico_relativo;
    }

    public int getInstancias() {
        return instancias;
    }

    public void setInstancias(int instancias) {
        this.instancias = instancias;
    }

    public int getId_dataset() {
        return id_dataset;
    }

    public void setId_dataset(int id_dataset) {
        this.id_dataset = id_dataset;
    }

    public int getId_algoritmo() {
        return id_algoritmo;
    }

    public void setId_algoritmo(int id_algoritmo) {
        this.id_algoritmo = id_algoritmo;
    }

    @Override
    public String toString() {
        return "Resultado{" + "id=" + id + ", p_correctas=" + p_correctas + ", p_incorrectas=" + p_incorrectas + ", kappa=" + kappa + ", error_absoluto_medio=" + error_absoluto_medio + ", error_cuadratico_medio=" + error_cuadratico_medio + ", error_relativo_absoluto=" + error_relativo_absoluto + ", error_cuadratico_relativo=" + error_cuadratico_relativo + ", instancias=" + instancias + ", id_dataset=" + id_dataset + ", id_algoritmo=" + id_algoritmo + '}';
    }

}
