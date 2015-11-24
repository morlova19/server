package utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import to.User;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class XMLUtils {
    private static CopyOnWriteArrayList<User> users = getUsersFromXML();
    public static CopyOnWriteArrayList<User> getUsers()
    {
        return users;
    }
    private static synchronized CopyOnWriteArrayList<User> getUsersFromXML()
    {
        users = new CopyOnWriteArrayList<>();
        XStream stream = new XStream(new DomDriver("UTF-8"));
        stream.processAnnotations(User.class);
        Vector<User> users1 = new Vector<>();
        stream.alias("users", users1.getClass());
        try {
            users1 = (Vector<User>) stream.fromXML(new FileInputStream(System.getProperty("user.home") + "/server/users.xml"));
            System.out.println("users count = " + users1.size());
            users.addAll(users1);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            writeUsers();
        }
        //TODO:read users
        return users;
    }
    public static synchronized User getUser(String login)
    {
        User u = null;
        try {
             u = users.stream()
                    .filter(user -> user.getLogin().equals(login))
                    .findFirst().get();
            return u;
        }catch (NoSuchElementException e)
        {
            return u;
        }
    }
    public static synchronized void newUser(String login,String pass)
    {
        User user = new User(login,pass);
        user.setCurrent_tasks(new CopyOnWriteArrayList<>());
        user.setCompleted_tasks(new CopyOnWriteArrayList<>());
        users.add(user);
        writeUsers();

    }
    public static synchronized void writeUser(User user)
    {
        try
        {
            User u = users.stream()
                    .filter(user1 -> (user1.getLogin()).equals((user.getLogin())))
                    .findFirst().get();
            users.set(users.indexOf(u),user);
        }
        catch (NoSuchElementException e)
        {
            users.add(user);
        }
        writeUsers();
    }
    private static synchronized void writeUsers()
    {
        XStream stream = new XStream(new DomDriver("UTF-8"));
        stream.processAnnotations(User.class);
        Vector<User> users1 = new Vector<>();
        users1.addAll(users);
        stream.alias("users", users1.getClass());
        File dir = new File(System.getProperty("user.home") + "/server/");
        if(!dir.exists())
        {
            dir.mkdir();
        }
        File file = new File(dir + "/users.xml");

        try {
            if(!file.exists())
            {
                file.createNewFile();
            }
            stream.toXML(users1, new FileOutputStream(file));
        }
        catch (IOException e) {
           e.printStackTrace();
        }
    }
}
