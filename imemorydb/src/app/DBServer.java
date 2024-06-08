package app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Request;
import services.JSON;
import services.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.*;
import java.util.*;

public class DBServer {

    private ServerSocket serverSocket;

    private final Timer serverTimer = new Timer();
    private final Map<String, String> database = new HashMap<>();


    public void initialize() {
        System.out.println((System.getProperty("os.arch") + "/" + System.getProperty("os.name") + "-->" + getIpAddress()));

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.setDaemon(true); //terminate the thread when program end
        socketServerThread.start();

    }



    private class SocketServerThread extends Thread {

        final int SocketServerPORT = Utils.getEar();
        int count = 0;

        @Override
        public void run() {
            try {
                Socket socket = null;

                serverSocket = new ServerSocket(SocketServerPORT);

                System.out.println("Ear on: " + serverSocket.getLocalPort());

                while (true) {
                    socket = serverSocket.accept();
                    count++;
                    //Start another thread
                    //to prevent blocked by empty dataInputStream
                    Thread acceptedThread = new Thread(new ServerSocketAcceptedThread(socket, count));
                    acceptedThread.setDaemon(true); //terminate the thread when program end
                    acceptedThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Utils.createLog(e);
            }

        }
    }

    private class ServerSocketAcceptedThread extends Thread {

        Socket socket = null;
        private PrintWriter printWriter = null;
        private BufferedReader bufferedReader = null;
        int count;

        ServerSocketAcceptedThread(Socket s, int c) {
            socket = s;
            count = c;
        }

        @Override
        public void run() {
            try {

                printWriter = new PrintWriter(socket.getOutputStream(), true);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String messageFromClient = bufferedReader.readLine();

                // validate message
                if (!messageFromClient.contains("-ITaTI-")) {
                    HashMap<String, String> resMap = new HashMap<>();
                    resMap.put("message", "Unauthorized access");
                    resMap.put("status", "401");
                    printWriter.println(JSON.convertMapToJson(resMap));
                    return;
                }

                String[] messagePair = messageFromClient.split("-ITaTI-");

                if (messagePair.length > 2) {
                    HashMap<String, String> resMap = new HashMap<>();
                    resMap.put("message", "Bad request");
                    resMap.put("status", "400");
                    printWriter.println(JSON.convertMapToJson(resMap));
                    return;
                }

                String route = messagePair[0];
                String body = messagePair[1];

                switch (route) {
                    case "/put" -> {
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Request>() {
                            }.getType();
                            Request request = gson.fromJson(body, type);
                            HashMap<String, String> resMap = new HashMap<>();

                            if (request == null) {
                                resMap.put("message", "Invalid request");
                                resMap.put("status", "400");
                                sendResponse(JSON.convertMapToJson(resMap));
                                break;
                            }

                            if (request.getKey().isEmpty()) {

                                resMap.put("message", "Invalid key");
                                resMap.put("status", "400");
                                sendResponse(JSON.convertMapToJson(resMap));
                                break;
                            }


                            database.put(request.getKey(), request.getValue());
                            resMap.put("message", "success");
                            resMap.put("status", "200");
                            sendResponse(JSON.convertMapToJson(resMap));

                        } catch (Exception e) {
                            e.printStackTrace();
                            HashMap<String, String> resMap = new HashMap<>();
                            resMap.put("message", e.getMessage());
                            resMap.put("status", "500");

                            Utils.createLog(e);

                            sendResponse(JSON.convertMapToJson(resMap));
                        }
                    }
                    case "/get" -> {
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Request>() {
                            }.getType();
                            Request request = gson.fromJson(body, type);

                            HashMap<String, String> resMap = new HashMap<>();

                            if (request == null) {
                                resMap.put("message", "Invalid request");
                                resMap.put("status", "400");
                                sendResponse(JSON.convertMapToJson(resMap));
                                break;
                            }

                            if (request.getKey().isEmpty()) {
                                resMap.put("message", "Invalid key");
                                resMap.put("status", "400");
                                sendResponse(JSON.convertMapToJson(resMap));
                                break;
                            }

                            // Send response.

                            resMap.put("message", "success");
                            resMap.put("data", database.get(request.getKey()));
                            resMap.put("status", "200");
                            printWriter.println(JSON.convertMapToJson(resMap));


                        } catch (Exception e) {
                            e.printStackTrace();
                            HashMap<String, String> resMap = new HashMap<>();
                            resMap.put("message", e.getMessage());
                            resMap.put("status", "500");

                            Utils.createLog(e);

                            printWriter.println(JSON.convertMapToJson(resMap));
                        }
                    }
                    default -> {
                        HashMap<String, String> resMap = new HashMap<>();
                        resMap.put("message", "Not Found");
                        resMap.put("status", "404");
                        printWriter.println(JSON.convertMapToJson(resMap));
                    }
                }


                System.out.println("Request count --> "+ count);



            } catch (IOException e) {
                e.printStackTrace();
                Utils.createLog(e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.createLog(e);
                    }
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.createLog(e);
                    }
                }

                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }

        private void sendResponse(String message){
            try{
                printWriter.println(message);
            }catch (Exception e){
                e.printStackTrace();
                Utils.createLog(e);
            }
        }
    }


    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Utils.createLog(e);
        }
        return ip;
    }
}
