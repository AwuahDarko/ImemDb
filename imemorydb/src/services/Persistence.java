package services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Persistence {

    public  void saveData(String key, String value)throws IOException {
        String sql = "INSERT into `persistence` (`key`, `value`) values(?, ?)";

        try (Connection connection =  Utils.connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);

             preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Utils.printSQLException(e);
        }
    }


    public HashMap<String, String> fetchData()throws IOException{
        String sql = "SELECT * from `persistence`; ";

        HashMap<String, String> map = new HashMap<>();

        try (Connection connection =  Utils.connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
               map.put(rs.getString("key"), rs.getString("value"));
            }

        } catch (SQLException e) {
            Utils.printSQLException(e);
        }
        return map;
    }

    public  void createTable() throws IOException{
        String sql =
                "CREATE TABLE IF NOT EXISTS `persistence` (" +
                "  `key` VARCHAR(255) PRIMARY KEY NOT NULL, " +
                "  `value` TEXT NOT NULL, " +
                "  `created_at` DATETIME NULL DEFAULT current_timestamp" +
                "  );";

        try (Connection connection =  Utils.connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Utils.printSQLException(e);
        }
    }

    public  boolean isKeyPresent(String key) throws IOException{
        String sql = "SELECT `key` from `persistence` where `key` = ?";

        boolean exist = false;
        try (Connection connection =  Utils.connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, key);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
               exist = true;
            }

        } catch (SQLException e) {
            Utils.printSQLException(e);
        }
        return exist;
    }

    public void updateValue(String key, String value) throws IOException{
        String sql = "UPDATE  `persistence` set `value` = ? where `key` = ?";

        try (Connection connection =  Utils.connectToDatabase();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, key);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            Utils.printSQLException(e);
        }
    }

}
