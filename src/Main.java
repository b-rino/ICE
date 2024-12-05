import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Todo: tilføj stien til din .db fil
        var url = "jdbc:sqlite:/home/mikkel/Desktop/Code/ICE/Blogbuster.db";
        DBConnector dbConnector = new DBConnector();
        dbConnector.connect(url);
        ArrayList<String> data = dbConnector.selectUsers();
        printData(data);
        System.out.println("------------------");

        dbConnector.createUser();
        printData(data);
    }

    // Blot til test. Skal ikke være her i main.
    private static void printData(ArrayList<String> data) {
        for (String s:data
        ) {
            System.out.println(s);
        }
    }

}