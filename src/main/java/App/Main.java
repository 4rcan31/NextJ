package App;


import core.db.datamanagement.Nexj;





public class Main {
    public static void main(String[] args) {
        Migrations.run();

        Nexj nexj = new Nexj("dbtest");
    

        
        nexj.select("table").from("user").run().toJson(); 
        /* nexj.select("*").from("users");

        nexj.insert("table").values((NexJ, String) -> {
            Map<String, String> columnsAndValues = new HashMap<>();
            columnsAndValues.put("column1", "value1");
            columnsAndValues.put("column2", "value2");
            return columnsAndValues;
        })

        nexj.update('table').values([

        ]).where('id', '=', '1') */


    }
}
