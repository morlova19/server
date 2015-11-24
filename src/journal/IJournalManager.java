package journal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Remote interface of journal manager.
 */
public interface IJournalManager extends Remote {
    /**
     * Adds task.
     * @param task new task.
     * @throws RemoteException
     */
    void add(Task task) throws RemoteException;

    /**
     * Deletes task with specified identifier.
     * @param id identifier of task.
     * @throws RemoteException
     */
    void delete(int id) throws RemoteException;

    /**
     * Gets current tasks.
     * @return list of current tasks.
     * @throws RemoteException
     */
    CopyOnWriteArrayList<Task> getCurrentTasks()throws RemoteException;
    /**
     * Gets completed tasks.
     * @return list of completed tasks.
     * @throws RemoteException
     */
    CopyOnWriteArrayList<Task> getCompletedTasks()throws RemoteException;

    void complete(int id)throws RemoteException;

    void delay(int id, Date newDate)throws RemoteException;

    Task get(int id)throws RemoteException;
}
