import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MediaClient {
    private DBConnector DBConnector = new DBConnector();
    private TextUI ui = new TextUI();
    private User currentUser;

    public MediaClient(User currentUser) {
        this.currentUser = currentUser;
    }


    public void displayMenu() {
        ArrayList<String> options = new ArrayList<>();
        System.out.println("MAIN MENU");

        options.add("1. Browse Media");
        options.add("2. See History");
        options.add("3. Display Favorites:");
        options.add("4. Account Information");
        options.add("5. Exit");

        for (int i = 0; i < options.size(); i++) {
            System.out.println(options.get(i));
        }
        //TODO: Hvorfor scanner her, når vi har UI?
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                displayMedia();
                break;
            case 2:
                System.out.println("HISTORY");
                break;
            case 3:
                System.out.println("FAVORITES");
                break;
            case 4:
                UserClient userClient = new UserClient(currentUser);
                userClient.displayAccount();
                break;
            case 5:
                System.out.println("Thank you for using BlogBuster");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice - please choose a number between 1 and 5");
                displayMenu();
        }
    }

    public void displayMedia() {
        List<MediaItem> mediaOptions = new ArrayList<>();

        int answer = ui.promptNumeric("\nYou now have following options:\n1. Browse Movies\n2. Browse Series\n3.Browse All");

        switch (answer) {
            case 1:
                ui.displayMsg("Browsing All Movies");
                mediaOptions = DBConnector.readMediaData("movie");
                for (int i = 0; i < mediaOptions.size(); i++) {
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                buyMedia(mediaOptions);
                break;
            case 2:
                ui.displayMsg("Browsing All Series");
                mediaOptions = DBConnector.readMediaData("series");
                for (int i = 0; i < mediaOptions.size(); i++) {
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                buyMedia(mediaOptions);
                break;
            case 3:
                ui.displayMsg("Browsing All Media");
                mediaOptions = DBConnector.readMediaData("combi");
                for (int i = 0; i < mediaOptions.size(); i++) {
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                buyMedia(mediaOptions);
                break;
        }

    }

    public void buyMedia(List<MediaItem> mediaOptions) {
        int mediaOption = ui.promptNumeric("Please pick a media option");

        if (mediaOption > 0 && mediaOption <= mediaOptions.size()) {
            MediaItem selectedMedia = mediaOptions.get(mediaOption - 1);
            String confirmation = ui.promptText("Do you want to buy " + selectedMedia + " for 30dkk or 1 punch? (Y/N)");

            if (confirmation.equalsIgnoreCase("Y")) {
                int payMethod = ui.promptNumeric("How do you want to pay?\n1. Account Wallet\n2. Punch card");
                if (payMethod == 1 && DBConnector.getUserBalance(currentUser.getUsername()) >= 30) {
                    ui.displayMsg("You have bought " + selectedMedia.getTitle());
                    DBConnector.updateUserBalance(currentUser, 30, true);
                    displayMenu();
                } else if (payMethod == 2 && DBConnector.getUserPunchcardBalance(currentUser.getUsername()) >= 1) {
                    ui.displayMsg("You have bought " + selectedMedia.getTitle());
                    DBConnector.updateUserPunchcard(currentUser, DBConnector.getUserPunchcardBalance(currentUser.getUsername()) -1);
                    displayMenu();
                } else {
                    ui.displayMsg("Purchase cancelled - insufficient funds");
                    displayMenu();
                }
            } else {
                ui.displayMsg("Invalid option");
            }
            displayMenu();
        }
    }
}