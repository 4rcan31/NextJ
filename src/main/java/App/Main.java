package App;

import java.util.List;

import core.db.datamanagement.Nexj;

public class Main {
    public static void main(String[] args) {
        Migrations.run();

        Nexj nexj = new Nexj("dbtest");

        List<List<String>> data = nexj.select("*")
                .from("user")
                .run();

        // nexj.printJson(data); System.exit(0);

        for (List<String> row : data) {
            for (String value : row) {
                System.out.print(value + " ");
            }
            System.out.println(); // Salto de línea después de imprimir cada fila
        }

        /*
         * nexj.select("*").from("users");
         * 
         * nexj.insert("table").values((NexJ, String) -> {
         * Map<String, String> columnsAndValues = new HashMap<>();
         * columnsAndValues.put("column1", "value1");
         * columnsAndValues.put("column2", "value2");
         * return columnsAndValues;
         * })
         * 
         * nexj.update('table').values([
         * 
         * ]).where('id', '=', '1')
         */

    }
}
