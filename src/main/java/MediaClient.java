import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        cleanUpPersonalList(currentUser, 1);
        ArrayList<String> options = new ArrayList<>();
        System.out.println("MAIN MENU");

        options.add("1. Browse Media");
        options.add("2. Your Media");
        options.add("3. Account Information");
        options.add("4. Exit");

        for (int i = 0; i < options.size(); i++) {
            System.out.println(options.get(i));
        }

        int choice = ui.promptNumeric("\nPlease type the number of your choice:");

        switch (choice) {
            case 1:
                browseMedia();
                break;
            case 2:
                displayPersonalList();
                personalListActions();
                break;
            case 3:
                UserClient userClient = new UserClient(currentUser);
                userClient.displayAccount();
                break;
            case 4:
                System.out.println("Thank you for using BlogBuster");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice - please choose a number between 1 and 4");
                displayMenu();
        }
    }

    public void browseMedia() {
        List<MediaItem> mediaOptions;

        int answer = ui.promptNumeric("\nYou now have following options:\n1. Browse Movies\n2. Browse Series\n3. Browse Audiobooks");

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
                ui.displayMsg("Browsing all Audiobooks");
                mediaOptions = DBConnector.readMediaData("audiobook");
                for (int i = 0; i < mediaOptions.size(); i++) {
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                buyMedia(mediaOptions);
                break;
            default:
                ui.displayMsg("Invalid choice");
                browseMedia();
        }

    }

    public void buyMedia(List<MediaItem> mediaOptions) {
        int mediaOption = ui.promptNumeric("Please pick a media option");

        if (mediaOption > 0 && mediaOption <= mediaOptions.size()) {
            MediaItem selectedMedia = mediaOptions.get(mediaOption - 1);
            ui.displayMsg("BALANCE: " + DBConnector.getUserBalance(currentUser.getUsername()) + " AVAILABLE PUNCHES: " + DBConnector.getUserPunchcardBalance(currentUser.getUsername()) + "\n");

            String confirmation = ui.promptText("Do you want to buy \"" + selectedMedia.getTitle() + "\" for 30dkk or 1 punch? (Y/N)");
            if (confirmation.equalsIgnoreCase("N")) {
                displayMenu();
            }
            if (confirmation.equalsIgnoreCase("Y")) {
                int payMethod = ui.promptNumeric("How do you want to pay?\n1. Account Wallet\n2. Punch card\n3. Go back to main menu");
                switch (payMethod) {
                    case 1:
                        buyWithWallet(selectedMedia, mediaOption);
                        break;
                    case 2:
                        buyWithPunchcard(selectedMedia, mediaOption);
                        break;
                    case 3:
                        displayMenu();
                        break;
                    default:
                        ui.displayMsg("Invalid choice");
                        browseMedia();
                }
            }
        }
        else{
            ui.displayMsg("\nInvalid choice");
            browseMedia();
        }
    }


    public void buyWithPunchcard(MediaItem selectedMedia, int mediaOption){
        if (DBConnector.getUserPunchcardBalance(currentUser.getUsername()) > 1) {
            ui.displayMsg("You have bought " + selectedMedia.getTitle() + ". You can find your purchase in \"Your Media\"\n");
            DBConnector.updateUserPunchcard(currentUser, DBConnector.getUserPunchcardBalance(currentUser.getUsername()) - 1);
            mediaTypeSelection(selectedMedia, mediaOption);
            displayMenu();
        } else if (DBConnector.getUserPunchcardBalance(currentUser.getUsername()) == 1) {
            ui.displayMsg("You have bought " + selectedMedia.getTitle() + " with your last available punch. You can find your purchase in \"Your Media\"\n");
            DBConnector.updateUserPunchcard(currentUser, DBConnector.getUserPunchcardBalance(currentUser.getUsername()) - 1);
            DBConnector.updateUserMembership(currentUser, 0);
            mediaTypeSelection(selectedMedia, mediaOption);
            displayMenu();
        } else {
            ui.displayMsg("\nPurchase cancelled - insufficient funds\n");
            displayMenu();
        }
    }

    public void buyWithWallet(MediaItem selectedMedia, int mediaOption){
        if (DBConnector.getUserBalance(currentUser.getUsername()) >= 30) {
            ui.displayMsg("You have bought " + selectedMedia.getTitle() + ". You can find your purchase in \"Your Media\"");
            DBConnector.updateUserBalance(currentUser, 30, true);
            mediaTypeSelection(selectedMedia, mediaOption);
            displayMenu();
        } else {
            ui.displayMsg("\nPurchase cancelled - insufficient funds\n");
            displayMenu();
        }
    }

    public void mediaTypeSelection(MediaItem selectedMedia, int mediaOption) {
        if(selectedMedia instanceof Movie){
            DBConnector.addToPersonalList(currentUser, mediaOption, "movie");
        }else if(selectedMedia instanceof Series){
            DBConnector.addToPersonalList(currentUser, mediaOption, "series");
        } else if (selectedMedia instanceof Audiobooks)
        DBConnector.addToPersonalList(currentUser, mediaOption, "audiobook");{
        }
    }
    public void displayPersonalList(){
        List<MediaItem> personalList = DBConnector.getPersonalList(currentUser);
        ui.displayMsg("\nYour available content\n");
        int counter = personalList.size()+1;

        if(personalList.size() > 0) {
        for (int i = 0; i < personalList.size(); i++) {
            MediaItem item = personalList.get(i); // Get the current media item
            String type = null;
            if (item instanceof Movie) {
                type = DBConnector.getType("movie");
            }
            else if (item instanceof Series) {
                type = DBConnector.getType("series");
            }
            else if (item instanceof Audiobooks) {
                type = DBConnector.getType("audiobook");
            }
            System.out.print((i + 1) + ". " + type + " - " + item + "\n");
        }
            System.out.println(counter + ". Main Menu");
            System.out.println("");
        }
        else{
            ui.displayMsg("You currently have no content on your personal list\n");
            displayMenu();
        }
    }


    public void personalListActions(){
        List<MediaItem> personalList = DBConnector.getPersonalList(currentUser);
        int answer = ui.promptNumeric("Please choose the number of the content you want to access");
        int counter = personalList.size()+1;
        if (counter == answer) {
            displayMenu();
        }
        if(answer > 0 && answer <= personalList.size()){
            MediaItem selectedMedia = personalList.get(answer -1);
            String choice = ui.promptText("Would you like to check out " + selectedMedia.getTitle() + "? (Y/N)");
            if (choice.equalsIgnoreCase("Y")) {
                ui.displayMsg("\n" + selectedMedia.getTitle() + " is now active. Enjoy!\n");
                displayMenu();
            }
            else if (choice.equalsIgnoreCase("N")) {
                displayMenu();
            }
        }
        else{
            ui.displayMsg("Invalid option");
            displayMenu();
        }
    }
    public void cleanUpPersonalList(User user, int timeLimit) {
        long currentTime = System.currentTimeMillis() / 1000L;
        long timeLimitInSeconds;
        if(DBConnector.getUserMembership(user.getUsername()) == 1){
            timeLimitInSeconds = timeLimit * 60 * 2;
        } else{
            timeLimitInSeconds = timeLimit * 60;
        }

        String sql = "DELETE FROM PersonalMediaLists WHERE added_timestamp < ?";

        try (Connection conn = DBConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, currentTime - timeLimitInSeconds);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}