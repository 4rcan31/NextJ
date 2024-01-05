package core.db.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Table {
    
    String name;
    List<String> config;
    Map<String, List<String>> table;
    /* 
     * 
     * table -> [
     *  column1 -> [config],
     *  column2 -> [config]
     * ] 
     */

    public Table(String table){
        this.name = table;
        this.config = new ArrayList<>();
        this.table = new LinkedHashMap<>();
    }

    public String getName(){
        return this.name;
    }

    public List<String> getColumns(){
         return new ArrayList<>(this.table.keySet());
    }

    public List<List<String>> getConfigColumns(){
        return new ArrayList<>(this.table.values());
    }

    public List<String> createAt(){
        return Arrays.asList("create_at", "notnull"); 
    }

    public List<String> updateAt(){
          return Arrays.asList("update_at", "notnull"); 
    }

    public List<String> id(){
          return Arrays.asList("int", "nullable", "auto_increment");
    }

    public Table addColumn(String name, List<String> config ){
        this.table.put(name, config);
        return this;
    }
}
