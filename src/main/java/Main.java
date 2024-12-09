import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Todo: tilføj stien til din .db fil
        var url = "jdbc:sqlite:Blogbuster.db";
        DBConnector dbConnector = new DBConnector();
        UserClient uc = new UserClient();

        User loggedInUser = uc.loginMenu();

        MediaClient mediaClient = new MediaClient(loggedInUser);
        mediaClient.displayMenu();

        //
        /*ArrayList<String> data = dbConnector.selectUsers();
        printData(data);
        System.out.println("------------------");

        dbConnector.createUser();
        printData(data);*/
    }

    // Blot til test. Skal ikke være her i main.
    private static void printData(ArrayList<String> data) {
        for (String s:data
        ) {
            System.out.println(s);
        }
    }

}