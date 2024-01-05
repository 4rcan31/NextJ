package App;

import java.util.Arrays;

import core.db.migration.Schema;
import core.db.migration.Table;

public class Migrations {

    private Schema schema = new Schema("dbtest");

    public void userTable() {
        Table users = schema.addTable("user");
        users.addColumn("id", users.id());
        users.addColumn("name", Arrays.asList("string", "notnull"));
        users.addColumn("email", Arrays.asList("string", "notnull"));
        users.addColumn("password", Arrays.asList("string", "notnull"));
        users.addColumn("update_at", users.updateAt());
        users.addColumn("create_at", users.createAt());
    }

    public static void run() {
        Migrations migrations = new Migrations();
        migrations.userTable();
        migrations.schema.save();
    }

}
