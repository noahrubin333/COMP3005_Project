import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static boolean login;
    static String username;
    static String address;
    public static void main(String[] args){
        Database database = new Database();


        System.out.print("Welcome to the bookstore!\n");
        System.out.println("Login to an existing account (1), create new account (2) or continue as guest (3) ?");
        Scanner sc = new Scanner(System.in);

        // Login Logic
        login(database);

        int choice = 0;
        System.out.println("\nWelcome, " + username + "!");

        // Main Loop
        while(choice != -1){  // Main loop
            System.out.println("What would you like to do?\n[1 - Browse and Shop, 2 - View Cart, -1 - Exit]");
            choice = Integer.parseInt(sc.nextLine());

            if(choice == 1){
                searchAndAdd(database);
            }
            else if(choice == 2){
                boolean notEmpty = viewCart(database);

                if(notEmpty) {
                    System.out.println("Checkout (1) ?");
                    choice = Integer.parseInt(sc.nextLine());
                    if (choice == 1) {
                        database.checkout(username);
                    }
                }
            }

        }

        database.closeUpShop();
    }

    public static boolean viewCart(Database database){
        try {
            ResultSet r = database.getCartContents(username);
            ResultSetMetaData rMD = r.getMetaData();
            int count = 0;
            while(r.next()){
                count++;
                System.out.print(count + "\t| ");
                for ( int i = 1; i <= rMD.getColumnCount(); i++) {
                    System.out.print(r.getString(i) + "\t ");
                }
                System.out.println();
            }
            if (count == 0) {
                System.out.println("Cart is empty");
                return false;
            }
        }
        catch (Exception e){
            System.out.println("Search failed with exception " + e);
            return false;
        }
        return true;
    }

    public static void searchAndAdd(Database database){
        Scanner sc = new Scanner(System.in);
        System.out.println("How would you like to search? [1 - Title, 2 - Author, 3 - Genre, 4 - ISBN]");
        int searchBy = Integer.parseInt(sc.nextLine());
        switch (searchBy){
            case 1: System.out.println("Enter Title to search: "); break;
            case 2: System.out.println("Enter Author's name to search: "); break;
            case 3: System.out.println("Enter Genre to search: "); break;
            //case 4: System.out.println("Enter ISBN"); break;
        }
        String keyword = sc.nextLine();
        try {
            ResultSet r = database.Search(searchBy, keyword);
            ResultSetMetaData rMD = r.getMetaData();
            int count = 0;
            while(r.next()){
                count++;
                System.out.print(count + "\t| ");
                for ( int i = 1; i <= rMD.getColumnCount(); i++) {
                    System.out.print(r.getString(i) + "\t ");
                }
                System.out.println();
            }
            if (count == 0) System.out.println("NO RESULTS");
        }
        catch (Exception e){
            System.out.println("Search failed with exception " + e);
        }

        if(login){
            System.out.println("Enter a comma sepparated list of ISBNs to add to your cart");

            String in = sc.nextLine();

            String[] res = in.split(",");
            for (String re : res) {
                database.insertIntoCart(Integer.parseInt(re), username);
            }
        }
        else{
            System.out.println("You need to be logged in to add items to your cart");
        }
    }

    public static void login(Database database){
        Scanner sc = new Scanner(System.in);
        String password = "";
        int loginOrNew = Integer.parseInt(sc.nextLine());
        while(loginOrNew != 1 && loginOrNew != 2 && loginOrNew != 3){
            System.out.println("Invalid choice, try again");
            loginOrNew = Integer.parseInt(sc.nextLine());
        }
        if(loginOrNew == 1){
            System.out.println("username: ");
            username = sc.nextLine();

            System.out.print("password: ");
            password = sc.nextLine();

            // Loop while not logged in
            while(!database.login(username, password)){
                System.out.print("Sorry, I can't find your credentials, please try again\nusername:");
                username = sc.nextLine();
                System.out.print("password: ");
                password = sc.nextLine();
            }
            login = true;
        }
        else if (loginOrNew == 2){
            System.out.println("username: ");
            username = sc.nextLine();

            System.out.print("password: ");
            password = sc.nextLine();


            System.out.println("address?");
            address = sc.nextLine();

            while(!database.addUser(username, password, address)){
                System.out.print("\nusername:");
                username = sc.nextLine();
                System.out.print("password: ");
                password = sc.nextLine();
            }
            login = true;
        }
    }

    public static void checkout(){
        // Create new order id
        // Add all ISBNs in basket to the order
        // Remove all ISBNs from cart
        // Adjust stock in warehouse
        // Add money to publisher account
    }
}
