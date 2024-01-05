package core.db.migration;

import com.google.gson.Gson;
import java.io.*;
import java.util.*;

public class Schema {

    private List<Table> tables;
    public String dirPath;
    public String nameSchema;
    public String structureJson;
    public Map<String, Map<String, Object>> structureObjectJava;
    public String fileNameBaseStructure;
    public String columnNameBaseHeaders;

    public Schema(String name) {
        this.tables = new ArrayList<>();
        this.structureObjectJava = new LinkedHashMap<>();
        this.nameSchema = name;
        this.dirPath = "src/main/java/core/db/migration/shecmas/" + this.nameSchema;
        this.fileNameBaseStructure = "/structure-base-schema.json";
        this.columnNameBaseHeaders = "header-metadata-colums";
    }

    public Table addTable(String tableName) {
        Table table = new Table(tableName);
        this.tables.add(table);
        return table;
    }

    public void save() {
        File dirSave = new File(this.dirPath);
        this.structureJson = getStructureJson();

        if (dirSave.exists()) {
            System.out.println("El schema ya existe.");
            return;
        }

        try {
            if (!dirSave.mkdirs()) {
                System.out.println("No se pudo crear el schema " + this.nameSchema);
                return;
            }

            writeStructureJsonToFile();

            for (Map.Entry<String, Map<String, Object>> entry : this.structureObjectJava.entrySet()) {
                String nameTable = entry.getKey();
                File tableJsonSave = new File(this.dirPath + "/" + nameTable + ".json");

                if (tableJsonSave.exists()) {
                    System.out.println("La tabla " + nameTable + " ya existe");
                    continue;
                }

                if (!tableJsonSave.createNewFile()) {
                    System.out.println("No se pudo crear la tabla " + nameTable);
                    continue;
                }

                writeTableJsonToFile(nameTable, entry.getValue());
            }

        } catch (SecurityException | IOException e) {
            System.out.println("Error al crear el archivo: " + e.getMessage());
        }
    }

    private void writeStructureJsonToFile() throws IOException {
        String filePathSaveStrucureJson = this.dirPath + this.fileNameBaseStructure;
        File StructureSchemaJson = new File(filePathSaveStrucureJson);
        StructureSchemaJson.createNewFile();
        FileWriter writeStructure = new FileWriter(filePathSaveStrucureJson);
        writeStructure.write(this.structureJson);
        writeStructure.close();
    }

    private void writeTableJsonToFile(String nameTable, Map<String, Object> structureTable) throws IOException {
        Gson structureTableJson = new Gson();
        String tableJson = structureTableJson.toJson(structureTable);
        FileWriter writerStructure = new FileWriter(this.dirPath + "/" + nameTable + ".json");
        writerStructure.write(tableJson);
        writerStructure.close();
        System.out.println("La tabla " + nameTable + " se creó correctamente!");
    }

    public Map<String, Map<String, Object>> buildStructure() {
        for (Table table : this.tables) {
            String nameTable = table.getName();
            Map<String, Object> tableData = new LinkedHashMap<>();
            tableData.put(this.columnNameBaseHeaders, table.getConfigColumns());

            for (String column : table.getColumns()) {
                tableData.put(column, new ArrayList<>());
            }

            this.structureObjectJava.put(nameTable, tableData);
        }
        return this.structureObjectJava;
    }

    public String getStructureJson() {
        this.structureJson = (new Gson()).toJson(this.buildStructure());
        return this.structureJson;
    }
}
