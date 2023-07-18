package data;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQL {
    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");

    public static void clearDB() {
        val cleanCreditRequest = "DELETE FROM credit_request_entity";
        val cleanOrder = "DELETE FROM order_entity";
        val cleanPayment = "DELETE FROM payment_entity";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(
                url, user, password)
        ) {
            runner.update(conn, cleanCreditRequest);
            runner.update(conn, cleanOrder);
            runner.update(conn, cleanPayment);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


    public static String getPaymentStatus() {
        val codesSQL = "SELECT status FROM payment_entity;";
        return getData(codesSQL);
    }

    public static String getCreditRequestStatus() {
        val codesSQL = "SELECT status FROM credit_request_entity;";
        return getData(codesSQL);
    }

      private static String getData(String query) {
        String data = "";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(
                url, user, password)
        ) {
            data = runner.query(conn, query, new ScalarHandler<>());
            return data;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
}
