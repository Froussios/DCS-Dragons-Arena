/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class who represent anything in the network
 * @author Ivanis
 */
public abstract class NetworkRessource extends UnicastRemoteObject{
    private static final long serialVersionUID = -8851690600231736075L;
    
    
    /**
     * Constructor
     * @throws RemoteException 
     */
    public NetworkRessource () throws RemoteException{
        super();
    }
}
