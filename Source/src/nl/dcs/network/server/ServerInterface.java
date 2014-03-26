/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network.server;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import nl.dcs.network.client.ClientInterface;
import nl.dcs.da.tss.events.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import nl.dcs.da.tss.TSS;

/**
 *
 * @author Ivanis
 */
public interface ServerInterface extends Remote {
    
    void sendEvent (long sender, Event e) throws RemoteException, NotBoundException, ServerNotActiveException,MalformedURLException;
    void transferEvent (Event e) throws RemoteException, NotBoundException, ServerNotActiveException,MalformedURLException;
    public TSS register (long sender, ClientInterface c) throws RemoteException;
    public void unregister (long sender, ClientInterface c) throws RemoteException;
}
