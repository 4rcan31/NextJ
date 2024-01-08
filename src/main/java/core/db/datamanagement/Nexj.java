package core.db.datamanagement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;

import core.db.datamanagement.kernel.Kernel;
import core.db.datamanagement.kernel.Reader;

public class Nexj extends Kernel {

    private String schema;

    // query
    public String operation;
    public String from;
    public List<String> columns;
    public List<String> conditions;
    public Map<String, String> values;

    public Object result;

    public Nexj(String schema) {
        this.schema = schema;
        this.columns = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.values = new LinkedHashMap<>();
    }

    public Nexj select(String... columnNames) {
        List<String> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            columns.add(columnName);
        }
        this.operation = "SELECT";
        this.columns = columns;
        return this;
    }

    public Nexj from(String tableName) {
        this.from = tableName;
        return this;
    }

    public Nexj where(String value1, String condition, String value2) {
        this.condition(value1, condition, value2);
        return this;
    }

    public Nexj and(String value1, String condition, String value2) {
        if (this.conditions.size() % 3 != 0) {
            throw new IllegalArgumentException("Incomplete condition found.");
        }

        this.conditions.add("AND");
        this.condition(value1, condition, value2);
        return this;
    }

    private void condition(String value1, String condition, String value2) {
        this.conditions.add(value1);
        this.conditions.add(condition);
        this.conditions.add(value2);
    }

    public Nexj insert(String table) {
        this.operation = "INSERT";
        this.from = table;
        return this;
    }

    public Nexj values(Function<Nexj, Map<String, String>> callback) {
        // en callback.apply es lo que me devuelve la funcion lamda
        this.values = callback.apply(this);
        return this;
    }

    public Nexj update(String table) {
        this.operation = "UPDATE";
        this.from = table;
        return this;
    }

    public List<List<String>> run() {
        Reader read = new Reader(this.from, this.schema);
        Map<String, List<String>> data = read.readData();
        List<Integer> sizes = read.getSizes();

        if (!this.allElementsEqual(sizes)) {
            System.err.println("Hay una falla en los datos");
            System.exit(0);
        }

        int totalRows = sizes.get(0);

        List<List<String>> tableData = new ArrayList<>();

        for (int i = 0; i < totalRows; i++) {
            final int index = i;
            List<String> row = new ArrayList<>();

            for (String columnSelect : this.columns) {
                if (columnSelect.equals("*")) {
                    Map<String, String> rowData = new LinkedHashMap<>();
                    for (Map.Entry<String, List<String>> entry : data.entrySet()) {
                        String column = entry.getKey();
                        List<String> values = entry.getValue();
                        String value = (index < values.size()) ? values.get(index) : null;
                        if (value != null) {
                            rowData.put(column, value);
                        }
                    }

                    if (matchesConditions(rowData)) {
                        row.addAll(rowData.values());
                    }
                } else {
                    if (data.containsKey(columnSelect)) {
                        List<String> values = data.get(columnSelect);
                        if (values != null) {
                            String value = (index < values.size()) ? values.get(index) : null;
                            if (value != null) {
                                row.add(value);
                            }
                        }
                    }
                }
            }

            // Se verifica si la fila no está vacía antes de agregarla a la tabla de datos
            if (!row.isEmpty()) {
                tableData.add(row);
            }
        }

        return tableData;
    }

    private boolean matchesConditions(Map<String, String> rowData) {
        boolean conditionsMatch = true;
        boolean orFlag = false;
        int j = 0;

        while (j < this.conditions.size()) {
            String column = this.conditions.get(j);
            String operator = this.conditions.get(j + 1);
            String value = this.conditions.get(j + 2);
            String dataValue = rowData.get(column);

            if (operator.equals("=")) {
                conditionsMatch &= dataValue.equals(value);
            } else if (operator.equals("<")) {
                conditionsMatch &= (Integer.parseInt(dataValue) < Integer.parseInt(value));
            } else if (operator.equals(">")) {
                conditionsMatch &= (Integer.parseInt(dataValue) > Integer.parseInt(value));
            } else {
                System.out.println("El operador " + operator + " no es soportado");
                System.exit(0);
            }

            j += 3; // Moverse al siguiente conjunto de condiciones

            if (j < this.conditions.size()) {
                String nextLogicalOperator = this.conditions.get(j);
                if (nextLogicalOperator.equals("AND")) {
                    // Continuar evaluando el siguiente conjunto de condiciones
                    j++;
                } else if (nextLogicalOperator.equals("OR")) {
                    if (conditionsMatch) {
                        return true;
                    } else {
                        orFlag = true;
                        j++; // Moverse al siguiente conjunto de condiciones después del OR
                        conditionsMatch = true; // Restablecer a true para la siguiente comparación OR
                    }
                } else {
                    System.out.println("El operador logico " + nextLogicalOperator + " no es soportado");
                    System.exit(0);
                }
            }
        }

        return conditionsMatch || orFlag;
    }

    public String toJson() {
        Gson gson = new Gson();
        String json = gson.toJson(this.result);
        return json;
    }

    public void printJson(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);

        System.out.println(json);
        System.out.println("-------------------------------------------");
    }

}
