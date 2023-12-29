package App;

import java.util.Arrays;

import javax.xml.validation.Schema;

import core.db.Table; 

public class Main {
    public static void main(String[] args) {
       Schema mySchema = new Schema();

        Table usersTable = mySchema.addTable("users");

        // Configurar columnas para la tabla users
        /* el size en verdad sera infinito, solamente si se define esa regla se vera */
        usersTable.addColumn("id", Arrays.asList("int:39", "notnull", "auto_increment", "primary_key"));
        usersTable.addColumn("username", Arrays.asList("string:30", "notnull"));
        usersTable.addColumn("email", Arrays.asList("string", "notnull"));

        Table pruductTable = mySchema.addTable("products");
        pruductTable.addColumn("id", Arrays.asList("int"));

        // Guardar el esquema como un archivo JSON
        mySchema.save();
    }
}
