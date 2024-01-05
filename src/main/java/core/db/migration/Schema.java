package core.db.migration;

import java.util.*;
import com.google.gson.Gson;

public class Schema {

    private List<Table> tables;
     Map<String, Map<String, Object>> data;

    public Schema() {
        this.tables = new ArrayList<>();
        this.data = new LinkedHashMap<>();
    }

    public Table addTable(String tableName) {
        Table table = new Table(tableName);
        this.tables.add(table);
        return table;
    }

    public void save() {
        // Aquí podrías guardar el esquema como un archivo JSON si lo deseas
    }

    public String getStructure() {
        /*
        * Esta es la estructura del JSON que representa cómo se guardarán los datos. 
        * Es importante destacar que el List final no solamente contendrá Strings, ya 
        * que una columna puede contener enteros u otros tipos de datos. Sin embargo, 
        * en este punto, esto es únicamente para crear la estructura del JSON y no se 
        * guardarán datos reales.
        * 
        * La estructura es la siguiente:
        * 
        * {
        *     "Table1": {
        *         "header-metadata-colums": [
        *             ["int", "notnull", "auto_increment", "primary_key", "size:30"],
        *             ["string", "notnull"]
        *         ],
        *         "colum1": ["1", "2"],
        *         "colum2": ["data1", "data2"]
        *     },
        *     "Table2": {
        *         "header-metadata-colums": [
        *             ["int", "notnull", "auto_increment", "primary_key", "size:30"],
        *             ["string", "notnull"]
        *         ],
        *         "colum1": ["3", "4"],
        *         "colum2": ["data3", "data4"]
        *     }
        * }
        */


        /* 
         * Se crea un LinkedHashMap para guardar
         * el orden
         */
        for (int i = 0; i < this.tables.size(); i++) {
            Table table = this.tables.get(i);
            String nameTable = table.getName();
        
            Map<String, Object> tableData = new LinkedHashMap<>();
            tableData.put("header-metadata-colums", table.getConfigColumns());
        
            List<String> columns = table.getColumns();
        
            // Iterar sobre las columnas y agregar listas vacías para cada una
            for (String column : columns) {
                tableData.put(column, new ArrayList<>());
            }
        
            data.put(nameTable, tableData);
        }
        
        Gson gson = new Gson();
        return gson.toJson(data);
    
    }
}