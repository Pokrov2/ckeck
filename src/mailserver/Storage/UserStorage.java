package mailserver.Storage;

import mailserver.Model.User;
import java.util.*;

public class UserStorage {
    private final Map<String, User> users = new HashMap<>();

    public void AddUser(User user) {
        users.put(user.GetUsername(), user);
    }

    public boolean UserExists(String username) {
        return users.containsKey(username);
    }

    public User GetUser(String username) {
        return users.get(username);
    }

    public List<User> GetAllUsers() {
        return new ArrayList<>(users.values());
    }

}
