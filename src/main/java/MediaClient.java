import java.util.ArrayList;
import java.util.Scanner;

public class MediaClient {
    private DBConnector dbc = new DBConnector();
    private TextUI textUI = new TextUI();
    private User currentUser;
    public MediaClient(User currentUser){
        this.currentUser = currentUser;
        System.out.println("Current User is " + currentUser);
    }

    public void displayMenu(){
        ArrayList<String> options = new ArrayList<>();
        System.out.println("MAIN MENU");

        options.add("1. Browse Media: ");
        options.add("2. See History: ");
        options.add("3. Display Favorites: ");
        options.add("4. Account Information");

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
                System.out.println("ACCOUNT INFORMATION");
                break;
            default:
                System.out.println("Invalid choice. Please enter a number from the displayed options.");
        }
    }
}