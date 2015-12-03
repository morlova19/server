package server_impl;

import client.IClient;
import server.IServer;
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

public class Server implements Serializable, IServer, JournalObserver {
    ConcurrentHashMap<String, String> users_data;
    ConcurrentHashMap<String, IJournalManager> jManagers;
    ConcurrentHashMap<String, IClient> ui;
    ConcurrentHashMap<String, IClient> nSystems;

    public Server() {
        jManagers = new ConcurrentHashMap<>();
        nSystems = new ConcurrentHashMap<>();
        ui = new ConcurrentHashMap<>();
        users_data = new ConcurrentHashMap<>();
        CopyOnWriteArrayList<User> users = XMLUtils.getUsers();
        if(users != null) {
            if (!users.isEmpty()) {
                users.stream()
                        .forEach(user -> users_data.put(user.getLogin(), user.getPass()));
            }
        }
        else {
            System.out.println("Not find resource file");
        }
    }

    public static void main(String[] args) {
        try {
            Server server  = new Server();
            IServer stub = (IServer) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(7777);
            registry.rebind("IAuthorizationService", stub);
            System.out.println("Server is ready");

            while (true)
            {

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized boolean registerUI(IClient client) throws RemoteException {
        boolean isAuthorized = false;
        String login = client.getLogin();
        String pass = client.getPassword();
        if(users_data.containsKey(login))
        {
            if (users_data.get(login).equals(pass))
            {
                ui.put(login,client);
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

    private synchronized void createJournalManager(String login) throws RemoteException {
        JournalManager jm = new JournalManager(login);
        jm.registerObserver(this);
        jManagers.put(login,jm);
        Registry registry = LocateRegistry.getRegistry(7777);
        IJournalManager stub = (IJournalManager) UnicastRemoteObject.exportObject(jm, 0);
        registry.rebind(login,stub);
    }

    @Override
    public synchronized boolean registerNotificationSystem(IClient client) throws RemoteException {
        boolean isAuthorized = false;
        String login = client.getLogin();
        String pass = client.getPassword();
        if(users_data.containsKey(login))
        {
            if (users_data.get(login).equals(pass))
            {
                nSystems.put(login,client);
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
    public synchronized boolean newUser(IClient client) {
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
    public synchronized boolean unregisterUI(IClient client) throws RemoteException {
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
    public synchronized boolean unregisterNotificationSystem(IClient client) throws RemoteException {
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
