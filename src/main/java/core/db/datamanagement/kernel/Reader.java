package core.db.datamanagement.kernel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Reader extends Kernel {

    String table;
    String schema;
    String columnMetdata;
    private String pathShemaFile;

    public Reader(String table, String schema) {
        this.table = table;
        this.schema = schema;
        this.pathShemaFile = this.pathString + this.schema;
    }

    private Map<String, Object> readJson() {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(this.pathShemaFile + "/" + this.table + ".json"))) {
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



    public Map<String, List<String>> readData() {
        Map<String, Object> data = this.readJson();

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


    public List<Integer> getSizes(){
        return this.readData().entrySet().stream()
                .filter(entry -> !entry.getKey().equals(this.columnMetdata))
                .map(entry -> entry.getValue().size())
                .collect(Collectors.toList());
    }
}
