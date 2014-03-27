/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network.server;

import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.Event;
import nl.dcs.network.client.ClientInterface;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

/**
 * @author Ivanis
 */
public interface ServerInterface extends Remote {

    void sendEvent(long sender, Event e) throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException;

    void transferEvent(Event e) throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException;

    public TSS register(long sender, ClientInterface c) throws RemoteException;

    public void unregister(long sender, ClientInterface c) throws RemoteException;
}
