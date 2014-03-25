package server;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton that manages and persists users and their files.
 */
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

        // user not found, create one
        final User user = new User();
        user.setName(name);
        user.setPassword(password);
        users.add(user);
        save();
        return user;
    }

    /**
     * Finds the specified user by name, ignoring the password
     * returns null if a user with the name does not exist
     *
     * @param name name of the user
     * @return the matched user object if found, otherwise null!!
     */
    public User find(final String name) {
        for (final User u : users) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Persists the user list to the disk.
     */
    public void save() {
        try {
            final XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(userFile)));
            encoder.writeObject(users);
            encoder.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
