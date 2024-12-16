import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//TODO: Husk modeller før aflevering



public class Main {
    public static void main(String[] args) {

        UserClient uc = new UserClient(null);
        User loggedInUser = uc.loginMenu();
        MediaClient mediaClient = new MediaClient(loggedInUser);
        mediaClient.displayMenu();

    }
}