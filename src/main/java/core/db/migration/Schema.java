package core.db.migration;

public class Schema {


    public Table addTable(String tableName){
        return new Table(tableName);
    }
}
