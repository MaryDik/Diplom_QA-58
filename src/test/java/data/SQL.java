package data;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.sql.SQLException;


public class SQL {

    public SQL() {
    }

    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");


    @SneakyThrows
    public static void clearDB() {
        val cleanCreditRequest = "DELETE FROM credit_request_entity";
        val cleanOrder = "DELETE FROM order_entity";
        val cleanPayment = "DELETE FROM payment_entity";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            runner.update(conn, cleanCreditRequest);
            runner.update(conn, cleanOrder);
            runner.update(conn, cleanPayment);
        } catch (Exception e) {
            System.out.println("SQL exception in clearDB");
        }
    }
    @SneakyThrows
    private static String getData(String query) {
        var result = " ";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(
                url, user, password)
        ) {
            result = runner.query(conn, query, new ScalarHandler<>());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    @SneakyThrows
    public static String getPaymentStatus() {
        val statusSQL = "SELECT status FROM payment_entity";
        return getData(statusSQL);
    }
    @SneakyThrows
    public static String getCreditRequestStatus() {
        val statusSQL = "SELECT status FROM credit_request_entity";
        return getData(statusSQL);
    }
    @SneakyThrows
    public static String getOrderCount() {
        String count = null;
        String statusSQL = "SELECT COUNT(*) FROM order_entity;";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            count = runner.query(conn, statusSQL, new ScalarHandler<>());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return count;
    }

}
