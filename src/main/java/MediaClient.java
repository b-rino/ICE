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
        options.add("2. Your Media");
        options.add("3. Account Information");
        options.add("4. Exit");

        for (int i = 0; i < options.size(); i++) {
            System.out.println(options.get(i));
        }
        //TODO: Hvorfor scanner her, når vi har UI?
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

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
                System.out.println("Invalid choice - please choose a number between 1 and 5");
                displayMenu();
        }
    }

    public void browseMedia() {
        List<MediaItem> mediaOptions = new ArrayList<>();

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
                mediaOptions = DBConnector.readMediaData("audiobooks");
                for (int i = 0; i < mediaOptions.size(); i++) {
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                buyMedia(mediaOptions);
                break;
        }

    }

    //Der skal tilføjes så den købte film bliver lagt i en liste over "ejede medier" og måske også en mulighed for at gemme den til senere køb??
    public void buyMedia(List<MediaItem> mediaOptions) {
        int mediaOption = ui.promptNumeric("Please pick a media option");

        if (mediaOption > 0 && mediaOption <= mediaOptions.size()) {
            MediaItem selectedMedia = mediaOptions.get(mediaOption - 1);
            String confirmation = ui.promptText("Do you want to buy \"" + selectedMedia.getTitle() + "\" for 30dkk or 1 punch? (Y/N)");

            if (confirmation.equalsIgnoreCase("Y")) {
                int payMethod = ui.promptNumeric("How do you want to pay?\n1. Account Wallet\n2. Punch card\n3. Go back to main menu");
                if (payMethod == 1 && DBConnector.getUserBalance(currentUser.getUsername()) >= 30) {
                    ui.displayMsg("You have bought " + selectedMedia.getTitle() + ". You can find your purchase in \"Your Media\"");
                    DBConnector.updateUserBalance(currentUser, 30, true);
                    mediaTypeSelection(selectedMedia, mediaOption);
                    displayMenu();
                } else if (payMethod == 2 && DBConnector.getUserPunchcardBalance(currentUser.getUsername()) > 1) {
                    ui.displayMsg("You have bought " + selectedMedia.getTitle() + ". You can find your purchase in \"Your Media\"");
                    DBConnector.updateUserPunchcard(currentUser, DBConnector.getUserPunchcardBalance(currentUser.getUsername()) - 1);
                    mediaTypeSelection(selectedMedia, mediaOption);
                    displayMenu();
                }else if(payMethod == 2 && DBConnector.getUserPunchcardBalance(currentUser.getUsername()) == 1) {
                    ui.displayMsg("You have bought " + selectedMedia.getTitle() + " with your last available punch. You can find your purchase in \"Your Media\"");
                    DBConnector.updateUserPunchcard(currentUser, DBConnector.getUserPunchcardBalance(currentUser.getUsername()) - 1);
                    DBConnector.updateUserMembership(currentUser, 0);
                    mediaTypeSelection(selectedMedia, mediaOption);
                    displayMenu();
                }else if(payMethod == 3){
                    displayMenu();
                }
                else {
                    ui.displayMsg("Purchase cancelled - insufficient funds\n");
                    displayMenu();
                }
            } else {
                ui.displayMsg("Invalid option");
            }
            displayMenu();
        }
    }

    public void mediaTypeSelection(MediaItem selectedMedia, int mediaOption) {
        if(selectedMedia instanceof Movie){
            DBConnector.addToPersonalList(currentUser, mediaOption, "movie");
        }else if(selectedMedia instanceof Series){
            DBConnector.addToPersonalList(currentUser, mediaOption, "series");
        } else if (selectedMedia instanceof Audiobooks)
        DBConnector.addToPersonalList(currentUser, mediaOption, "Audiobooks");{
        }
    }
    public void displayPersonalList(){
        List<MediaItem> personalList = DBConnector.getPersonalList(currentUser);
        ui.displayMsg("\nYour available content\n");
        if(personalList.size() > 0) {

            for (int i = 0; i < personalList.size(); i++) {
                System.out.print((i + 1) + ". " + personalList.get(i) + "\n");
            }
            System.out.println("");
        }
        else{
            ui.displayMsg("You currently have no content on your personal list");
            displayMenu();
        }
    }

    public void personalListActions(){
        List<MediaItem> personalList = DBConnector.getPersonalList(currentUser);
        int answer = ui.promptNumeric("Please choose the number of the content you want to access");

        if(answer > 0 && answer <= personalList.size()){
            MediaItem selectedMedia = personalList.get(answer -1);
            String choice = ui.promptText("Do you wanna watch " + selectedMedia.getTitle() + "? (Y/N)");
            if (choice.equalsIgnoreCase("Y")) {
                ui.displayMsg("You are now watching " + selectedMedia.getTitle() + ".\n");
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
}