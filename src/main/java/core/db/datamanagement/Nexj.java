package core.db.datamanagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Nexj {

    private String schema;
    private String pathShemaFile;

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
        this.values = new HashMap<>();
        this.pathShemaFile = "src/main/java/core/db/migration/shecmas/" + this.schema;
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

    public Map<String, Object> readJson(String table) {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.pathShemaFile + "/" + table + ".json"))) {
            Gson gson = new Gson();
            StringBuilder content = new StringBuilder();
            String line;

            // Leer el archivo y almacenar su contenido en un StringBuilder
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            // Convertir el contenido del archivo a un JsonObject
            JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);

            // Procesar cada entrada del JsonObject y crear un nuevo Map<String, Object>
            Map<String, Object> resultMap = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (value.isJsonArray()) {
                    JsonArray jsonArray = value.getAsJsonArray();

                    // Verificar si el valor es una lista de listas de cadenas
                    if (jsonArray.size() > 0 && jsonArray.get(0).isJsonArray()) {
                        List<List<String>> listOfLists = new ArrayList<>();
                        for (JsonElement arrayElement : jsonArray) {
                            JsonArray innerArray = arrayElement.getAsJsonArray();
                            List<String> innerList = new ArrayList<>();
                            for (JsonElement innerElement : innerArray) {
                                innerList.add(innerElement.getAsString());
                            }
                            listOfLists.add(innerList);
                        }
                        resultMap.put(key, listOfLists);
                    } else { // Si no es una lista de listas, entonces es una lista de cadenas
                        List<String> stringList = new ArrayList<>();
                        for (JsonElement element : jsonArray) {
                            stringList.add(element.getAsString());
                        }
                        resultMap.put(key, stringList);
                    }
                }
            }

            return resultMap;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Manejo de excepciones
        }
    }

    public Map<String, List<String>> readData(String table) {
        Map<String, Object> data = this.readJson(table);

        Map<String, List<String>> extractedData = new LinkedHashMap<>();
        boolean isFirst = true;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (isFirst) {
                isFirst = false;
                continue; // interrumtor para obviar el primero ya que ese es solamente la estrucura
            }

            Object value = entry.getValue();
            if (value instanceof List) {
                List<String> stringList = new ArrayList<>();
                for (Object obj : (List<?>) value) {
                    stringList.add(obj.toString());
                }
                extractedData.put(entry.getKey(), stringList);
            }
        }

        return extractedData;
    }

    public List<List<String>> run() {
        Map<String, List<String>> data = this.readData(this.from);

        List<Integer> sizes = data.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("header-metadata-colums"))
                .map(entry -> entry.getValue().size())
                .collect(Collectors.toList());

        boolean isSame = sizes.stream().distinct().limit(2).count() <= 1;

        if (!isSame) {
            System.err.println("Hay una falla en los datos");
            System.exit(0);
        }

        int totalRows = sizes.get(0); // Obtener el total de filas de las listas

        List<List<String>> tableData = new ArrayList<>();

        for (int i = 0; i < totalRows; i++) {
            final int index = i; // Variable final o efectivamente final

            List<String> row = data.values().stream()
                    .map(valueList -> index < valueList.size() ? valueList.get(index) : "")
                    .collect(Collectors.toList());

            this.printJson(row);
            tableData.add(row);
        }
        this.result = tableData;
        return tableData;
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
