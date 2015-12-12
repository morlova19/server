package server_impl;

import client.IClient;
import server.IServer;
import remote_journal.JournalManager;
import remote_journal.JournalObserver;
import journal.*;
import to.User;
import utils.DecryptionUtil;
import utils.Icon;
import utils.XMLUtils;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Server class.
 */
public class Server implements Serializable, IServer, JournalObserver {
    /**
     * Logins and passwords of users.
     */
    ConcurrentHashMap<String, String> users_data;
    /**
     * Journal managers of users.
     */
    ConcurrentHashMap<String, IJournalManager> jManagers;
    /**
     * Registered user interfaces.
     */
    ConcurrentHashMap<String, IClient> ui;
    /**
     * Registered notification systems.
     */
    ConcurrentHashMap<String, IClient> nSystems;

    public Server() {
        DecryptionUtil.configKeys();
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

    @Override
    public String getPublicKey() {
        return DecryptionUtil.getPublicKey();
    }

    @Override
    public synchronized boolean registerUI(IClient client) throws RemoteException {
        boolean isAuthorized = false;
        String login = client.getLogin();
        String pass = client.getPassword();
        if(users_data.containsKey(login))
        {
            String received_decrypted = DecryptionUtil.decrypt(pass);
            String right_decrypted = DecryptionUtil.decrypt(users_data.get(login));
            System.out.println(" recieved = " + received_decrypted + "=====" + right_decrypted);
            assert right_decrypted != null;
            if (right_decrypted.equals(received_decrypted))
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


    @Override
    public synchronized boolean registerNotificationSystem(IClient client) throws RemoteException {
        boolean isAuthorized = false;
        String login = client.getLogin();
        String pass = client.getPassword();
        if(users_data.containsKey(login))
        {
            String received_decrypted = DecryptionUtil.decrypt(pass);
            String right_decrypted = DecryptionUtil.decrypt(users_data.get(login));
            assert right_decrypted != null;
            if (right_decrypted.equals(received_decrypted))
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

    public static void main(String[] args) {
        try {
            Server server  = new Server();
            IServer stub = (IServer) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(7777);
            registry.rebind("IAuthorizationService", stub);
            if(SystemTray.isSupported())
            {
                SystemTray tray = SystemTray.getSystemTray();
                Image img = Icon.getIcon();
                PopupMenu popupMenu = new PopupMenu();
                MenuItem stop = new MenuItem("stop");
                stop.addActionListener(e -> System.exit(1));
                popupMenu.add(stop);
                TrayIcon icon = new TrayIcon(img,"Server",popupMenu);
                icon.setImageAutoSize(true);
                try {
                    tray.add(icon);
                } catch (AWTException e) {
                    JOptionPane.showMessageDialog(new JFrame(),"Cannot add icon to system tray","Error",JOptionPane.ERROR_MESSAGE);
                }
                icon.displayMessage("Task Manager Server","Server is running", TrayIcon.MessageType.INFO);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    /**
     * Creates anf puts user's journal manager into rmi registry.
     * @param login user's login.
     * @throws RemoteException
     */
    private synchronized void createJournalManager(String login) throws RemoteException {
        JournalManager jm = new JournalManager(login);
        jm.registerObserver(this);
        jManagers.put(login,jm);
        Registry registry = LocateRegistry.getRegistry(7777);
        IJournalManager stub = (IJournalManager) UnicastRemoteObject.exportObject(jm, 0);
        registry.rebind(login,stub);
    }
}
