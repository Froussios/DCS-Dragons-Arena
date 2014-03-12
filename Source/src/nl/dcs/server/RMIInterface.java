/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import nl.dcs.da.tss.events.Event;

/**
 *
 * @author Ivanis
 */
public interface RMIInterface extends Remote {
    
    public void recieveEvent (long sender, Event e) throws RemoteException;
    
    
}
