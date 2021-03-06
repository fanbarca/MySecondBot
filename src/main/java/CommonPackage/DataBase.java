package CommonPackage;

import CurtainBotPackage.Lan;

import java.sql.Connection;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.net.URI;
import java.util.*;
import java.sql.*;

public class DataBase {
    private static final String DRIVER = "org.postgresql.Driver";


public static Connection getConnection() throws URISyntaxException, SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
            + "Include in your library path!");
            return null;
        }
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
        return DriverManager.getConnection(dbUrl, username, password);
    }
    public static String sqlQuery(String command, String field) throws SQLException {
            String lan = "";
            try {
                Connection conn = getConnection();
                if (conn!=null) {
                    Statement prst = conn.createStatement();
                    ResultSet rs = prst.executeQuery(command);
                    while (rs.next()){
                        lan= rs.getString(field);
                    }
                    prst.close();
                    conn.close();
                }
            }
            catch(Exception ex) {
                System.err.println(ex);
            }
        return lan;
    }
    public static boolean sqlQueryBoolean(String command, String field) throws SQLException {
            boolean lan = false;
            try {
                Connection conn = getConnection();
                if (conn!=null) {
                    Statement prst = conn.createStatement();
                    ResultSet rs = prst.executeQuery(command);
                    while (rs.next()){
                        lan= rs.getBoolean(field);
                    }
                    prst.close();
                    conn.close();
                }
            }
            catch(Exception ex) {
                System.err.println(ex);
            }
        return lan;
    }
    public static void sql(String command) {
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                prst.executeUpdate(command);
                prst.close();
                conn.close();
            }
            }
            catch(Exception ex) {
                System.err.println(ex);
            }
    }
    public static List<String> listMyOrders(String id, String column){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from orders where userid =" + id);
                while (rs.next()){
                    lan.add(rs.getString(column));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }
    public static List<String> showProducts(String language, String column, String type){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select * from table0 where type = '"+type+"' order by "+column+" asc");
                while (rs.next()){
                    if (rs.getBoolean("instock")) lan.add(rs.getString(column)+" - "+rs.getString("cost")+ Lan.currency(language));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }
    public static List<String> showAllProducts(String column, boolean instock){
        List<String> lan = new ArrayList<>();
        String stock = "";
        if (instock) stock = "where instock = true ";
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select * from table0 "+stock+"order by type asc, "+column+" asc");
                while (rs.next()){
                    if (!rs.getString("russian").equals("Лого")) lan.add(rs.getString(column));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }



    public static List<String> sqlGetUserData(String id) throws SQLException {
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select * from users where id ="+id);
                while (rs.next()){
                    lan.add(rs.getString("firstname"));
                    lan.add(rs.getString("phone"));
                    lan.add(rs.getString("language"));
                    lan.add(rs.getString("rmid"));
                    lan.add(rs.getString("smid"));
                    lan.add(rs.getString("image"));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }
    public static List<String> sqlIdList() throws SQLException {
            List<String> idList = new ArrayList<String>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select id from users");
                while (rs.next()) {
                    idList.add(rs.getString("id"));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return idList;
    }


    public static String sqlselect(String id, String column) throws SQLException {
        String lan = "";
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select "+column+" from users where id =" + id);
                while (rs.next()){
                    lan= rs.getString(column);
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
    return lan;
    }
    public static List<String> sqlQueryList(String command, String field) throws SQLException {
            List<String> lan = new ArrayList<>();
            try {
                Connection conn = getConnection();
                if (conn!=null) {
                    Statement prst = conn.createStatement();
                    ResultSet rs = prst.executeQuery(command);
                    while (rs.next()){
                        lan.add(rs.getString(field));
                    }
                    prst.close();
                    conn.close();
                }
            }
            catch(Exception ex) {
                System.err.println(ex);
            }
        return lan;
    }


    public static List<String> productsAvailability(String column){
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = DataBase.getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select * from table0 ORDER BY type ASC, "+column+" ASC");
                while (rs.next()){
                    String mark="";
                    if (rs.getBoolean("instock")) mark = ":white_check_mark: ";
                    else mark = ":x: ";
                    if (!rs.getString("type").equals("99")) lan.add(mark + rs.getString(column));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }




    //////////////////////////////////////////////////////////////////////////////////

    public static List<String> sqlStudentsIdList() throws SQLException {
        List<String> idList = new ArrayList<String>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select id from students");
                while (rs.next()) {
                    idList.add(rs.getString("id"));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return idList;
    }

    public static List<String> sqlGetStudentData(String id) throws SQLException {
        List<String> lan = new ArrayList<>();
        try {
            Connection conn = getConnection();
            if (conn!=null) {
                Statement prst = conn.createStatement();
                ResultSet rs = prst.executeQuery("select * from users where id ="+id);
                while (rs.next()){
                    lan.add(rs.getString("firstname"));
                    lan.add(rs.getString("phone"));
                    lan.add(rs.getString("language"));
                    lan.add(rs.getString("rmid"));
                    lan.add(rs.getString("smid"));
                    lan.add(rs.getString("image"));
                }
                prst.close();
                conn.close();
            }
        }
        catch(Exception ex) {
            System.err.println(ex);
        }
        return lan;
    }
}