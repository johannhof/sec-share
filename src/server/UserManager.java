package server;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final UserManager ourInstance = new UserManager();

    private List<User> users;
    private final File userFile;

    public static UserManager getInstance() {
        return ourInstance;
    }

    private UserManager() {
        userFile = new File(SecShareServer.SERVER_REPO, "users.xml");
        if (userFile.exists()) {
            try {
                final XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(userFile)));
                users = (ArrayList<User>) d.readObject();
                d.close();
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            users = new ArrayList<>();
        }
    }

    /**
     * Finds or creates the specified user by name, trying to match the password
     * if a user with the specified name does not exist, it will create a new user
     * returns null if a user with the name exists but the password did not match
     *
     * @param name     name of the user
     * @param password user password
     * @return the matched user object if found, otherwise null!!
     */
    public User findOrCreate(final String name, final String password) {
        for (final User u : users) {
            if (u.getName().equals(name)) {
                if (u.getPassword().equals(password)) {
                    return u;
                }
                return null;
            }
        }

        final User user = new User();
        user.setName(name);
        user.setPassword(password);
        users.add(user);
        save();
        return user;
    }

    /**
     * Persists the user list to the disk.
     */
    public void save() {
        try {
            final XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(userFile)));
            e.writeObject(users);
            e.close();
        } catch (final FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
