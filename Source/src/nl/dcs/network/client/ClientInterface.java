/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network.client;

import nl.dcs.da.tss.TSS;
import nl.dcs.da.tss.events.Event;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Ivanis
 */
public interface ClientInterface extends Remote{
    
    public void update (Event e) throws RemoteException;
    public void updateFromDump(TSS state) throws RemoteException;
}
