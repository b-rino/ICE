import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.io.File;

public class User {
    private String password;
    private String username;

    public User(String username, String password) {
        this.username = username;
        this.password = password;

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
        return  username + "; " + password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
