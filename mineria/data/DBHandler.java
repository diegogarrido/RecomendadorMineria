package mineria.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBHandler {

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    public DBHandler() {
        conn = null;
        stmt = null;
        rs = null;
    }

    public boolean connect(String db, String u, String p) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?user=" + u + "&password=" + p + "&useSSL=false");
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1049) {
                System.out.println("Database 'mineria' no encontrada");
                System.out.println("Creando database 'mineria'");
                try {
                    conn = DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=" + u + "&password=" + p + "&useSSL=false");
                    stmt = conn.createStatement();
                    stmt.execute("CREATE DATABASE mineria;");
                    conn = DriverManager.getConnection("jdbc:mysql://localhost/" + db + "?user=" + u + "&password=" + p + "&useSSL=false");
                    stmt.close();
                    System.out.println("Database creada!");
                } catch (SQLException ex1) {
                    System.out.println("SQLException: " + ex1.getMessage());
                    System.out.println("SQLState: " + ex1.getSQLState());
                    System.out.println("VendorError: " + ex1.getErrorCode());
                    return false;
                }
            } else {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return false;
            }
        }
        return true;
    }

    public boolean isConnected() {
        return conn != null;
    }

    public Dataset findDataset(String nombre) {
        Dataset d = null;
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                if (stmt.execute("SELECT * FROM dataset WHERE nombre='" + nombre + "'")) {
                    rs = stmt.getResultSet();
                    rs.first();
                    d = new Dataset();
                    d.setId(rs.getInt("id"));
                    d.setNombre(rs.getString("nombre"));
                    d.setAtributos_discretos(rs.getInt("at_discretos"));
                    d.setAtributos_nominales(rs.getInt("at_nominales"));
                    d.setDesbalance(rs.getDouble("desbalance"));
                    d.setInstancias(rs.getInt("instancias"));
                    d.setPorcentaje_correlacion(rs.getDouble("porc_correl"));
                    d.setPorcentaje_nulos(rs.getDouble("porc_nulos"));
                    d.setTipo_variable_objetivo(rs.getString("tipo_var_obj"));
                    if (rs.getString("mejor_algoritmo") == null) {
                        d.setMejorAlgoritmo(-1);
                    } else {
                        d.setMejorAlgoritmo(rs.getInt("mejor_algoritmo"));
                    }
                }
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return null;
            } finally {
                endTransaction();
            }
        }
        return d;
    }

    public boolean insertResultado(Resultado r, String nombreDataset, String nombreAlgoritmo) {
        if (this.isConnected()) {
            try {
                Dataset d = findDataset(nombreDataset);
                Algoritmo a = findAlgoritmo(nombreAlgoritmo);
                stmt = conn.createStatement();
                stmt.execute("INSERT INTO resultado (p_correctas,p_incorrectas,kappa,e_abs_medio,e_cuad_medio,e_rel_abs,e_cuad_rel,instancias,id_dataset,id_algoritmo) VALUES "
                        + "(" + r.getP_correctas() + "," + r.getP_incorrectas() + "," + r.getKappa() + "," + r.getError_absoluto_medio() + "," + r.getError_cuadratico_medio() + "," + r.getError_relativo_absoluto() + "," + r.getError_cuadratico_relativo() + "," + r.getInstancias() + "," + d.getId() + "," + a.getId() + ")");
                stmt = conn.createStatement();
                if (d.getMejorAlgoritmo() == -1) {
                    stmt.execute("UPDATE dataset SET mejor_algoritmo=" + a.getId() + " WHERE nombre='" + nombreDataset + "'");
                } else {
                    Resultado[] results = findResultados(nombreDataset);
                    int idMejor = d.getMejorAlgoritmo();
                    double mejorPorc = 0.0;
                    for (int i = 0; i < results.length; i++) {
                        if (results[i].getP_correctas() > mejorPorc) {
                            mejorPorc = results[i].getP_correctas();
                            idMejor = results[i].getId_algoritmo();
                        }
                    }
                    if (idMejor != -1 && idMejor != d.getMejorAlgoritmo()) {
                        stmt = conn.createStatement();
                        stmt.execute("UPDATE dataset SET mejor_algoritmo=" + idMejor + " WHERE nombre='" + nombreDataset + "'");
                    }
                }
                return true;
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return false;
            } finally {
                endTransaction();
            }
        } else {
            return false;
        }
    }

    public boolean insertDataset(Dataset d) {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("INSERT INTO dataset (nombre,instancias,porc_nulos,porc_correl,desbalance,at_nominales,at_discretos,tipo_var_obj,atributo_obj)"
                        + " VALUES ('" + d.getNombre() + "'," + d.getInstancias() + "," + d.getPorcentaje_nulos() + "," + d.getPorcentaje_correlacion()
                        + "," + d.getDesbalance() + "," + d.getAtributos_nominales() + "," + d.getAtributos_discretos() + ",'" + d.getTipo_variable_objetivo() + "'," + d.getAtributo_obj() + ")");
                stmt.execute("SELECT id FROM dataset WHERE nombre LIKE '" + d.getNombre() + "'");
                rs = stmt.getResultSet();
                rs.first();
                int id = rs.getInt("id");
                for (int i = 0; i < d.getAtributos().length; i++) {
                    stmt = conn.createStatement();
                    stmt.execute("INSERT INTO atributo (nombre,id_dataset) VALUES ('" + d.getAtributos()[i].getNombre() + "'," + id + ")");
                }
                System.out.println(d.getDatos().length);
                for (int i = 0; i < d.getDatos().length; i++) {
                    stmt = conn.createStatement();
                    stmt.execute("INSERT INTO instancia (valor,id_dataset) VALUES ('" + d.getDatos()[i].getValor() + "'," + id + ")");
                }
                
                return true;
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
                return false;
            } finally {
                endTransaction();
            }
        } else {
            return false;
        }
    }

    public Dataset[] retrieveDatasets() {
        Dataset[] datas = null;
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("SELECT id FROM dataset");
                ResultSet res = stmt.getResultSet();
                
                res.last();
                datas = new Dataset[res.getRow()];
                res.beforeFirst();
                res.first();
                for (int i = 0; i < datas.length; i++) {
                    datas[i] = getDataset(res.getInt("id"));
                    res.next();
                }
                res.close();
                stmt.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
        return datas;
    }

    public Dataset getDataset(int id) {
        Dataset d = null;
        if (this.isConnected()) {
            try {
                Statement st = conn.createStatement();
                if (st.execute("SELECT * FROM dataset WHERE id=" + id)) {
                    ResultSet res = st.getResultSet();
                    res.first();
                    d = new Dataset();
                    d.setId(res.getInt("id"));
                    d.setNombre(res.getString("nombre"));
                    d.setAtributos_discretos(res.getInt("at_discretos"));
                    d.setAtributos_nominales(res.getInt("at_nominales"));
                    d.setDesbalance(res.getDouble("desbalance"));
                    d.setInstancias(res.getInt("instancias"));
                    d.setPorcentaje_correlacion(res.getDouble("porc_correl"));
                    d.setPorcentaje_nulos(res.getDouble("porc_nulos"));
                    d.setTipo_variable_objetivo(res.getString("tipo_var_obj"));
                    if (res.getString("mejor_algoritmo") == null) {
                        d.setMejorAlgoritmo(-1);
                    } else {
                        d.setMejorAlgoritmo(res.getInt("mejor_algoritmo"));
                    }
                    res.close();
                    st.close();
                }
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
        return d;
    }

    public boolean insertAlgoritmo(Algoritmo a) {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("INSERT INTO algoritmo (nombre,tecnica) VALUES ('" + a.getNombre() + "','" + a.getTecnica() + "')");
                System.out.println("algoritmo "+a+" insertado");
                return true;
            } catch (SQLException ex) {
                if (ex.getErrorCode() != 1062) {
                    endTransaction();
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                    return false;
                } else {
                    return true;
                }
            } finally {
                endTransaction();
            }
        } else {
            return false;
        }
    }

    public Algoritmo findAlgoritmo(String nombreAlgoritmo) {
        Algoritmo al = null;
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                if (stmt.execute("SELECT * FROM algoritmo WHERE nombre='" + nombreAlgoritmo + "'")) {
                    ResultSet res = stmt.getResultSet();
                    res.first();
                    al = new Algoritmo();
                    al.setId(res.getInt("id"));
                    al.setNombre(res.getString("nombre"));
                    al.setTecnica(res.getString("tecnica"));
                }
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
        return al;
    }

    public Algoritmo getAlgoritmo(int id) {
        Algoritmo al = null;
        if (this.isConnected()) {
            try {
                Statement stamt = conn.createStatement();
                if (stamt.execute("SELECT * FROM algoritmo WHERE id=" + id)) {
                    rs = stamt.getResultSet();
                    rs.first();
                    al = new Algoritmo();
                    al.setId(rs.getInt("id"));
                    al.setNombre(rs.getString("nombre"));
                    al.setTecnica(rs.getString("tecnica"));
                    rs.close();
                    stamt.close();
                }
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }
        return al;
    }

    private void endTransaction() {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException sqlEx) {
            }
            rs = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlEx) {
            }
            stmt = null;
        }
    }

    public Resultado[] findResultados(String nombreDataset) {
        Resultado[] results = null;
        try {
            Dataset d = findDataset(nombreDataset);
            stmt = conn.createStatement();
            if (stmt.execute("SELECT * FROM resultado WHERE id_dataset = " + d.getId())) {
                rs = stmt.getResultSet();
                rs.last();
                results = new Resultado[rs.getRow()];
                rs.beforeFirst();
                int count = 0;
                while (rs.next()) {
                    results[count] = new Resultado();
                    results[count].setError_absoluto_medio(rs.getDouble("e_abs_medio"));
                    results[count].setError_cuadratico_medio(rs.getDouble("e_cuad_medio"));
                    results[count].setError_cuadratico_relativo(rs.getDouble("e_cuad_rel"));
                    results[count].setError_relativo_absoluto(rs.getDouble("e_rel_abs"));
                    results[count].setId(rs.getInt("id"));
                    results[count].setId_algoritmo(rs.getInt("id_algoritmo"));
                    results[count].setId_dataset(rs.getInt("id_dataset"));
                    results[count].setInstancias(rs.getInt("instancias"));
                    results[count].setKappa(rs.getDouble("kappa"));
                    results[count].setP_correctas(rs.getDouble("p_correctas"));
                    results[count].setP_incorrectas(rs.getDouble("p_incorrectas"));
                    count++;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            endTransaction();
        }
        return results;
    }

    public void listData(String table) {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                if (stmt.execute("SELECT * FROM " + table)) {
                    rs = stmt.getResultSet();
                    System.out.println("Listing " + table);
                    int count = 0;
                    while (rs.next()) {
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            System.out.println(rs.getMetaData().getColumnName(i) + ": " + rs.getString(i));
                        }
                        count++;
                        if (!rs.isLast()) {
                            System.out.println("-------------------------------");
                        }
                    }
                    System.out.println("Total: " + count);
                }
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
    }

    public void setUpSchema() {
        dropTables();
        setUpAlgoritmo();
        setUpDataset();
        setUpAtributo();
        setUpInstancia();
        setUpResultado();
    }

    private void dropTables() {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("DROP TABLE IF EXISTS instancia;");
                stmt.execute("DROP TABLE IF EXISTS atributo;");
                stmt.execute("DROP TABLE IF EXISTS resultado;");
                stmt.execute("DROP TABLE IF EXISTS dataset;");
                stmt.execute("DROP TABLE IF EXISTS algoritmo;");
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
    }

    private void setUpAlgoritmo() {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE algoritmo ("
                        + "id int(11) NOT NULL AUTO_INCREMENT,"
                        + "nombre varchar(20) NOT NULL UNIQUE,"
                        + "tecnica varchar(20) NOT NULL,"
                        + "PRIMARY KEY (id)"
                        + ");");
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
    }

    private void setUpAtributo() {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE atributo ("
                        + "id int NOT NULL AUTO_INCREMENT,"
                        + "nombre varchar(50) NOT NULL,"
                        + "id_dataset int NOT NULL,"
                        + "CONSTRAINT pk PRIMARY KEY (id),"
                        + "CONSTRAINT FOREIGN KEY (id_dataset) REFERENCES dataset (id) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
    }

    private void setUpResultado() {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE resultado ("
                        + "id int(11) NOT NULL AUTO_INCREMENT,"
                        + "p_correctas decimal(5,2) NOT NULL,"
                        + "p_incorrectas decimal(5,2) NOT NULL,"
                        + "kappa decimal(5,2) NOT NULL,"
                        + "e_abs_medio decimal(6,3) NOT NULL,"
                        + "e_cuad_medio decimal(6,3) NOT NULL,"
                        + "e_rel_abs decimal(7,4) NOT NULL,"
                        + "e_cuad_rel decimal(7,4) NOT NULL,"
                        + "instancias int(11) NOT NULL,"
                        + "id_dataset int(11) NOT NULL,"
                        + "id_algoritmo int(11) NOT NULL,"
                        + "PRIMARY KEY (id),"
                        + "CONSTRAINT FOREIGN KEY (id_algoritmo) REFERENCES algoritmo (id) ON DELETE CASCADE ON UPDATE CASCADE,"
                        + "CONSTRAINT FOREIGN KEY (id_dataset) REFERENCES dataset (id) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
    }

    private void setUpInstancia() {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE instancia("
                        + "id int NOT NULL UNIQUE AUTO_INCREMENT,"
                        + "valor varchar(1000) NOT NULL,"
                        + "id_dataset int NOT NULL,"
                        + "PRIMARY KEY (id),"
                        + "CONSTRAINT FOREIGN KEY (id_dataset) REFERENCES dataset (id) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
    }

    private void setUpDataset() {
        if (this.isConnected()) {
            try {
                stmt = conn.createStatement();
                stmt.execute("CREATE TABLE dataset ("
                        + "id int(11) NOT NULL AUTO_INCREMENT,"
                        + "nombre varchar(30) NOT NULL UNIQUE,"
                        + "porc_nulos decimal(5,2) NOT NULL,"
                        + "porc_correl decimal(5,2) NOT NULL,"
                        + "desbalance decimal(7,4) NOT NULL,"
                        + "at_nominales int(11) NOT NULL,"
                        + "at_discretos int(11) NOT NULL,"
                        + "instancias int(11) NOT NULL,"
                        + "tipo_var_obj varchar(10) NOT NULL,"
                        + "atributo_obj int(11) NOT NULL,"
                        + "mejor_algoritmo int(11) DEFAULT NULL,"
                        + "PRIMARY KEY (id),"
                        + "CONSTRAINT FOREIGN KEY (mejor_algoritmo) REFERENCES algoritmo (id) ON DELETE CASCADE ON UPDATE CASCADE"
                        + ");");
            } catch (SQLException ex) {
                endTransaction();
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                endTransaction();
            }
        }
    }
}
