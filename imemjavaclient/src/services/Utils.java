package services;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

public class Utils {

    private static HttpURLConnection con;

    public static Connection connectToDatabase() throws SQLException {

        Connection connection = null;

        try (FileInputStream f = new FileInputStream("3ix57J89Ej.properties")) {
            // load the properties file
            Properties pros = new Properties();
            pros.load(f);

            // assign db parameters
            String url = pros.getProperty("ryu");
            String user = pros.getProperty("zangief");
            String password = pros.getProperty("bison");

            // create a connection to the database
            connection = DriverManager.getConnection(url, user, password);
        } catch (IOException e) {
            Utils.createLog(e.getMessage() + "\n Cause:->> " + e.getCause());
        }
        return connection;
    }

    public static String randomName() {
        int min = 100000;
        int max = 999999;
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return "" + random_int;
    }


    public static void printSQLException(SQLException ex) throws IOException {
        StringBuilder message = new StringBuilder();
        StringBuilder cause = new StringBuilder();
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                message.append(e.getMessage()).append("\n");
                Throwable t = ex.getCause();
                while (t != null) {
                    t = t.getCause();
                    cause.append(t).append("\n");
                }
            }
        }

        Utils.createLog(message + "\n Cause:->> " + cause + "\n Stack:->> " + Arrays.toString(ex.getStackTrace()));
    }

    public static int getEar() {
        int ear = 0;
        try (FileInputStream f = new FileInputStream("3ix57J89Ej.properties")) {
            // load the properties file
            Properties pros = new Properties();
            pros.load(f);

            ear = Integer.parseInt(pros.getProperty("ears"));

        } catch (IOException e) {
            Utils.createLog(e.getMessage() + "\n Cause:->> " + e.getCause());
        }
        return ear;
    }


    public static void createLogDir() {
        try {
            String dir = "./logs";
//
//            if (Platform.getOS() == Platform.OS.WINDOWS) {
//                dir = System.getProperty("user.home") + File.separatorChar +
//                        "Documents" + File.separatorChar + "PresetAdmin" + File.separatorChar + "DBServer" + File.separatorChar + "logs";
//            } else {
//                dir = "./PresetAdmin/DBServer/logs"; // during debugging
//            }

            File theDir = new File(dir);
            if (!theDir.exists()) {
                boolean created = theDir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createUploadsDir() {
        try {
            String dir;

            if (Platform.getOS() == Platform.OS.WINDOWS) {
                dir = System.getProperty("user.home") + File.separatorChar +
                        "Documents" + File.separatorChar + "PresetAdmin" + File.separatorChar + "DBServer" + File.separatorChar + "uploads";
            } else {
                dir = "./PresetAdmin/DBServer/uploads"; // during debugging
            }

            File theDir = new File(dir);
            if (!theDir.exists()) {
                boolean created = theDir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLogDir() {
        String dir = "./logs";

//        if (Platform.getOS() == Platform.OS.WINDOWS) {
//            dir = System.getProperty("user.home") + File.separatorChar +
//                    "Documents" + File.separatorChar + "PresetAdmin" + File.separatorChar + "DBServer" + File.separatorChar + "logs" + File.separatorChar;
//        } else {
//            dir = "./PresetAdmin/DBServer/logs/"; // during debugging
//        }
        return dir;
    }

    public static boolean createAppDir() {
        boolean created = false;
        try {
            String dir;

            if (Platform.getOS() == Platform.OS.WINDOWS) {
                dir = System.getProperty("user.home") + File.separatorChar +
                        "Documents" + File.separatorChar + "PresetAdmin" + File.separatorChar + "DBServer";
            } else {
                dir = "./PresetAdmin" + File.separatorChar + "DBServer"; // during debugging
            }

            File theDir = new File(dir);
            if (!theDir.exists()) {
                created = theDir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return created;
    }

    public static String getAppDir() {
        String dir;

        if (Platform.getOS() == Platform.OS.WINDOWS) {
            dir = System.getProperty("user.home") + File.separatorChar +
                    "Documents" + File.separatorChar + "PresetAdmin" + File.separatorChar + "DBServer" + File.separatorChar;
        } else {
            dir = "./PresetAdmin/DBServer/"; // during debugging
        }
        return dir;
    }

    public static void createResourceDir() {
        try {
            String dir;

            if (Platform.getOS() == Platform.OS.WINDOWS) {
                dir = System.getProperty("user.home") + File.separatorChar +
                        "Documents" + File.separatorChar + "PresetAdmin" + File.separatorChar + "DBServer" + File.separatorChar + "resources";
            } else {
                dir = "./PresetAdmin/DBServer/resources"; // during debugging
            }

            File theDir = new File(dir);
            if (!theDir.exists()) {
                boolean created = theDir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getResourceDir() {
        String dir = "./";
//        if (Platform.getOS() == Platform.OS.WINDOWS) {
//            dir = System.getProperty("user.home") + File.separatorChar +
//                    "Documents" + File.separatorChar + "PresetAdmin" + File.separatorChar + "DBServer" + File.separatorChar + "resources" + File.separatorChar;
//        } else {
//            dir = "./PresetAdmin/DBServer/resources/"; // during debugging
//        }
        return dir;
    }

    public static String createLog(String message) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd__HH-mm-ss");
        String today = dtf.format(now);

        String dir = Utils.getLogDir();
        try {
            FileWriter myWriter = new FileWriter(dir + today + ".txt");
            myWriter.write(message);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir + today + ".txt";
    }

    public static String createLog(Exception e) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd__HH-mm-ss");
        String today = dtf.format(now);

        String dir = Utils.getLogDir();

        String message = e.getMessage() + "\n Cause:->> " +
                e.getCause() + "\n Stack:->> " + Arrays.toString(e.getStackTrace());
        try {
            FileWriter myWriter = new FileWriter(dir + today + ".txt");
            myWriter.write(message);
            myWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dir + today + ".txt";
    }

    public static String convertFileToBase64(File file) throws IllegalStateException, IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static boolean convertBase64ToFile(String base64, String path, String name) {
        boolean success = true;
        try {
            String actual = base64;
            if (base64.contains("base64,")) {
                actual = base64.split(",")[1];
            }
            byte[] decodedImg = Base64.getDecoder().decode(actual.getBytes(StandardCharsets.UTF_8));
            Path destinationFile = Paths.get(path, name);
            Files.write(destinationFile, decodedImg);
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }





    public static boolean isInternetConnected() {
        boolean available = true;
        try {
            URL url = new URL("https://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            connection.getInputStream().close();

        } catch (Exception e) {
//            Utils.createLog(e.getMessage() + "\nCause:->> " + e.getCause() + "\n Stack:->> " + Arrays.toString(e.getStackTrace()));
            available = false;
        }

        return available;
    }




    public static boolean validatePassword(String incomingPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(incomingPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);

    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }


    public static boolean isEmailValid(String email) {
        String regex = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static void writeToPropertyFile(String key, String value){
        try {
            Properties prop = new Properties();
            InputStream in = Utils.class.getResourceAsStream("3ix57J89Ej.properties");
            prop.load(in);
            prop.setProperty(key, value);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.createLog(e);
        }
    }

   public static void saveProperty(Properties p) {
        try {
            FileOutputStream fr = new FileOutputStream("xz8123uHGo4.properties");
            p.store(fr, "hold server paused state");
            fr.close();
            System.out.println("After saving properties: " + p);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.createLog(e);
        }
    }

   public static String getProperty(String key) {
        String result="";
        try {
            Properties p= new Properties();
            FileInputStream fi=new FileInputStream("xz8123uHGo4.properties");
            p.load(fi);
           result = p.getProperty(key);
            fi.close();
            System.out.println("After Loading properties: " + p);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.createLog(e);
        }
        return result;
    }

}
