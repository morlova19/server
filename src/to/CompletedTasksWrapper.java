package to;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import journal.Task;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class for writing user's completed tasks to xml.
 */
@XStreamAlias("completed_tasks")
public class CompletedTasksWrapper {
    @XStreamAsAttribute
    String login;
    @XStreamImplicit
    CopyOnWriteArrayList<Task> completed_tasks;

    public CompletedTasksWrapper(String login, CopyOnWriteArrayList<Task> completed_tasks) {
        this.login = login;
        this.completed_tasks = completed_tasks;
    }

    public void setCompleted_tasks(CopyOnWriteArrayList<Task> current_tasks) {
        this.completed_tasks = current_tasks;
    }

    public String getLogin() {
        return login;
    }

    public CopyOnWriteArrayList<Task> getCompleted_tasks() {
        return completed_tasks;
    }
}
