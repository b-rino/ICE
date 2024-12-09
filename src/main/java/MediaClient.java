import java.util.ArrayList;
import java.util.Scanner;

public class MediaClient {
    private DBConnector dbc = new DBConnector();
    private TextUI ui = new TextUI();
    private User currentUser;

    public MediaClient(User currentUser){
        this.currentUser = currentUser;
        System.out.println("Current User is " + currentUser.getUsername());
    }


    public void displayMenu(){
        ArrayList<String> options = new ArrayList<>();
        System.out.println("MAIN MENU");

        options.add("1. Browse Media: ");
        options.add("2. See History: ");
        options.add("3. Display Favorites: ");
        options.add("4. Account Information");
        options.add("5. Exit");

        for (int i = 0; i < options.size(); i++){
            System.out.println(options.get(i));
        }
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice){
            case 1:
                System.out.println("Browsing All Media");
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
                System.out.println("Invalid choice - please choose a number between 1 and 4");
                displayMenu();        }
    }
}