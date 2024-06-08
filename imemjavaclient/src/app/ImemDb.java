package app;

import com.google.gson.Gson;
import services.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImemDb {

    private static String ip = "192.168.245.195";
    private static int port = 5346;
    private  static ImemDataReceiver dataReceiver;
    private  static ImemDataResponder dataResponder;
    public static void setDataReceiver(ImemDataReceiver listener) {
        dataReceiver = listener;
    }

    public static void setDataResponder(ImemDataResponder listener) {
        dataResponder = listener;
    }
    public static boolean put(String key, String value){
        HashMap<String, String>req = new HashMap<>();
        req.put("key", key);
        req.put("value", value);
        Gson gson = new Gson();
        String json = gson.toJson(req);
        String msg = "/put" +"-ITaTI-"+json+"\r\n";

        String response = MessagingClient.sendMessageToServerSync(ip,
                port, msg, new ArrayList<>());

        if(response == null) return false;

        Map<String, String> map = JSON.convertJsonToMap(response);
        return map.get("status").equals("200");
    }

    public static String get(String key){
        HashMap<String, String>req = new HashMap<>();
        req.put("key", key);
        req.put("value", "");
        Gson gson = new Gson();
        String json = gson.toJson(req);
        String msg = "/get" +"-ITaTI-"+json+"\r\n";

        String  response = MessagingClient.sendMessageToServerSync(ip,
                port, msg, new ArrayList<>());

        if(response == null) return null;

        Map<String, String> map = JSON.convertJsonToMap(response);

        if(map.get("status").equals("200")){
            return map.get("data");
        }

        return  null;
    }

    public static void getAsync(String key, ImemDataReceiver receiver){
//        ImemDb imemDb = new ImemDb();
        setDataReceiver(receiver);
        HashMap<String, String>req = new HashMap<>();
        req.put("key", key);
        req.put("value", "");
        Gson gson = new Gson();
        String json = gson.toJson(req);
       String msg = "/get" +"-ITaTI-"+json;

        MessagingClient.sendMessageToServerAsync("192.168.245.195",
                5346, msg, new ArrayList<>(),
                new MessagingClient.SocketResponseReceiver() {
                    @Override
                    public void onResponseReceived(String response) {
                        dataReceiver.sendData(response);
                    }

                    @Override
                    public void onError(String error) {
                        dataReceiver.sendData(null);
                    }
                });
    }

    public static void putAsync(String key, String value, ImemDataResponder responder){
        setDataResponder(responder);

        HashMap<String, String>req = new HashMap<>();
        req.put("key", key);
        req.put("value", value);
        Gson gson = new Gson();
        String json = gson.toJson(req);
        String msg = "/put" +"-ITaTI-"+json;

        MessagingClient.sendMessageToServerAsync("192.168.245.195",
                5346, msg, new ArrayList<>(),
                new MessagingClient.SocketResponseReceiver() {
                    @Override
                    public void onResponseReceived(String response) {
                        dataResponder.sendData(true);
                    }

                    @Override
                    public void onError(String error) {
                        dataResponder.sendData(false);
                    }
                });
    }


    public interface ImemDataReceiver {
        void sendData(String response);
    }

    public interface ImemDataResponder {
        void sendData(boolean response);
    }
}
