import app.DBServer;
import services.Utils;

public class Main {
    public static void main(String[] args) {
        Utils.createLogDir();
        Utils.createResourceDir();
        DBServer server = new DBServer();
        server.initialize();
        System.out.println("Hello world!");
    }
}