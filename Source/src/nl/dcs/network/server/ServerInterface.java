/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network.server;

import nl.dcs.network.client.ClientInterface;
import nl.dcs.da.tss.events.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Ivanis
 */
public interface ServerInterface extends Remote {
    
    public void sendEvent (long sender, Event e) throws RemoteException;
    public ServerInterface register (long sender, ClientInterface c) throws RemoteException;
    public void unregister (long sender, ClientInterface c) throws RemoteException;
}
