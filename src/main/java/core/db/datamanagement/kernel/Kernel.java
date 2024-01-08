package core.db.datamanagement.kernel;

import java.util.List;

public class Kernel {


    String pathString;
    String columnMetdata;
    public Kernel(){
        this.pathString = "src/main/java/core/db/migration/shecmas/";
        this.columnMetdata = "header-metadata-colums";
    }

    public <T> boolean allElementsEqual(List<T> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }

        T firstElement = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (!list.get(i).equals(firstElement)) {
                return false; 
            }
        }

        return true; 
    }
}
