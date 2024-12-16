package App;

import Client.*;
import Model.User;

public class BlogBuster {

    public static void setup(){
        UserClient uc = new UserClient(null);
        User loggedInUser = uc.loginMenu();
        MediaClient mediaClient = new MediaClient(loggedInUser);
        mediaClient.displayMenu();
    }
}


