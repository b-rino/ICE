package Model;

import java.util.Objects;

public class User {
    private String password;
    private String username;
    private int balance;
    private String email;


    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
        return  "Username: " + username + " Password:  " + password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
