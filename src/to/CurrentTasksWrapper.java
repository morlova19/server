package to;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import journal.Task;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class for writing user's current tasks to xml.
 */
@XStreamAlias("current_tasks")
public class CurrentTasksWrapper {
    @XStreamAsAttribute
    String login;
    @XStreamImplicit
    CopyOnWriteArrayList<Task> current_tasks;

    public void setCurrent_tasks(CopyOnWriteArrayList<Task> current_tasks) {
        this.current_tasks = current_tasks;
    }

    public CurrentTasksWrapper(String login, CopyOnWriteArrayList<Task> current_tasks) {
        this.login = login;
        this.current_tasks = current_tasks;
    }

    public String getLogin() {
        return login;
    }

    public CopyOnWriteArrayList<Task> getCurrent_tasks() {
        return current_tasks;
    }
}
