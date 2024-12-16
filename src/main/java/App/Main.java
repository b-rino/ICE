package App;

import Model.*;
import Client.*;

import Client.MediaClient;
//TODO: Husk modeller før aflevering



public class Main {
    public static void main(String[] args) {

        UserClient uc = new UserClient(null);
        User loggedInUser = uc.loginMenu();
        MediaClient mediaClient = new MediaClient(loggedInUser);
        mediaClient.displayMenu();

    }
}