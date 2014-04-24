/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network.server;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.Map;

import nl.dcs.da.tss.OutOfSyncException;
import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.TimedTSS;
import nl.dcs.da.tss.events.Event;
import nl.dcs.network.client.ClientInterface;

/**
 * @author Ivanis
 */
public interface ServerInterface
		extends Remote
{

	/**
	 * Used by the client to send an event to the server
	 * 
	 * @param sender
	 * @param event
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws ServerNotActiveException
	 * @throws OutOfSyncException
	 * @throws Exception 
	 */
	void sendEvent(long sender, Event event)
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException, Exception;


	/**
	 * Used by servers to send event to other servers
	 * 
	 * @param event the transferred event
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws ServerNotActiveException
	 * @throws OutOfSyncException
	 * @throws Exception 
	 */
	void transferEvent(Event event)
			throws RemoteException, NotBoundException, ServerNotActiveException, OutOfSyncException, Exception;


	/**
	 * Called by servers when they are out of sync or in need of an updated tss
	 * state
	 * 
	 * @param id the calling server id
	 * @return
	 * @throws RemoteException
	 */
	TimedTSS watch(int id)
			throws RemoteException;


	/**
	 * Add the client to the list of client connected to this server
	 * 
	 * @param sender the id of the client
	 * @param client the client object
	 * @return the state of the game on the server
	 * @throws RemoteException
	 */
	public TSS register(long sender, ClientInterface client)
			throws RemoteException;


	/**
	 * Remove a client from the list of clients connected to this server
	 * 
	 * @param sender the id of the client
	 * @param client the client object
	 * @throws RemoteException in case of problem with RMI
	 * @see java.rmi.server.UnicastRemoteObject
	 */
	public void unregister(long sender, ClientInterface client)
			throws RemoteException;


	public void putServer(int id, String address)
			throws RemoteException;


	/**
	 * A method that does no computing.
	 */
	public void ping()
			throws RemoteException;

    public Map<Integer, String> getServerList() throws RemoteException;
}
