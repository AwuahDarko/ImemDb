package app;

import services.FileIO;
import services.Utils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MessagingClient implements Runnable {
    String dstAddress;
    int dstPort;
    public String response = "";
    String msgToServer;
    private SocketResponseReceiver responseReceiver;


    public static void sendMessageToServerAsync(String ip, int port, String message, ArrayList<String> files, SocketResponseReceiver listener) {
        MessagingClient messagingClient = new MessagingClient(ip, port, message);

        messagingClient.setSocketResponseReceiver(listener);
        new Thread(messagingClient).start();
    }

    public static String sendMessageToServerSync(String ip, int port, String message, ArrayList<String> files){
        MessagingClient messagingClient = new MessagingClient(ip, port, message);
        return messagingClient.performSynchronousSend();
    }

    public String performSynchronousSend(){
        String response = "";

        Socket socket = null;

        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        try {
            socket = new Socket(dstAddress, dstPort);

             printWriter = new PrintWriter(socket.getOutputStream(), true);
             bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            printWriter.println(msgToServer);
            System.out.println(response);
            response = bufferedReader.readLine();

        } catch (IOException e) {
            response = e.getMessage();

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
        return response;
    }



    public interface SocketResponseReceiver {
        void onResponseReceived(String response);

        void onError(String error);
    }



    public MessagingClient(String ip, int port, String msgTo) {
        dstAddress = ip;
        dstPort = port;
        msgToServer = msgTo;
    }

    public void setSocketResponseReceiver(SocketResponseReceiver listener) {
        this.responseReceiver = listener;
    }

    @Override
    public void run() {
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            if(msgToServer != null){
                dataOutputStream.writeUTF(msgToServer);
            }

            response = dataInputStream.readUTF();

        } catch (IOException e) {
            responseReceiver.onError(e.getMessage());

            e.printStackTrace();
            Utils.createLog(e);
        } finally {

            responseReceiver.onResponseReceived(response);

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.createLog(e);
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.createLog(e);
                }
            }

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Utils.createLog(e);
                }
            }

        }
    }
}
