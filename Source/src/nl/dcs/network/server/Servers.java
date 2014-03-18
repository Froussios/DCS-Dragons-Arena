/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network.server;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivanis
 */
public class Servers {
        
    private static HashMap<String, Server> instance;
    private static HashMap<String, ArrayList<String>> broadcastServers;
    
    private Servers () {
        try {
            instance = new HashMap<>();
            
        } catch (Exception ex) {
            Logger.getLogger(Servers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static HashMap<String, Server>  getInstance (){
        if (instance == null){
            Servers s = new Servers ();
        }
        return instance;
    }
    
    public static Server getServerByName (String name){
        return Servers.getInstance().get(name);
    }
    
    
    
}
