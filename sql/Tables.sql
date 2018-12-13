DROP TABLE IF EXISTS resultado;
DROP TABLE IF EXISTS instancia;
DROP TABLE IF EXISTS atributo;
DROP TABLE IF EXISTS dataset;
DROP TABLE IF EXISTS algoritmo;

CREATE TABLE algoritmo (
    id int(11) NOT NULL AUTO_INCREMENT,
    nombre varchar(20) NOT NULL UNIQUE,
    tecnica varchar(20) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE dataset (
    id int(11) NOT NULL AUTO_INCREMENT,
    nombre varchar(30) NOT NULL UNIQUE,
    porc_nulos decimal(5,2) NOT NULL,
    porc_correl decimal(5,2) NOT NULL,
    desbalance decimal(7,4) NOT NULL,
    at_nominales int(11) NOT NULL,
    at_discretos int(11) NOT NULL,
    instancias int(11) NOT NULL,
    tipo_var_obj varchar(10) NOT NULL,
    atributo_obj int(11) NOT NULL,
    mejor_algoritmo int(11) DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (mejor_algoritmo) REFERENCES algoritmo (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE instancia(
    id int NOT NULL UNIQUE AUTO_INCREMENT,
    valor varchar(1000) NOT NULL,
    id_dataset int NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (id_dataset) REFERENCES dataset (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE resultado (
    id int(11) NOT NULL AUTO_INCREMENT,
    p_correctas decimal(5,2) NOT NULL,
    p_incorrectas decimal(5,2) NOT NULL,
    kappa decimal(5,2) NOT NULL,
    e_abs_medio decimal(6,3) NOT NULL,
    e_cuad_medio decimal(6,3) NOT NULL,
    e_rel_abs decimal(7,4) NOT NULL,
    e_cuad_rel decimal(7,4) NOT NULL,
    instancias int(11) NOT NULL,
    id_dataset int(11) NOT NULL,
    id_algoritmo int(11) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (id_algoritmo) REFERENCES algoritmo (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FOREIGN KEY (id_dataset) REFERENCES dataset (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE atributo (
    id int NOT NULL AUTO_INCREMENT,
    nombre varchar(50) NOT NULL,
    id_dataset int NOT NULL,
    CONSTRAINT pk PRIMARY KEY (id),
    CONSTRAINT FOREIGN KEY (id_dataset) REFERENCES dataset (id) ON DELETE CASCADE ON UPDATE CASCADE
);

