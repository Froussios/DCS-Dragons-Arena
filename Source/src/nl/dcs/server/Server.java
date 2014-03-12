/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import nl.dcs.da.tss.events.Event;


/**
 *
 * @author Ivanis
 */
public class Server {
    
    public static void main (String[] args) throws RemoteException{
        try {
        	
            RMIInterface skeleton = (RMIImplementation) UnicastRemoteObject.exportObject(new RMIImplementation(), 10000); 
            Registry registry = LocateRegistry.createRegistry(10000);
            registry.rebind("Recieve", skeleton); // publie notre instance sous le nom "Add"
        } catch (RemoteException e) {
        }
    }

}
