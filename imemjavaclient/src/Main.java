import app.ImemDb;

public class Main {
    public static void main(String[] args) {

        System.out.println("Hello world!");

       boolean res = ImemDb.put("avg", "34566");
        ImemDb.put("age", "20");
        ImemDb.put("place", "kasoa");
        ImemDb.put("name", "Kwame");
       String data = ImemDb.get("age");
        System.out.println(data);
    }
}