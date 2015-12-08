package remote_journal;

import journal.IJournalManager;
import journal.Task;
import utils.XMLUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Part of taskmgr.
 */

public class JournalManager implements IJournalManager, Serializable {
    /**
     * Directory in which files with tasks will be stored.
     */
    private String login;
    /**
     * Journal of tasks.
     */
    private Journal journal;
    /**
     * Observer of journal.
     */
    private JournalObserver observer;
    /**
     * Creates journal manager and fills fields.
     * @param user directory in which there are files with tasks.
     */
    public JournalManager(String user) {
        this.login = user;
        loadJournal();
    }
    /**
     * Loads journal.
     */
    public void loadJournal()  {
        journal = readJournal();
        journal.reload();
        writeJournal();
    }

    public synchronized void add(Task task)  {
        if(task != null) {
            journal.addTask(task);
            writeJournal();
            observer.updateNotificationSystem(login);
        }
    }
    public synchronized void delete(int id)  {
        journal.deleteTask(id);
        writeJournal();
        observer.updateNotificationSystem(login);
    }

    @Override
    public CopyOnWriteArrayList<Task> getCurrentTasks() {
        return journal.getCurrentTasks();
    }

    @Override
    public CopyOnWriteArrayList<Task> getCompletedTasks() {
        return journal.getCompletedTasks();
    }

    public Task get(int id) {
        return journal.getTask(id);
    }

    public void delay(int id, Date newDate) {
        journal.delayTask(id, newDate);
        writeJournal();
        observer.updateUserInterface(login);
    }

    public void complete(int id)  {
        journal.setCompleted(journal.getTask(id));
        writeJournal();
        observer.updateUserInterface(login);
    }
    /**
     * Writes journal.
     */
    public void writeJournal() {
        XMLUtils.writeUserTasks(this.login,journal.getCurrentTasks(),journal.getCompletedTasks());
    }
    /**
     * Reads journal.
     * @return journal.
     */
    public Journal readJournal()  {
            Journal journal = new Journal();
            if(!this.login.isEmpty()) {
                CopyOnWriteArrayList<Task> cur_tasks = XMLUtils.getCurrentTasks(this.login);
                journal.setCurrentTasks(cur_tasks);
                CopyOnWriteArrayList<Task> completed_tasks = XMLUtils.getCompletedTasks(this.login);
                journal.setCompletedTasks(completed_tasks);
                int max1 = 0;
                int max2 = 0;
                if (!completed_tasks.isEmpty()) {
                        max1 = journal.getCompletedTasks()
                                .stream()
                                .mapToInt(Task::getID)
                                .max().getAsInt();
                }
                if (!cur_tasks.isEmpty()) {
                        max2 = journal.getCurrentTasks()
                                .stream()
                                .mapToInt(Task::getID).max().getAsInt();
                }
                journal.setGenerated_task_id(Math.max(max1, max2) + 1);
        }
        return journal;
    }
    /**
     * Registers observer of journal.
     * @param observer journal observer.
     */
    public synchronized void registerObserver(JournalObserver observer) {
        this.observer = observer;
    }
}
