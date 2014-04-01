/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dcs.network.server;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ivanis
 */
public class DNS {

    private static volatile DNS instance;
    private final List<String> addresses;

    private DNS() {
        super();
        addresses = new ArrayList<>();
        addresses.add("rmi://localhost:1099/");
        addresses.add("rmi://localhost:1100/");
        addresses.add("rmi://localhost:1101/");
        addresses.add("rmi://localhost:1102/");
        addresses.add("rmi://localhost:1103/");
    }

    private static final DNS getInstance() {
        if (DNS.instance == null) {
            synchronized (DNS.class) {
                if (DNS.instance == null) {
                    DNS.instance = new DNS();
                }
            }
        }
        return DNS.instance;
    }

    public static String getServerAddress(int i) throws IllegalArgumentException {
        if (i < 0 || i > getInstance().addresses.size()) throw new IllegalArgumentException("Unknown server");
        return getInstance().addresses.get(i);
    }

    public static ServerInterface lookup(int i) throws RemoteException, NotBoundException, MalformedURLException {
         return (ServerInterface) Naming.lookup(getServerAddress(i) + "SERVER");
    }


    public static int getNbServers() {
        return getInstance().addresses.size();
    }

    public static void addServer(String address) throws UnknownHostException {
        try {
            getInstance().addresses.add(address);
            lookup(getNbServers() - 1);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            getInstance().addresses.remove(getNbServers() - 1);
        }
    }


    public static void list() {
        for (String address : getInstance().addresses) {
            System.out.println(address);
        }
    }
}
