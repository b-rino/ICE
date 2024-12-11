import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//TODO: HVIS DU FAILER LOGIN I FØRSTE FORSØG SKAL DU LOGGE IND TO GANGE EFTERFØLGENDE!!!
//TODO: Husk modeller før aflevering
//TODO: TimeStamp fungerer, så du får double time


public class Main {
    public static void main(String[] args) {

        UserClient uc = new UserClient(null);
        User loggedInUser = uc.loginMenu();
        MediaClient mediaClient = new MediaClient(loggedInUser);
        mediaClient.displayMenu();

    }
}