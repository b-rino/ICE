import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//TODO: HVIS DU FAILER LOGIN I FØRSTE FORSØG SKAL DU LOGGE IND TO GANGE EFTERFØLGENDE!!! DISPLAY FUNDS OG MEMBERSHIP STATUS
//TODO: Husk modeller før aflevering
//TODO: Restriktioner på alle informationer givet med create user

//deleteAccount og displayAccount + småting hvor programmet crashede


public class Main {
    public static void main(String[] args) {
        //Todo: tilføj stien til din .db fil
        var url = "jdbc:sqlite:Blogbuster.db";
        DBConnector dbConnector = new DBConnector();
        UserClient uc = new UserClient(null);

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