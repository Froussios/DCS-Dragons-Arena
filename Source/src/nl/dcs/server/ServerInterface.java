/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.server;

import nl.dcs.da.ClientInterface;
import nl.dcs.da.tss.events.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Ivanis
 */
public interface ServerInterface extends Remote {
    
    public void sendEvent (Event e, ClientInterface c) throws RemoteException;
    public void register (ClientInterface c) throws RemoteException;
    public void unregister (ClientInterface c) throws RemoteException;
}
