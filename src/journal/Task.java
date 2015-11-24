package journal;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import to.TransferObject;

import java.io.Serializable;
import java.util.Date;

/**
 * A task that will be executed at a specified time.
 * The task has 4 parameters such as name, description, date of execution, contacts.
 */
@XStreamAlias("task")
public class Task implements Serializable{
    /**
     * Name of the task.
     */
    private String name;
    /**
     * Description of the task.
     */
    private String description;
    /**
     * Time of execution of the task.
     */
    private Date date;
    /**
     * Contacts.
     */
    private String contacts;
    /**
     * Identifier of the task.
     */
    private int ID;

    /**
     * Creates a task with the given parameters.
     * @param data object that contains parameters of the task.
     */

    public Task(TransferObject data) {
        this.name = data.getName();
        this.description = data.getDescription();
        this.date = data.getDate();
        this.contacts = data.getContacts();
    }
    /**
     * Gets name of the task.
     * @return name of the task.
     */
    public synchronized String getName() {
        return name;
    }
    /**
     *  Gets description of the task.
     * @return description of the task.
     */
    public synchronized String getDescription() {
        return description;
    }
    /**
     *  Gets date of execution of the task.
     * @return date of execution.
     */
    public synchronized Date getDate() {
        return date;
    }
    /**
     * Gets contacts.
     * @return contacts.
     */
    public synchronized String getContacts() {
        return contacts;
    }
    /**
     * Sets new date of execution of the task.
     * @param date new date.
     */
    public synchronized void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets identifier of the task.
     * @param ID identifier.
     */
    public synchronized void setID(int ID) {
        this.ID = ID;
    }
    /**
     * Gets identifier of the task.
     * @return identifier.
     */
    public synchronized int getID() {
        return ID;
    }

    @Override
    public synchronized String toString() {
        StringBuilder s = new StringBuilder();
        s.append("NAME: ")
                .append(this.name)
                .append("\nDESCRIPTION: ")
                .append(this.description)
                .append("\nCONTACTS: ")
                .append(this.contacts);
        return s.toString();
    }


}
