import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Database {
    private static Connection connection;
    private static Statement statement;

    public Database(){
        try{
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BOOKSTORE", "postgres", "9632");
            statement = connection.createStatement();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public ResultSet Search(int searchBy, String keyword) {
        String SEARCH = "SELECT * FROM BOOKS WHERE ";
        switch (searchBy){
            case 1:
                SEARCH += "TITLE";
                SEARCH += " LIKE '%"  + keyword + "%'";
                break;
            case 2:
                SEARCH += "AUTHOR";
                SEARCH += " LIKE '%"  + keyword + "%'";
                break;
            case 3:
                SEARCH += "GENRE";
                SEARCH += " LIKE '%"  + keyword + "%'";
                break;
        }
        try {
            ResultSet res = statement.executeQuery(SEARCH);
            return res;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }


    public boolean login(String username, String password){
        try {
            ResultSet res = statement.executeQuery("SELECT PASSWORD FROM USERS WHERE USERNAME = '" + username + "'");
            if(res.next()) {
                if (password.equals(res.getString(1))) {
                    return true;
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return false;
    }

    public boolean addUser(String username, String password, String address){
        try {
            ResultSet res = statement.executeQuery("SELECT USERNAME FROM USERS WHERE USERNAME = '" + username + "'");
            if(res.next()) {
                System.out.println("Sorry, that username is taken");
                return false;
            }
            else{
                statement.execute("INSERT INTO USERS VALUES ('" + username + "', '" + password + ", " + address + "')");
                statement.execute("INSERT INTO CART_OF VALUES ('" + username + "')");
                //connection.commit();
                return true;
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return false;
    }

    public void insertIntoCart(int toAdd, String username){
        try{
            ResultSet res = statement.executeQuery("INSERT INTO CART VALUES ((SELECT CART_ID FROM CART_OF WHERE USER_ID LIKE '" + username + "'), " + Integer.toString(toAdd) + ")");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean checkout(String username){
        try{
            ResultSet toShip = statement.executeQuery("SELECT ISBN FROM CART WHERE CART_ID = (SELECT CART_ID FROM CART_OF WHERE USER_ID LIKE '" + username + "')");
            ResultSetMetaData rMD = toShip.getMetaData();
            Queue<String> ISBNs = new LinkedList<>();
            while(toShip.next()){
                ISBNs.add(toShip.getString(1));
            }
            statement.executeUpdate("INSERT INTO ORDER_OF(USERNAME) VALUES('" + username + "')");

            while(!ISBNs.isEmpty()){
                String currISBN = ISBNs.remove();
                statement.executeUpdate("INSERT INTO ORDERS VALUES ((SELECT ORDER_NO FROM ORDER_OF WHERE USERNAME LIKE '" + username + "' ORDER BY ORDER_NO DESC LIMIT 1), " + currISBN + ")");
                statement.executeUpdate("DELETE FROM CART WHERE CART_ID = (SELECT CART_ID FROM CART_OF WHERE USER_ID LIKE '" + username + "' AND ISBN = " + currISBN + ")");
            }

        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public ResultSet getCartContents (String username){
        try {
            ResultSet res = statement.executeQuery("SELECT * FROM BOOKS WHERE ISBN IN (SELECT ISBN FROM CART WHERE CART_ID = (SELECT CART_ID FROM CART_OF WHERE USER_ID LIKE '" + username + "'))");
            return res;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public void closeUpShop() {
        try {
            connection.close();
        }
        catch (Exception e){ }
    }
}
