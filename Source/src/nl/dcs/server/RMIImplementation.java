/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import nl.dcs.da.tss.events.Event;
import nl.dcs.da.tss.util.StateLogger;

/**
 *
 * @author Ivanis
 */
public class RMIImplementation extends UnicastRemoteObject implements RMIInterface {

    StateLogger log;
    Queue<Event> eventQueue;
    
    public RMIImplementation () throws RemoteException{
        super();
        
        log = new StateLogger ();
        eventQueue = new SynchronousQueue<>();
        
    }

    public RMIImplementation(StateLogger log, Queue<Event> eventQueue) throws RemoteException{
        super();
        this.log = log;
        this.eventQueue = eventQueue;
    }
    
    @Override
    public void recieveEvent(long sender, Event e) throws RemoteException {
        log.add(e.toString());
        eventQueue.add(e);
    }
    
}
