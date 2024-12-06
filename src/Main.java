import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Todo: tilføj stien til din .db fil
        var url = "jdbc:sqlite:Blogbuster.db";
        DBConnector dbConnector = new DBConnector();
        //UserClient uc = new UserClient();
        //uc.createUser();
        //System.out.println(dbConnector.readUserData());
        //Movie movie = new Movie("Philip", 1993, "Smartass", 2);
        //dbConnector.saveMediaData(movie);
        List<MediaItem> list = dbConnector.readMediaData();
        for (MediaItem mediaItem : list) {
            System.out.println(mediaItem);
        }

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