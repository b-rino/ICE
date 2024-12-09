import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.io.File;

public class User {
    private String password;
    private String username;
    private String email;
    private int phoneNumber;
    private int balance;
    private boolean membership = false;

    public boolean isMembership() {
        return membership;
    }

    public User(String username, String password, String email, int phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getEmail(){return email;}
    public int getPhoneNumber(){return phoneNumber;}

    public int getBalance(){
        return balance;}

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) && password.equals(user.password);
    }

    @Override
    public String toString() {
        return  username + "; " + password + "; " + email + "; " + phoneNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
