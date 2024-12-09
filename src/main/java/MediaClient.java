import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MediaClient {
    private DBConnector DBConnector = new DBConnector();
    private TextUI ui = new TextUI();
    private User currentUser;

    public MediaClient(User currentUser){
        this.currentUser = currentUser;
    }


    public void displayMenu(){
        ArrayList<String> options = new ArrayList<>();
        System.out.println("MAIN MENU");

        options.add("1. Browse Media");
        options.add("2. See History");
        options.add("3. Display Favorites:");
        options.add("4. Account Information");
        options.add("5. Exit");

        for (int i = 0; i < options.size(); i++){
            System.out.println(options.get(i));
        }
        //TODO: Hvorfor scanner her, når vi har UI?
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice){
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
                displayMenu();        }
    }

    public void displayMedia(){
        List<MediaItem> mediaOptions = new ArrayList<>();

        int answer = ui.promptNumeric("\nYou now have following options:\n1. Browse Movies\n2. Browse Series\n3.Browse All");

        switch (answer){
            case 1:
                ui.displayMsg("Browsing All Movies");
                mediaOptions = DBConnector.readMediaData("movie");
                for(int i = 0; i < mediaOptions.size(); i++){
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                displayMenu();
                break;
            case 2:
                ui.displayMsg("Browsing All Series");
                mediaOptions = DBConnector.readMediaData("series");
                for(int i = 0; i < mediaOptions.size(); i++){
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                displayMenu();
                break;
            case 3:
                ui.displayMsg("Browsing All Media");
                mediaOptions = DBConnector.readMediaData("combi");
                for(int i = 0; i < mediaOptions.size(); i++){
                    ui.displayMsg((i + 1) + ". " + mediaOptions.get(i).toString());
                }
                displayMenu();
                break;
        }

    }

    public void buyMedia(){
        int mediaOption = ui.promptNumeric("Please pick a media option");

    }
}