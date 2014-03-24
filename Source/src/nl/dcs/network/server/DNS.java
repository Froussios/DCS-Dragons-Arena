/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dcs.network.server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivanis
 */
public class DNS {

    private static volatile DNS instance;

    public static final DNS getInstance() {
        if (DNS.instance == null) {
            synchronized (DNS.class) {
                if (DNS.instance == null) {
                    DNS.instance = new DNS();
                }
            }
        }
        return DNS.instance;
    }

    public static Server getServer(String name) {
        return getInstance().mapping.get(name);
    }

    public static Set<String> getServersNames() {
        return getInstance().mapping.keySet();
    }

    public static ServerInterface find(Server s) throws RemoteException, NotBoundException {
        Registry r = LocateRegistry.getRegistry(s.getPort());
        return (ServerInterface) r.lookup(s.getName());
    }

    private final HashMap<String, Server> mapping;

    private DNS() {
        super();
        mapping = new HashMap<>();
        try {
            mapping.put("ALPHA", new Server(1L, "ALPHA", 1099, new String[]{}));
            mapping.put("BETA", new Server(2L, "BETA", 1100, new String[]{}));
            mapping.put("EPSILON", new Server(3L, "EPSLION", 1101, new String[]{}));
            mapping.put("GAMMA", new Server(4L, "GAMMA", 1102, new String[]{}));
            mapping.put("ZETA", new Server(5L, "ZETA", 1103, new String[]{}));
        } catch (RemoteException ex) {
            Logger.getLogger(DNS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ServerInterface find(String s) throws RemoteException, NotBoundException {
        return DNS.find(DNS.getServer(s));
    }

}
