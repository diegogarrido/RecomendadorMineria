package mineria;

import java.awt.FileDialog;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;
import mineria.data.DBHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;
import mineria.data.Algoritmo;
import mineria.data.Atributo;
import mineria.data.Dataset;
import mineria.data.Instancia;
import mineria.data.Resultado;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.Id3;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class Mineria {

    private static final DBHandler db = new DBHandler();
    private static double valorEsperado;
    private static final double[] funcionDistribucionChiCuadrado005 = new double[]{3.841,
        5.991, 7.815, 9.488, 11.07, 12.59, 15.51, 16.92, 18.31, 19.68,
        21.03, 22.36, 23.68, 25.00, 26.30, 27.59, 28.87, 30.14, 31.41,
        32.67, 33.92, 35.17, 36.42, 37.65, 38.89, 40.11, 41.34, 42.56,
        43.77, 44.99, 46.19, 47.40, 48.60, 49.80, 51.00, 52.19, 53.38,
        54.57, 55.76, 56.94, 58.12, 59.30, 60.48, 61.66, 62.83, 64.00,
        65.17, 66.34, 67.50, 68.67, 69.83, 70.99, 72.15, 73.31, 74.47,
        75.62, 76.78, 77.93, 79.08};

    public static void main(String args[]) {
        int op = JOptionPane.showOptionDialog(null, "Selección de Operación", "Operación", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Agregar Dataset", "Generar ARFF", "Crear BD"}, -1);
        if (op != -1) {
            db.connect("mineria", "admin", "admin");
            if (op == 2) {
                db.setUpSchema();
            } else {
                FileDialog dialog = new FileDialog(new Frame(), "Directorio Dataset");
                dialog.setDirectory("D:\\Desktop\\Mineria de Datos");
                if (op == 0) {
                    dialog.setMultipleMode(true);
                    dialog.setVisible(true);
                    for (int k = 0; k < dialog.getFiles().length; k++) {
                        System.out.println("Procesando archivo: " + dialog.getFiles()[k].getName());
                        String ruta;
                        if (dialog.getFiles().length > 1) {
                            ruta = dialog.getDirectory() + "\\" + dialog.getFiles()[k].getName();
                        } else {
                            ruta = dialog.getDirectory() + dialog.getFiles()[k].getName();
                        }
                        try {
                            DataSource data = new DataSource(ruta);
                            Dataset d = new Dataset();
                            int atributos_discretos = 0, atributos_nominales = 0;
                            Atributo[] atributo = new Atributo[data.getDataSet().numAttributes()];
                            int varObj = data.getDataSet().numAttributes() - 1;
                            data.getDataSet().setClassIndex(varObj);
                            int tipoVar = data.getDataSet().attribute(varObj).type();
                            String[] tipos = new String[]{"Discreto", "Nominal"};
                            String tipoVarObj = tipos[tipoVar];

                            System.out.println("Calculando metadatos...");
                            int desbalanceados = 0;

                            int nulls = 0;
                            Instancia[] datos = new Instancia[data.getDataSet().numInstances()];

                            CorrelationAttributeEval correl = new CorrelationAttributeEval();
                            correl.buildEvaluator(data.getDataSet(varObj));
                            int contCorrel = 0;

                            for (int i = 0; i < data.getDataSet().numAttributes(); i++) {
                                atributo[i] = new Atributo();
                                atributo[i].setNombre(data.getDataSet().attribute(i).name());
                                if (data.getDataSet().attribute(i).type() == 0) {
                                    atributos_discretos++;
                                    double[] valores = new double[data.getDataSet().numInstances()];
                                    for (int j = 0; j < data.getDataSet().numInstances(); j++) {
                                        datos[j] = new Instancia();
                                        datos[j].setValor("" + data.getDataSet().get(i));
                                        Instance ins = data.getDataSet().get(j);
                                        valores[j] = ins.value(i);
                                    }
                                    if (!balanced(valores)) {
                                        desbalanceados++;
                                    }
                                } else if (data.getDataSet().attribute(i).type() == 1) {
                                    atributos_nominales++;
                                    String[] valores = new String[data.getDataSet().numInstances()];
                                    for (int j = 0; j < data.getDataSet().numInstances(); j++) {
                                        datos[j] = new Instancia();
                                        datos[j].setValor("" + data.getDataSet().get(i));
                                        Instance ins = data.getDataSet().get(j);
                                        valores[j] = ins.stringValue(i);
                                    }
                                    if (!balanced(valores)) {
                                        desbalanceados++;
                                    }
                                }

                                if (data.getDataSet().get(i).hasMissingValue()) {
                                    nulls++;
                                }

                                if (i != varObj) {
                                    if (correl.evaluateAttribute(i) > 0.7) {
                                        System.out.println("Correlacion del atributo " + data.getDataSet().attribute(i).name() + " con " + data.getDataSet(varObj).classAttribute().name() + " Excede el 70%!");
                                        contCorrel++;
                                    }
                                }
                            }

                            System.out.println("Atributos Nominales: " + atributos_nominales);
                            System.out.println("Atributos Discretos: " + atributos_discretos);

                            double nulos;
                            if (nulls > 0) {
                                nulos = (nulls * 100) / data.getDataSet().numInstances();
                            } else {
                                nulos = 0.0;
                            }

                            double correlacion = (contCorrel * 100) / data.getDataSet().numAttributes();

                            double desbalance = (desbalanceados * 100) / data.getDataSet().numAttributes();

                            System.out.println("Desbalance: " + desbalance + "% (" + desbalanceados + " de " + data.getDataSet().numAttributes() + " Atributos están desbalanceados)");

                            d.setAtributo_obj(varObj);
                            d.setAtributos_discretos(atributos_discretos);
                            d.setAtributos_nominales(atributos_nominales);
                            d.setDesbalance(desbalance);
                            d.setInstancias(data.getDataSet().numInstances());
                            d.setNombre(dialog.getFiles()[k].getName());
                            d.setPorcentaje_correlacion(correlacion);
                            d.setPorcentaje_nulos(nulos);
                            d.setTipo_variable_objetivo(tipoVarObj);
                            d.setAtributos(atributo);
                            d.setDatos(datos);

                            db.insertDataset(d);

                            Instances training = data.getDataSet();

                            training.setClassIndex(varObj);

                            Classifier cls = new Id3();
                            Algoritmo al = new Algoritmo();
                            al.setNombre("ID3");
                            al.setTecnica("Clasificacion");
                            db.insertAlgoritmo(al);
                            evaluate(cls, training, al, d.getNombre());

                            cls = new J48();
                            al.setNombre("J48");
                            db.insertAlgoritmo(al);
                            evaluate(cls, training, al, d.getNombre());

                            cls = new RandomTree();
                            al.setNombre("RandomTree");
                            db.insertAlgoritmo(al);
                            evaluate(cls, training, al, d.getNombre());

                            //cls = new DecisionTable();
                            //al.setNombre("DecisionTable");
                            //db.insertAlgoritmo(al);
                            //evaluate(cls, training, al, d.getNombre());
                            cls = new REPTree();
                            al.setNombre("REPTree");
                            db.insertAlgoritmo(al);
                            evaluate(cls, training, al, d.getNombre());

                            cls = new M5P();
                            al.setNombre("M5P");
                            db.insertAlgoritmo(al);
                            evaluate(cls, training, al, d.getNombre());

                            cls = new OneR();
                            al.setNombre("OneR");
                            db.insertAlgoritmo(al);
                            evaluate(cls, training, al, d.getNombre());

                        } catch (Exception ex) {
                            System.out.println(ex.toString());
                            System.out.println(ex.getMessage());
                        }
                    }
                } else {
                    dialog.setMode(FileDialog.SAVE);
                    dialog.setVisible(true);
                    System.out.println("Leyendo datasets");
                    Dataset[] datas = db.retrieveDatasets();
                    System.out.println(datas.length + " datasets encontrados");
                    try {
                        ArffSaver m_output = new ArffSaver();
                        File model = new File(dialog.getDirectory() + dialog.getFile());
                        m_output.setFile(model);
                        ArrayList<Attribute> attr = new ArrayList<>();
                        attr.add(new Attribute("PorcentajeNulos", false));
                        attr.add(new Attribute("PorcentajeCorrelacion", false));
                        attr.add(new Attribute("Desbalance", false));
                        attr.add(new Attribute("AtributosNominales", false));
                        attr.add(new Attribute("AtributosDiscretos", false));
                        attr.add(new Attribute("Instancias", false));
                        ArrayList<String> algoritmos = new ArrayList();
                        algoritmos.add("J48");
                        algoritmos.add("REPTree");
                        algoritmos.add("OneR");
                        algoritmos.add("RandomTree");
                        algoritmos.add("Ninguno");
                        algoritmos.add("ID3");
                        algoritmos.add("M5P");
                        attr.add(new Attribute("MejorAlgoritmo", algoritmos));
                        Instances ins = new Instances("DatasetProcesado", attr, 0);
                        for (int i = 0; i < datas.length; i++) {
                            Instance in = new DenseInstance(attr.size());
                            in.setValue(attr.get(0), datas[i].getPorcentaje_nulos());
                            in.setValue(attr.get(1), datas[i].getPorcentaje_correlacion());
                            in.setValue(attr.get(2), datas[i].getDesbalance());
                            in.setValue(attr.get(3), datas[i].getAtributos_nominales());
                            in.setValue(attr.get(4), datas[i].getAtributos_discretos());
                            in.setValue(attr.get(5), datas[i].getInstancias());
                            if (datas[i].getMejorAlgoritmo() == -1) {
                                in.setValue(attr.get(6), "Ninguno");
                            } else {
                                in.setValue(attr.get(6), db.getAlgoritmo(datas[i].getMejorAlgoritmo()).getNombre());
                            }
                            ins.add(in);
                        }
                        m_output.setInstances(ins);
                        m_output.writeBatch();
                        System.out.println("ARFF generado exitosamente");
                        dialog.dispose();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }

        System.exit(0);
    }

    public static void evaluate(Classifier cls, Instances training, Algoritmo al, String nombreDataset) {
        try {
            cls.buildClassifier(training);
            Evaluation eval = new Evaluation(training);
            double numPruebas = (training.numInstances() * 0.25);
            if (numPruebas < 2) {
                numPruebas = 2;
            }
            eval.crossValidateModel(cls, training, (int) numPruebas, new Random(1));
            System.out.println(eval.toSummaryString(al.getNombre(), false));
            Resultado res = new Resultado();
            res.setP_correctas(eval.pctCorrect());
            res.setP_incorrectas(eval.pctIncorrect());
            res.setKappa(eval.kappa());
            res.setError_absoluto_medio(eval.meanAbsoluteError());
            res.setError_cuadratico_medio(eval.rootMeanSquaredError());
            res.setError_relativo_absoluto(eval.relativeAbsoluteError());
            res.setError_cuadratico_relativo(eval.rootRelativeSquaredError());
            res.setInstancias(training.numInstances());
            db.insertResultado(res, nombreDataset, al.getNombre());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static boolean balanced(double[] randomNums) {
        Map<Double, Integer> ht = getFrequencies(randomNums);
        double chiSquare1 = 0;
        for (double v : ht.values()) {
            double f1 = v - valorEsperado;
            chiSquare1 += f1 * f1;
        }
        chiSquare1 /= valorEsperado;
        int GradosLibertad = ht.size();
        if (funcionDistribucionChiCuadrado005.length < (GradosLibertad)) {
            GradosLibertad = funcionDistribucionChiCuadrado005.length;
        }
        if (GradosLibertad >= funcionDistribucionChiCuadrado005.length) {
            return false;
        } else {
            return chiSquare1 < funcionDistribucionChiCuadrado005[GradosLibertad + 1];
        }
    }

    private static Map<Double, Integer> getFrequencies(double[] nums) {
        double cantidadtotal = 0;
        Map<Double, Integer> freqs = new HashMap<>();
        for (double x : nums) {
            cantidadtotal = cantidadtotal + x;
            if (freqs.containsKey(x)) {
                freqs.put(x, freqs.get(x) + 1);
            } else {
                freqs.put(x, 1);
            }
        }
        valorEsperado = cantidadtotal / nums.length;
        return freqs;
    }

    public static boolean balanced/*ChiSquare*/(String[] randomNums) {
        boolean resp = false;
        // Obtiene la frecuencia de aparición de cada elemento
        Map<String, Integer> ht = getFrequenciesString(randomNums);
        // Calcula chi-cuadrado
        if (ht.size() <= 0)//si esta columna esta vacia completa esta balanceada
        {
            resp = true;
        } else {
            double chiSquare1 = 0;
            for (double v : ht.values()) {
                double f1 = v - valorEsperado;
                chiSquare1 += (f1 * f1) / valorEsperado;
            }
            int GradosLibertad = ht.size();
            if (funcionDistribucionChiCuadrado005.length < (GradosLibertad)) {
                GradosLibertad = funcionDistribucionChiCuadrado005.length;
            }
            if (GradosLibertad >= funcionDistribucionChiCuadrado005.length) {
                return false;
            }
            resp = chiSquare1 < funcionDistribucionChiCuadrado005[GradosLibertad - 1];
        }
        return resp;
    }

    private static Map<String, Integer> getFrequenciesString(String[] nums) {
        double count = 0;
        Map<String, Integer> freqs = new HashMap<>();
        for (String x : nums) {
            if (!"?".equals(x)) {
                count = count + 1;
                if (freqs.containsKey(x)) {
                    freqs.put(x, freqs.get(x) + 1);
                } else {
                    freqs.put(x, 1);
                }
            }
        }
        if ((int) freqs.size() <= 0) {
            valorEsperado = 0;
        } else {
            valorEsperado = (double) (nums.length / freqs.size());
        }
        return freqs;
    }
}
