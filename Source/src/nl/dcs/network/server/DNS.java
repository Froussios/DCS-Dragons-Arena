/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dcs.network.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    public static String getServerAddress(int i) {
        return getInstance().addresses.get(i);
    }

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

    public static ServerInterface lookup(int i) throws RemoteException, NotBoundException, MalformedURLException{
        
        return (ServerInterface) Naming.lookup(getServerAddress(i) + "SERVER");
        
    }
    
    
    
    
    public static int getNbServers (){
        return getInstance().addresses.size();
    }
    
    
    
}
