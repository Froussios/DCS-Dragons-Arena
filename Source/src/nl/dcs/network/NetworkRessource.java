/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.dcs.network;

import nl.dcs.network.server.ServerInterface;

import java.rmi.ConnectException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

/**
 * Class who represent anything in the network
 *
 * @author Ivanis
 */
public abstract class NetworkRessource extends UnicastRemoteObject {
    private static final long serialVersionUID = -8851690600231736075L;


    /**
     * Constructor
     *
     * @throws RemoteException
     */
    public NetworkRessource() throws RemoteException {
        super();
    }

    public ServerInterface lookup(String s) {
        try {
            return (ServerInterface) Naming.lookup(s + "SERVER");
        } catch (NotBoundException | ConnectException e) {
            System.out.println("Unable de reach " + s);
            this.getLogger().severe("Unable de reach " + s);
            e.printStackTrace();
        } catch (RemoteException e) {
            System.out.println("Unable de reach " + s);
            this.getLogger().severe("Unable de reach " + s);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("Address format incorrect (" + s + ")");
            this.getLogger().severe("Address format incorrect (" + s + ")");
            e.printStackTrace();
        }
        return null;
    }


    public Logger getLogger(){
        return Logger.getLogger(this.getClass().getName());
    }
}
