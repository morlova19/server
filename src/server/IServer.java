package server;

import client.IClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;

/**
 * Remote interface of server.
 */
public interface IServer extends Remote {
    /**
     * Gets public key for encryption.
     * @return string with public key.
     * @throws RemoteException
     */
    String getPublicKey() throws RemoteException;

    /**
     * Registers user interface.
     * @param client user interface.
     * @return true if registered, else - false.
     * @throws RemoteException
     */
    boolean registerUI(IClient client) throws RemoteException;
    /**
     * Registers notification system.
     * @param client notification system.
     * @return true if registered, else - false.
     * @throws RemoteException
     */
    boolean registerNotificationSystem(IClient client) throws RemoteException;

    /**
     * Creates new user.
     * @param client new user.
     * @return true if created, else - false.
     * @throws RemoteException
     */
    boolean newUser(IClient client)throws RemoteException;

    /**
     * Unregisters user interface.
     * @param client user interface.
     * @return true if registered, else - false.
     * @throws RemoteException
     */
    boolean unregisterUI(IClient client)throws RemoteException;
    /**
     * Unregisters notification system.
     * @param client notification system.
     * @return true if registered, else - false.
     * @throws RemoteException
     */
    boolean unregisterNotificationSystem(IClient client)throws RemoteException;
}
