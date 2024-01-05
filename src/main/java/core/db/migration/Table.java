package core.db.migration;

import java.util.ArrayList;
import java.util.HashMap;
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
        this.table = new HashMap<>();
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


    public Table addColumn(String name, List<String> config ){
        this.table.put(name, config);
        return this;
    }
}
