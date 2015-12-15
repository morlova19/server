package utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import journal.Task;
import to.CompletedTasksWrapper;
import to.CurrentTasksWrapper;
import to.User;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class for reading and writing xml files.
 */
public class XMLUtils {
    /**
     * Users.
     */
    private static CopyOnWriteArrayList<User> users;
    /**
     * Current tasks of users.
     */
    private static CopyOnWriteArrayList<CurrentTasksWrapper> users_current_tasks;
    /**
     * Completed tasks of users.
     */
    private static CopyOnWriteArrayList<CompletedTasksWrapper> users_completed_tasks;
    /**
     * Directory in which will be stored xml files.
     */
    private static  String PATHNAME;
    /**
     * File in which user's details will be stored.
     */
    private static  String USERS_FILE ;
    /**
     * File in which current tasks of users will be stored.
     */
    private static  String CURRENT_TASKS_FILE;
    /**
     * File in which completed tasks of users will be stored.
     */
    private static  String COMPLETED_TASKS_FILE;
    /**
     * Gets users list.
     * @return list with users.
     */
    public static CopyOnWriteArrayList<User> getUsers()
    {
        configPaths();
        readUserFromXML();
        return users;
    }

    /**
     * Gets user's current tasks.
     * @param login user's login
     * @return user's current tasks.
     */
    public static synchronized CopyOnWriteArrayList<Task> getCurrentTasks(String login)
    {
        CopyOnWriteArrayList<Task> tasks;
        try {
            tasks = users_current_tasks.stream()
                    .filter(user -> user.getLogin().equals(login))
                    .findFirst().get().getCurrent_tasks();
            return tasks;
        }catch (NoSuchElementException e)
        {
            return null;
        }
    }

    /**
     * Gets user's completed tasks.
     * @param login user's login
     * @return user's current tasks.
     */
    public static synchronized CopyOnWriteArrayList<Task> getCompletedTasks(String login)
    {
        CopyOnWriteArrayList<Task> tasks;
        try {
            tasks = users_completed_tasks.stream()
                    .filter(user -> user.getLogin().equals(login))
                    .findFirst().get().getCompleted_tasks();
            return tasks;
        }catch (NoSuchElementException e)
        {
            return null;
        }
    }

    /**
     * Creates new user.
     * @param login login of new user.
     * @param pass password of new user.
     */
    public static synchronized void newUser(String login,String pass)
    {
        User user = new User(login,pass);
        users.add(user);

        CurrentTasksWrapper ct = new CurrentTasksWrapper(login,new CopyOnWriteArrayList<>());
        users_current_tasks.add(ct);

        CompletedTasksWrapper ct1 = new CompletedTasksWrapper(login,new CopyOnWriteArrayList<>());
        users_completed_tasks.add(ct1);

        writeUsers();

    }

    /**
     * Updates user's current and completed tasks.
     * @param login user's login.
     * @param current user's current tasks.
     * @param completed user's completed tasks.
     */
    public static synchronized void writeUserTasks(String login, CopyOnWriteArrayList<Task> current, CopyOnWriteArrayList<Task> completed)
    {
        try
        {
            CurrentTasksWrapper c1 = (users_current_tasks.stream()
                    .filter(c -> c.getLogin().equals(login))
                    .findFirst()
                    .get());
            c1.setCurrent_tasks(current);
            users_current_tasks.set(users_current_tasks.indexOf(c1),c1);
        }
        catch (NoSuchElementException e)
        {
            users_current_tasks.add(new CurrentTasksWrapper(login, current));
        }
        try
        {
            CompletedTasksWrapper c1 = (users_completed_tasks.stream()
                    .filter(c -> c.getLogin()
                            .equals(login))
                    .findFirst()
                    .get());
            c1.setCompleted_tasks(completed);
            users_completed_tasks.set(users_completed_tasks.indexOf(c1),c1);
        }
        catch (NoSuchElementException e)
        {
            users_completed_tasks.add(new CompletedTasksWrapper(login, completed));

        }
        writeUsers();
    }

    /**
     * Writes users, its current and completed tasks to files.
     */
    private static synchronized void writeUsers() {
        XStream stream = getXStream();
        File dir = new File(PATHNAME);
        if(!dir.exists())
        {
            dir.mkdir();
        }
        File users_file = new File(USERS_FILE);
        File current_tasks_file = new File(CURRENT_TASKS_FILE);
        File completed_tasks_file = new File(COMPLETED_TASKS_FILE);
        try {
            if(!users_file.exists())
            {
                users_file.createNewFile();
            }
            if(!current_tasks_file.exists())
            {
                current_tasks_file.createNewFile();
            }
            if(!completed_tasks_file.exists())
            {
                completed_tasks_file.createNewFile();
            }
            stream.toXML(users, new FileOutputStream(users_file));
            stream.toXML(users_current_tasks, new FileOutputStream(current_tasks_file));
            stream.toXML(users_completed_tasks,new FileOutputStream(completed_tasks_file));
        }
        catch (IOException e) {
           e.printStackTrace();
        }
    }
    /**
     * Creates and configures xstream instance.
     * @return  xstream instance.
     */
    private static XStream getXStream() {
        XStream stream = new XStream(new DomDriver("UTF-8"));
        stream.processAnnotations(to.User.class);
        stream.processAnnotations(to.CurrentTasksWrapper.class);
        stream.processAnnotations(to.CompletedTasksWrapper.class);
        return stream;
    }
    /**
     * Reads user, its current and completed tasks from files.
     */
    private static synchronized void readUserFromXML() {
        XStream xstream = getXStream();
        users = new CopyOnWriteArrayList<>();
        users_current_tasks = new CopyOnWriteArrayList<>();
        users_completed_tasks = new CopyOnWriteArrayList<>();
        try {
            users = (CopyOnWriteArrayList) xstream.fromXML(new FileInputStream(USERS_FILE));
            users_current_tasks = (CopyOnWriteArrayList<CurrentTasksWrapper>)
                    xstream.fromXML(new FileInputStream(CURRENT_TASKS_FILE));
            users_current_tasks.stream().forEach(currentTasks ->
            {
                if(currentTasks.getCurrent_tasks() == null)
                {
                    currentTasks.setCurrent_tasks(new CopyOnWriteArrayList<>());
                }
            });
            users_completed_tasks = (CopyOnWriteArrayList<CompletedTasksWrapper>)
                    xstream.fromXML(new FileInputStream(COMPLETED_TASKS_FILE));
            users_completed_tasks.stream().forEach(completedTasks -> {
                if(completedTasks.getCompleted_tasks() == null)
                {
                    completedTasks.setCompleted_tasks(new CopyOnWriteArrayList<>());
                }
            });
        } catch (FileNotFoundException e) {
            writeUsers();
        }
    }
    /**
     * Initializes paths of xml files.
     */
    private synchronized static void configPaths() {
        PATHNAME = System.getProperty("user.home") + "/server_impl/";
        USERS_FILE = PATHNAME + "users.xml";
        CURRENT_TASKS_FILE = PATHNAME + "current.xml";
        COMPLETED_TASKS_FILE = PATHNAME +"completed.xml";
    }
}
