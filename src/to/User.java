package to;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import journal.Task;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class for writing information about user to xml.
 */
@XStreamAlias("user")
public class User {
    @XStreamAsAttribute
    String login;
    @XStreamAsAttribute
    String pass;
    @XStreamAlias("current_tasks")
    CopyOnWriteArrayList<Task> current_tasks;
    @XStreamAlias("completed_tasks")
    CopyOnWriteArrayList<Task> completed_tasks;

    public CopyOnWriteArrayList<Task> getCurrent_tasks() {
        return current_tasks;
    }

    public CopyOnWriteArrayList<Task> getCompleted_tasks() {
        return completed_tasks;
    }

    public void setCurrent_tasks(CopyOnWriteArrayList<Task> current_tasks) {
        this.current_tasks = current_tasks;
    }

    public void setCompleted_tasks(CopyOnWriteArrayList<Task> completed_tasks) {
        this.completed_tasks = completed_tasks;
    }

    public User(String login, String pass) {
        this.login = login;
        this.pass = pass;
        this.current_tasks = new CopyOnWriteArrayList<>();
        this.completed_tasks = new CopyOnWriteArrayList<>();
    }

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public String getPass() {
        return pass;
    }
}
