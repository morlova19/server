package server;

import callback.ICallbackClient;
import callback.ICallbackServer;
import remote_journal.JournalManager;
import remote_journal.JournalObserver;
import journal.*;
import to.User;
import utils.XMLUtils;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class CallbackServer implements Serializable, ICallbackServer, JournalObserver {
    ConcurrentHashMap<String, String> users_data;
    ConcurrentHashMap<String, IJournalManager> jManagers;
    ConcurrentHashMap<String, ICallbackClient> ui;
    ConcurrentHashMap<String, ICallbackClient> nSystems;

    public CallbackServer() {
        jManagers = new ConcurrentHashMap<>();
        nSystems = new ConcurrentHashMap<>();
        ui = new ConcurrentHashMap<>();
        users_data = new ConcurrentHashMap<>();
        CopyOnWriteArrayList<User> users = XMLUtils.getUsers();
        if(users != null) {
            if (!users.isEmpty()) {
                users.stream()
                        .forEach(user ->
                        {
                            users_data.put(user.getLogin(), user.getPass());
                            System.out.println("user = " + user.getLogin() + " pass = " + user.getPass());
                        });
            }
        }
        else {
            System.out.println("Not find resource file");
        }
    }


    public static void main(String[] args) {
        try {
            CallbackServer server  = new CallbackServer();
            ICallbackServer stub = (ICallbackServer) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(7777);
            registry.rebind("IAuthorizationService", stub);
            System.out.println("CallbackServer is ready");

            while (true)
            {

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean registerUserInterface(ICallbackClient client) throws RemoteException {
        System.out.println("try to register");
        boolean isAuthorized = false;
        String login = client.getLogin();
        String pass = client.getPassword();
        if(users_data.containsKey(login))
        {
            if (users_data.get(login).equals(pass))
            {
                if(!ui.containsKey(login))
                {
                    ui.put(login,client);
                }
                if(!jManagers.containsKey(login))
                {
                    try {
                        createJournalManager(login);
                        isAuthorized = true;
                    } catch (RemoteException e) {
                        isAuthorized = false;
                    }
                }
                else {
                    isAuthorized = true;
                }

            }
        }
        return isAuthorized;
    }

    private void createJournalManager(String login) throws RemoteException {
        JournalManager jm = new JournalManager(login);
        jm.registerObserver(this);
        jManagers.put(login,jm);
        Registry registry = LocateRegistry.getRegistry(7777);
        IJournalManager stub = (IJournalManager) UnicastRemoteObject.exportObject(jm, 0);
        registry.rebind(login,stub);
    }

    @Override
    public boolean registerNotificationSystem(ICallbackClient client) throws RemoteException {
        boolean isAuthorized = false;
        String login = client.getLogin();
        String pass = client.getPassword();
        if(users_data.containsKey(login))
        {
            if (users_data.get(login).equals(pass))
            {
                if(!nSystems.containsKey(login))
                {
                    nSystems.put(login,client);
                }
                if(!jManagers.containsKey(login))
                {
                    try {
                        createJournalManager(login);
                        isAuthorized = true;
                    } catch (RemoteException e) {
                        isAuthorized = false;
                    }
                }
                else {
                    isAuthorized = true;
                }

            }
        }
        return isAuthorized;
    }

    @Override
    public boolean newUser(ICallbackClient client) {
        boolean isRegistered = false;
        String login = null;
        String pass = null;
        try {
            login = client.getLogin();
            pass = client.getPassword();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            isRegistered = false;
        }
        if (login != null && pass != null) {
            if (!users_data.containsKey(login)) {
                isRegistered = true;
                users_data.put(login, pass);
                XMLUtils.newUser(login, pass);
            }
        }
        return isRegistered;
    }

    @Override
    public boolean unregisterUserInterface(ICallbackClient client) throws RemoteException {
        if(client != null) {
            String login = client.getLogin();
            if(ui.containsKey(login))
            {
                ui.remove(login);
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean unregisterNotificationSystem(ICallbackClient client) throws RemoteException {
        if(client != null) {
            String login = client.getLogin();
            if(nSystems.containsKey(login))
            {
                nSystems.remove(login);
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }


    @Override
    public void updateUserInterface(String login) {
        if(ui.containsKey(login))
        {
            try {
                ui.get(login).update();
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void updateNotificationSystem(String login) {
        if(nSystems.containsKey(login))
        {
            try {
                nSystems.get(login).update();
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
