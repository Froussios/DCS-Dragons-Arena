/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.server;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.util.StateLogger;

/**
 *
 * @author Ivanis
 */
public class RMIImplementation extends UnicastRemoteObject implements RMIInterface {

    StateLogger log;
    Map eventQueue;
    
    public RMIImplementation () throws RemoteException{
        super();
        
        log = new StateLogger ();
        eventQueue = Collections.synchronizedMap(new LinkedHashMap<>());
        
    }

    public RMIImplementation(StateLogger log, LinkedHashMap<Event,Integer> eventQueue) throws RemoteException{
        super();
        this.log = log;
        this.eventQueue = Collections.synchronizedMap(eventQueue);
    }
    
    @Override
    public void recieveEvent(Event e) throws RemoteException, AccessException {
        log.add(e.toString());
        eventQueue.put(e, (Integer) eventQueue.get(e) + 1);
        if ((Integer) eventQueue.get(e) == 1){
            try {
                Registry registry = LocateRegistry.getRegistry();
                
                RMIInterface stub = (RMIInterface) registry.lookup("Recieve");
                stub.recieveEvent(e);
            } catch (NotBoundException ex) {
                Logger.getLogger(RMIImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
        System.out.println(e + " " +   eventQueue.get(e));
    }
    
}
