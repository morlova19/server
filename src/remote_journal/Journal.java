package remote_journal;


import journal.Task;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Part of taskmgr.
 */
public class Journal implements Serializable {
    /**
     * List of current tasks.
     */
    private CopyOnWriteArrayList<Task> currentTasks = new CopyOnWriteArrayList<>();
    /**
     * List of completed tasks.
     */
    private CopyOnWriteArrayList<Task> completedTasks = new CopyOnWriteArrayList<>();

    /**
     * Last generated id for task.
     */
    private int generated_task_id;

    /**
     * Sets {@link #generated_task_id}.
     * @param generated_task_id new value of last generated id.
     */
    public void setGenerated_task_id(int generated_task_id) {
        this.generated_task_id = generated_task_id;
    }
    /**
     * Gets task by identifier.
     * @param id task's id.
     * @return task.
     */
    public Task getTask(int id) {

        for(Task t: completedTasks) {
            if(t.getID() == id) {
                return t;
            }
        }
        for(Task t: currentTasks) {
            if(t.getID() == id) {
                return t;
            }
        }
       return null;
    }

    /**
     * Adds task in list.
     * @param newTask new task.
     */
    void addTask(Task newTask) {
        if(currentTasks != null) {
            newTask.setID(generated_task_id++);
            currentTasks.add(newTask);
        }
    }
    /**
     * Deletes task.
     * @param id identifier of task that will be deleted.
     */
    void deleteTask(int id) {
        for(Task t: completedTasks)
        {
            if(t.getID() == id)
            {
                completedTasks.remove(t);
                return;
            }
        }
        for(Task t: currentTasks)
        {
            if(t.getID() == id)
            {
                currentTasks.remove(t);
                return;
            }
        }
    }
    /**
     * Delays task.
     * @param id identifier of task that will be delayed.
     * @param newDate new date of task.
     */
    void delayTask(int id, Date newDate) {
        Task t = currentTasks
                .stream()
                .filter(task -> task.getID() == id)
                .findFirst().get();
        t.setDate(newDate);
    }

    /**
     * Makes task completed.
     * Removes from current tasks and adds into completed tasks.
     * @param task completed task.
     */
    void setCompleted(Task task) {
        currentTasks.remove(task);
        completedTasks.add(task);
    }

    /**
     * Gets list of current tasks.
     * @return current tasks.
     */
    public CopyOnWriteArrayList<Task> getCurrentTasks() {
        return currentTasks;
    }

    /**
     * Sets list of current tasks.
     * @param currentTasks current tasks.
     */
    public void setCurrentTasks(CopyOnWriteArrayList<Task> currentTasks) {
        this.currentTasks = currentTasks;
    }
    /**
     * Gets list of completed tasks.
     * @return completed tasks.
     */
    public CopyOnWriteArrayList<Task> getCompletedTasks() {
        return completedTasks;
    }
    /**
     * Sets list of completed tasks.
     * @param completedTasks completed tasks.
     */
    public void setCompletedTasks(CopyOnWriteArrayList<Task> completedTasks) {
        this.completedTasks = completedTasks;
    }

    /**
     * Checks if there are among the current problems already completed tasks.
     * If finds such tasks, makes them completed.
     */
    public void reload() {
        if(!currentTasks.isEmpty())
        {
            for (Task t: currentTasks)
            {
                long delta = t.getDate().getTime() - Calendar.getInstance().getTimeInMillis();
                if(delta <= 0) {
                    currentTasks.remove(t);
                    completedTasks.add(t);
                }
            }

        }
    }


}
