package server;

import client.IClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
    boolean registerUI(IClient client) throws RemoteException;
    boolean registerNotificationSystem(IClient client) throws RemoteException;
    boolean newUser(IClient client)throws RemoteException;
    boolean unregisterUI(IClient client)throws RemoteException;
    boolean unregisterNotificationSystem(IClient client)throws RemoteException;
}
