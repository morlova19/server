package callback;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICallbackClient extends Remote {
    void update() throws RemoteException;
    String getLogin() throws RemoteException;
    String getPassword()throws RemoteException;
}
