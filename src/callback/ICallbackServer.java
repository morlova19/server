package callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICallbackServer extends Remote {
    boolean registerUserInterface(ICallbackClient client) throws RemoteException;
    boolean registerNotificationSystem(ICallbackClient client) throws RemoteException;
    boolean newUser(ICallbackClient client)throws RemoteException;
    boolean unregisterUserInterface(ICallbackClient client)throws RemoteException;
    boolean unregisterNotificationSystem(ICallbackClient client)throws RemoteException;
}
