package services;

import java.io.*;
import java.util.Arrays;

public class FileIO {

    public static void writeToFile(String file, String message){
        try{
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(message);
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
            Utils.createLog(e.getMessage() + "\n Cause:->> "
                    + e.getCause() + "\n Stack:->> " + Arrays.toString(e.getStackTrace()));
        }
    }

    public static boolean createFile(String path){
        boolean isCreated = true;
        try {
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
        }catch (IOException e){
            isCreated = false;
            e.printStackTrace();
            Utils.createLog(e.getMessage() + "\n Cause:->> "
                    + e.getCause() + "\n Stack:->> " + Arrays.toString(e.getStackTrace()));
        }
        return isCreated;
    }

    public static String readFile(String path){
        String message = "";
        try {
            FileReader reader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;

            while ((line = bufferedReader.readLine())  != null){
                message += line;
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
            Utils.createLog(e.getMessage() + "\n Cause:->> "
                    + e.getCause() + "\n Stack:->> " + Arrays.toString(e.getStackTrace()));
        }
        return message;
    }

    public static boolean deleteFile(String path){
        File file = new File(path);
        return file.delete();
    }
}
