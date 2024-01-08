package App;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonCreator {

    public static void main(String[] args) {
        int cantidadRegistros = 1000; // Puedes cambiar esta cantidad según lo que necesites
        
        Map<String, List<?>> jsonData = new LinkedHashMap<>();
        jsonData.put("id", generateData(cantidadRegistros, 1000, 9999));
        jsonData.put("name", generateNames(cantidadRegistros));
        jsonData.put("email", generateEmails(cantidadRegistros));
        jsonData.put("password", generatePasswords(cantidadRegistros));
        jsonData.put("update_at", generateDates(cantidadRegistros, "2023-12-01", "2023-12-05"));
        jsonData.put("create_at", generateDates(cantidadRegistros, "2023-11-01", "2023-12-05"));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(jsonData);
        System.out.println(json);
    }

    private static List<Integer> generateData(int cantidad, int min, int max) {
        List<Integer> dataList = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            int random = (int) (Math.random() * (max - min + 1) + min);
            dataList.add(random);
        }
        return dataList;
    }

    private static List<String> generateNames(int cantidad) {
        List<String> nameList = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            nameList.add("Name" + (i + 1));
        }
        return nameList;
    }

    private static List<String> generateEmails(int cantidad) {
        List<String> emailList = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            emailList.add("email" + (i + 1) + "@example.com");
        }
        return emailList;
    }

    private static List<String> generatePasswords(int cantidad) {
        List<String> passwordList = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            passwordList.add("pass" + (i + 1));
        }
        return passwordList;
    }

    private static List<String> generateDates(int cantidad, String startDate, String endDate) {
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            String date = generateRandomDate(startDate, endDate);
            dateList.add(date);
        }
        return dateList;
    }

    private static String generateRandomDate(String startDate, String endDate) {
        long startMillis = java.sql.Date.valueOf(startDate).getTime();
        long endMillis = java.sql.Date.valueOf(endDate).getTime();
        long randomMillis = startMillis + (long) (Math.random() * (endMillis - startMillis));
        return new java.sql.Date(randomMillis).toString();
    }
}
